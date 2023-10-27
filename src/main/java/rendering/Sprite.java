package rendering;

import org.joml.Vector2f;

/**
 * This class represents an individual sprite extracted from a spritesheet.
 */
public class Sprite {

    // FIELDS
    /**
     * Parent texture of sprite.
     */
    private Texture texture;

    /**
     * Coordinates of sprite on parent texture.
     * Note that texture coordinates are normalized from zero to one, where (0, 0) is the bottom-left corner of the
     * texture and (1, 1) is the top-right corner.
     */
    private Vector2f[] textureCoords;

    /**
     * Native sprite width.
     */
    private int nativeWidth;

    /**
     * Native sprite height.
     */
    private int nativeHeight;


    // CONSTRUCTORS
    /**
     * Constructs a Sprite instance.
     *
     * @param texture parent texture of sprite
     */
    public Sprite(Texture texture) {
        this.texture = texture;
        Vector2f[] textureCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
        this.textureCoords = textureCoords;
    }


    /**
     * Constructs a Sprite instance.
     *
     * @param texture parent texture of sprite
     * @param textureCoords coordinates of sprite on parent texture
     * @param spriteWidth native sprite width
     * @param spriteHeight native sprite height
     */
    public Sprite(Texture texture, Vector2f[] textureCoords, int spriteWidth, int spriteHeight) {
        this.texture = texture;
        this.textureCoords = textureCoords;
        this.nativeWidth = spriteWidth;
        this.nativeHeight = spriteHeight;
    }


    // GETTERS
    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

    public int getNativeWidth() {
        return nativeWidth;
    }

    public int getNativeHeight() {
        return nativeHeight;
    }
}
