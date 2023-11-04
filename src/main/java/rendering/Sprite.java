package rendering;

import org.joml.Vector2f;

/**
 * This class represents an individual sprite extracted from a spritesheet.
 */
public class Sprite {

    // FIELDS
    /**
     * Parent texture of this sprite.
     */
    private Texture texture;

    /**
     * Coordinates of sprite on parent texture.
     * Note that texture coordinates are normalized from zero to one, where (0, 0) is the bottom-left corner of the
     * texture and (1, 1) is the top-right corner.
     */
    private final Vector2f[] textureCoords;

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
     * Constructs a null Sprite instance.
     */
    public Sprite() {
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
     * @param texture parent texture of sprite (sprite will encompass entire texture)
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
        this.nativeWidth = texture.getNativeWidth();
        this.nativeHeight = texture.getNativeHeight();
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


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Sprite)) {
            return false;
        }
        Sprite oSprite = (Sprite)o;
        return (oSprite.getTexture().equals(this.texture))
                && (oSprite.getTextureCoords().equals(this.textureCoords))
                && (oSprite.getNativeWidth() == this.nativeWidth)
                && (oSprite.getNativeHeight() == this.nativeHeight);
    }
}
