package rendering;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utilities.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class holds a batch of Drawable instances to be sent to the GPU and rendered in a single call.
 */
public class RenderBatch {

    /*
     * Vertex array
     * ============
     * Position         Color                          Texture coordinates     Texture ID
     * float, float,    float, float, float, float,    float, float, float,    float
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Defines two position indices in the vertex array.
     */
    private final int positionSize = 2;

    /**
     * Defines four color indices in the vertex array.
     */
    private final int colorSize = 4;

    /**
     * Defines two texture coordinate indices in the vertex array.
     */
    private final int textureCoordsSize = 2;

    /**
     * Defines one texture ID index in the vertex array.
     */
    private final int textureIdSize = 1;

    /**
     * Defines the offset (in bytes) of the start of the position indices in the vertex array.
     * Here, the position starts at the beginning of the vertex array, so it has zero offset.
     */
    private final int positionOffset = 0;

    /**
     * Defines the offset (in bytes) of the start of the color indices in the vertex array.
     * Here, the color starts after the position in the vertex array, so it has an offset determined by the position.
     */
    private final int colorOffset = positionOffset + positionSize * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture coordinate indices in the vertex array.
     * Here, the texture coordinates start after the color in the vertex array, so it has an offset determined by the
     * color.
     */
    private final int textureCoordsOffset = colorOffset + colorSize * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture ID index in the vertex array.
     * Here, the texture ID starts after the texture coordinates in the vertex array, so it has an offset determined by
     * the texture coordinates.
     */
    private final int textureIdOffset = textureCoordsOffset + textureCoordsSize * Float.BYTES;

    /**
     * Total number of indices in the vertex array.
     */
    private final int vertexSize = 9;

    /**
     * Size of the vertex array in bytes.
     */
    private final int vertexSizeBytes = vertexSize * Float.BYTES;

    /**
     * Array to store drawables that will be rendered with this batch.
     */
    private Drawable[] drawables;

    /**
     * Number of drawables added to the array of drawables thus far.
     */
    private int numDrawables;

    /**
     * Boolean indicating whether any more drawables can be added to this batch.
     */
    private boolean hasRoom;

    /**
     * Vertex array.
     */
    private float[] vertices;

    /**
     * Slots available to store textures in this batch.
     */
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    /**
     * List to store the textures attached to this batch.
     */
    private List<Texture> textures;

    /**
     * Vertex array object ID.
     */
    private int vaoId;

    /**
     * Vertex buffer object ID.
     */
    private int vboId;

    /**
     * Maximum number of drawables that can be added to this batch.
     */
    private int maxBatchSize;

    /**
     * Shader attached to this batch.
     */
    private Shader shader;


    // CONSTRUCTOR
    /**
     * Constructs a BatchRenderer instance.
     *
     * @param gp GamePanel instance
     * @param maxBatchSize maximum number of drawables allowed in this batch
     */
    public RenderBatch(GamePanel gp, int maxBatchSize) {
        this.gp = gp;
        this.shader = AssetPool.getShader("/shaders/default.glsl");
        this.drawables = new Drawable[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        this.vertices = new float[maxBatchSize * 4 * vertexSize]; // 4 vertices per quad
        this.numDrawables = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }


    // METHODS
    /**
     * Initializes this batch.
     * All necessary data is created on the GPU.
     * In other words, space is allocated on the GPU.
     */
    public void init() {

        // Generate and bind a vertex array object.
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Allocate space for vertices.
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer.
        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable buffer attribute pointers.
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, positionOffset);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, colorOffset);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, textureCoordsSize, GL_FLOAT, false, vertexSizeBytes, textureCoordsOffset);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, textureIdSize, GL_FLOAT, false, vertexSizeBytes, textureIdOffset);
        glEnableVertexAttribArray(3);
    }


    /**
     * Renders all drawables in this batch.
     */
    public void render() {

        // Update each Drawable with new position, color, and texture if applicable.
        boolean rebufferData = false;
        for (int i = 0; i < numDrawables; i++) {

            Drawable drawable = drawables[i];

            if (drawable.isDirty()) {

                loadVertexProperties(i);
                drawable.setClean();
                rebufferData = true;
            }
        }

        // Rebuffer all data if anything has changed since last render operation.
        if (rebufferData) {

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Bind shader program.
        shader.use();

        // Camera.
        shader.uploadMat4f("uProjection", gp.getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", gp.getCamera().getViewMatrix());

        // Bind texture.
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);  // Activate texture in appropriate slot; slot zero is reserved for no texture.
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        // Bind VAO being used.
        glBindVertexArray(vaoId);

        // Enable vertex attribute pointers.
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw.
        glDrawElements(GL_TRIANGLES, (numDrawables * 6), GL_UNSIGNED_INT, 0);

        // Unbind after drawing.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);    // 0 is a flag that states to bind nothing.
        shader.detach();  // Detach shader program.
        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
    }


    /**
     * Adds a drawable to this batch.
     *
     * @param drawable Drawable instance to add
     */
    public void addDrawable(Drawable drawable) {

        // Get index and add render object.
        int index = numDrawables;
        drawables[index] = drawable;
        numDrawables++;

        // Check if drawable has texture; if so, add to list if not already loaded.
        if (drawable.getTexture() != null) {
            if (!textures.contains(drawable.getTexture())) {
                textures.add(drawable.getTexture());
            }
        }

        // Add properties to local vertices array.
        loadVertexProperties(index);

        // Check if batch has run out of room.
        if (numDrawables >= maxBatchSize) {
            hasRoom = false;
        }
    }


    /**
     * Generates indices for all quads in this batch.
     * The number of quads generated is determined by the maximum batch size.
     *
     * @return indices
     */
    private int[] generateIndices() {

        int[] elements = new int[6 * maxBatchSize];                                                                     // 6 indices per quad (3 per triangle).

        for (int i = 0; i < maxBatchSize; i++) {

            loadElementIndices(elements, i);
        }
        return elements;
    }


    /**
     * Loads the indices for a single quad in an array of indices.
     *
     * @param elements array of indices that contains the target quad
     * @param index first index of the target quad in the array of indices (each quad is composed of six sequential
     *              indices, since each quad is made of two triangles)
     */
    private void loadElementIndices(int[] elements, int index) {

        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Triangle 1.
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2.
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }


    /**
     * Loads the vertex properties of the specified Drawable instance.
     *
     * @param index index of target drawables in the list of drawables to be rendered with this batch
     */
    private void loadVertexProperties(int index) {

        Drawable drawable = drawables[index];

        // Find offset within array (4 vertices per sprite).
        int offset = index * 4 * vertexSize;

        // Color.
        Vector4f color = drawable.getColor();

        // Texture.
        Vector2f[] textureCoords = drawable.getTextureCoords();
        int textureId = 0;
        if (drawable.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(drawable.getTexture())) {                                                    // Use `.equals()` because == only compares memory address of two objects, not actual contents.
                    textureId = i + 1;                                                                                  // Texture slot 0 is reserved for no bound texture to sprite.
                    break;
                }
            }
        }

        // Add vertices with appropriate properties.
        // This assumes positioning everything according to bottom-left corner.
        // Add clockwise, starting from top-right.
        // *    *
        // *    *
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Load position.
            vertices[offset] = drawable.transform.position.x + (xAdd * drawable.transform.scale.x);
            vertices[offset + 1] = drawable.transform.position.y + (yAdd * drawable.transform.scale.y);

            // Load color.
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates.
            vertices[offset + 6] = textureCoords[i].x;
            vertices[offset + 7] = textureCoords[i].y;

            // Load texture ID.
            vertices[offset + 8] = textureId;

            // Increment.
            offset += vertexSize;
        }
    }


    // GETTERS
    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasTextureRoom() {
        return textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }
}
