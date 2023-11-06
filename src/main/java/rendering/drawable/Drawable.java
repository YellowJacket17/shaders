package rendering.drawable;

import core.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Sprite;
import rendering.Texture;

/**
 * This class represents a game object that can be rendered to the screen.
 */
public class Drawable {

    // FIELDS
    /**
     * Drawable name.
     */
    private final String name;

    /**
     * Drawable color (r, g, b, a).
     */
    private final Vector4f color;

    /**
     * Stores the current screen position (top-left coordinate) and scale (width and height) of the quad that this
     * drawable is mapped to.
     * Modifying this variable will directly affect the position and scale of this drawable.
     */
    public final Transform transform;

    /**
     * Active drawable sprite.
     */
    private Sprite sprite;


    // CONSTRUCTORS
    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param sprite sprite this drawable
     */
    public Drawable(String name, Sprite sprite) {
        this.name = name;
        this.color = new Vector4f(255, 255, 255, 255);
        this.transform = new Transform();
        this.sprite = sprite;
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param color color of this drawable (r, g, b, a)
     * @param transform screen position (top-left coordinate) and scale (width and height) of this drawable
     */
    public Drawable(String name, Transform transform, Vector4f color) {
        this.name = name;
        this.color = color;
        this.transform = transform;
        this.sprite = new Sprite();
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param sprite sprite of this drawable
     * @param transform screen position (top-left coordinate) and scale (width and height) of this drawable
     */
    public Drawable(String name, Transform transform, Sprite sprite) {
        this.name = name;
        this.color = new Vector4f(255, 255, 255, 255);
        this.transform = transform;
        this.sprite = sprite;
    }


    // METHOD
    /**
     * Updates the state of this drawable by one frame.
     */
    public void update() {}


    // GETTERS
    public String getName() {
        return name;
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords() {
        return sprite.getTextureCoords();
    }


    // SETTERS
    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
        }
    }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite)) {
            this.sprite = sprite;
        }
    }
}
