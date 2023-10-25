package rendering;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * This class defines a texture to be bound to a drawn object.
 */
public class Texture {

    // FIELDS
    /**
     * Texture file path.
     */
    private final String filePath;

    /**
     * Texture ID.
     */
    private final int textureId;

    /**
     * Native texture width.
     */
    private final int nativeWidth;

    /**
     * Native texture height.
     */
    private final int nativeHeight;


    // CONSTRUCTORS
    /**
     * Constructs a Texture instance.
     * The texture at the provided file path is loaded and prepared upon construction.
     *
     * @param filePath file path of texture from resources directory
     */
    public Texture(String filePath) {

        this.filePath = filePath;

        // Generate texture on GPU.
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Parameter: repeat image in both directions.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Parameter: pixelate when stretching.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Parameter: pixelate when shrinking.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Load image.
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);  // rgb or rgba.
//        stbi_set_flip_vertically_on_load(true);  // Load image upside-down.
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
        if (image != null) {
            if (channels.get(0) == 3) {                                                                                 // rbg image.
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {                                                                          // rgba image.
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                // TODO : Throw new AssetLoadException in actual program here.
                throw new RuntimeException("Unexpected number of channels (" + channels.get(0)
                        + ") in image for texture loaded from " + filePath);
            }
            nativeWidth = width.get(0);
            nativeHeight = height.get(0);
        } else {
            // TODO : Throw new AssetLoadException in actual program here.
            throw new RuntimeException("Failed to load image for texture from " + filePath);
        }
        stbi_image_free(image);                                                                                         // Free memory, since image has now been uploaded to GPU.
    }


    /**
     * Constructs a Texture instance.
     * An empty texture is prepared upon construction.
     *
     * @param width texture width
     * @param height texture height
     */
    public Texture(int width, int height) {

        this.filePath = "generated";

        // Generate texture on GPU.
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Allocate space for empty image.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        nativeWidth = width;
        nativeHeight = height;
    }


    // METHODS
    /**
     * Binds this texture to be used when drawing.
     */
    public void bind() {

        glBindTexture(GL_TEXTURE_2D, textureId);
    }


    /**
     * Unbinds this texture when finished being used.
     */
    public void unbind() {

        glBindTexture(GL_TEXTURE_2D, 0);
    }


    // GETTERS
    public String getFilePath() {
        return filePath;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getNativeWidth() {
        return nativeWidth;
    }

    public int getNativeHeight() {
        return nativeHeight;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Texture)) {
            return false;
        }
        Texture oTex = (Texture)o;
        return (oTex.getNativeWidth() == this.nativeWidth)
                && (oTex.getNativeHeight() == this.nativeHeight)
                && (oTex.getTextureId() == this.textureId)
                && (oTex.getFilePath().equals(this.filePath));
    }
}
