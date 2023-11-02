package rendering;

import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * This class represent a spritesheet that contains many sprites to be extracted.
 * A spritesheet is an parent entire texture, while a sprite is a section of a spritesheet (i.e., texture).
 */
public class Spritesheet {

    // FIELDS
    /**
     * Parent texture of this spritesheet.
     */
    private final Texture texture;

    /**
     * List to store all sprites derived from this spritesheet.
     */
    private final ArrayList<Sprite> sprites = new ArrayList<>();;


    // CONSTRUCTOR
    /**
     * Constructs a Spritesheet instance.
     * Sprites are derived from this spritesheet upon construction.
     *
     * @param texture parent texture of this spritesheet to derive sprites from
     * @param numSprites number of sprites contained within this spritesheet
     * @param spriteWidth native width of each sprite in this spritesheet
     * @param spriteHeight native height of each sprite in this spritesheet
     * @param spacing native spacing between each sprite in this spritesheet
     */
    public Spritesheet(Texture texture, int numSprites, int spriteWidth, int spriteHeight, int spacing) {

        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getNativeHeight() - spriteHeight;

        for (int i = 0; i < numSprites; i++) {

            // Normalize sprite coordinates on parent texture;
            float topY = (currentY + spriteHeight) / (float)texture.getNativeHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getNativeWidth();
            float leftX = currentX / (float)texture.getNativeWidth();
            float bottomY = currentY / (float)texture.getNativeHeight();

            // Store coordinates of sprite on parent texture.
            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            // Create sprite using coordinates on parent texture.
            Sprite sprite = new Sprite(texture, textureCoords, spriteWidth, spriteHeight);
            sprites.add(sprite);

            // Iterate to next sprite in parent texture.
            currentX += spriteWidth + spacing;
            if (currentX >= texture.getNativeWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }


    // GETTERS
    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Spritesheet)) {
            return false;
        }
        Spritesheet oSpritesheet = (Spritesheet)o;
        return oSpritesheet.getTexture().equals(this.texture);
    }
}
