package rendering.drawable;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Shader;
import rendering.Texture;
import utility.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class holds a batch of drawables to be sent to the GPU and rendered in a single call.
 */
public class DrawableBatch {

    /*
     * Vertex in Vertex Array
     * ======================
     * Position         Color                          Texture coordinates     Texture ID
     * float, float,    float, float, float, float,    float, float,           float
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Defines two position floats in the vertex array for each vertex.
     */
    private final int positionSize = 2;

    /**
     * Defines four color floats in the vertex array for each vertex.
     */
    private final int colorSize = 4;

    /**
     * Defines two texture coordinate floats in the vertex array for each vertex.
     */
    private final int textureCoordsSize = 2;

    /**
     * Defines one texture ID float in the vertex array for each vertex.
     */
    private final int textureIdSize = 1;

    /**
     * Defines the offset (in bytes) of the start of the position floats in the vertex array for each vertex.
     * Here, the position starts at the beginning of a vertex definition, so it has zero offset.
     */
    private final int positionOffset = 0;

    /**
     * Defines the offset (in bytes) of the start of the color floats in the vertex array for each vertex.
     * Here, the color starts after the position in a vertex definition, so it has an offset determined by the position.
     */
    private final int colorOffset = positionOffset + positionSize * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture coordinate floats in the vertex array for each vertex.
     * Here, the texture coordinates start after the color in a vertex definition, so it has an offset determined by the
     * color.
     */
    private final int textureCoordsOffset = colorOffset + colorSize * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture ID float in the vertex array for each vertex.
     * Here, the texture ID starts after the texture coordinates in a vertex definition, so it has an offset determined
     * by the texture coordinates.
     */
    private final int textureIdOffset = textureCoordsOffset + textureCoordsSize * Float.BYTES;

    /**
     * Maximum number of drawables that can be added to this batch.
     */
    private final int maxBatchSize = 1000;

    /**
     * Actual number of drawables added to this batch (array of drawables) thus far.
     */
    private int numDrawables;

    /**
     * Array to store drawables that will be rendered with this batch.
     */
    private final Drawable[] drawables = new Drawable[maxBatchSize];

    /**
     * Boolean indicating whether any more drawables can be added to this batch.
     */
    private boolean hasRoom = true;

    /**
     * Total number of floats in each vertex of the vertex array.
     * Remember that a vertex represents a corner of a quad being rendered in this case.
     */
    private final int vertexSize = 9;

    /**
     * Vertex array.
     * Note that there are four vertices per quad.
     * We would like a number of drawables equal to the maximum batch size, hence the multiplication by four.
     * Each drawable to render requires a quad.
     */
    private final float[] vertices = new float[maxBatchSize * 4 * vertexSize];

    /**
     * Vertex array object ID.
     */
    private int vaoId;

    /**
     * Vertex buffer object ID.
     */
    private int vboId;

    /**
     * Slots available to bind textures for sampling during a draw in this batch.
     * Here, the number available is limited eight textures to ensure that lower-end GPUs are supported, even though
     * OpenGL specifies a minimum of 16 available slots.
     * This will index into the appropriate texture from the textures list.
     * In other words, this correlates directly to the textures in the textures list being bound in the GPU.
     * Note that slot zero is reserved for the empty texture.
     */
    private final int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    // TODO : Change textures to be a LimitedArrayList with maximum size of eight.
    /**
     * List to store the textures available in this batch.
     * As a reminder, a texture is an entire spritesheet, while a sprite is a section of a spritesheet (i.e., texture).
     */
    private final ArrayList<Texture> textures = new ArrayList<>();

    /**
     * Shader attached to this batch.
     */
    private final Shader shader;


    // CONSTRUCTOR
    /**
     * Constructs a BatchRenderer instance.
     *
     * @param gp GamePanel instance
     */
    public DrawableBatch(GamePanel gp) {
        this.gp = gp;
        this.shader = AssetPool.getShader("/shaders/default.glsl");
        init();
    }


    // METHODS
    /**
     * Renders this batch then clears it of all drawables.
     */
    public void flush() {

        render();
        clear();
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
     * Renders all drawables in this batch.
     */
    private void render() {

        // Rebuffer all data.
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Bind shader program.
        shader.use();

        // Camera.
        shader.uploadMat4f("uProjection", gp.getSystemCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", gp.getSystemCamera().getViewMatrix());

        // Bind textures.
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);                                                                       // Activate texture in appropriate slot; slot 0 is reserved for the empty texture.
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);                                                               // Use multiple textures in shader (up to seven plus the empty texture).

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
        glBindVertexArray(0);                                                                                           // 0 is a flag that states to bind nothing.
        shader.detach();                                                                                                // Detach shader program.
        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
    }


    /**
     * Clears this batch of all drawables, resetting it to its default initialized state.
     */
    private void clear() {

        Arrays.fill(vertices, 0);
        Arrays.fill(drawables, null);
        numDrawables = 0;
        textures.clear();
        hasRoom = true;
    }


    /**
     * Initializes this batch.
     * All necessary data is created on the GPU.
     * In other words, space is allocated on the GPU.
     */
    private void init() {

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
        int stride = vertexSize * Float.BYTES;                                                                          // Size of the vertex array in bytes.
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, stride, positionOffset);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, stride, colorOffset);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, textureCoordsSize, GL_FLOAT, false, stride, textureCoordsOffset);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, textureIdSize, GL_FLOAT, false, stride, textureIdOffset);
        glEnableVertexAttribArray(3);
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
     * Loads the vertex properties of the specified drawable.
     *
     * @param index index of target drawables in the list of drawables to be rendered with this batch
     */
    private void loadVertexProperties(int index) {

        Drawable drawable = drawables[index];

        // Find offset within array (4 vertices per drawable).
        int offset = index * 4 * vertexSize;

        // Color.
        Vector4f color = drawable.getColor();

        // Texture.
        Vector2f[] textureCoords = drawable.getTextureCoords();
        int textureId = 0;
        if (drawable.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {                                                                 // Find the texture ID; loop through all textures in this batch until we find a match, then assign corresponding ID.
                if (textures.get(i).equals(drawable.getTexture())) {                                                    // Use `.equals()` because == only compares memory address of two objects, not actual contents.
                    textureId = i + 1;                                                                                  // Texture slot 0 is reserved for no bound texture to sprite, hence why 1 is added.
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
            vertices[offset + 2] = color.x / 255;                                                                       // Red information.
            vertices[offset + 3] = color.y / 255;                                                                       // Green information.
            vertices[offset + 4] = color.z / 255;                                                                       // Blue information.
            vertices[offset + 5] = color.w / 255;                                                                       // Alpha information.

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
    public boolean hasDrawable() {
        return numDrawables > 0;
    }

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
