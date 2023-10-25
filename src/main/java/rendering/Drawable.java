package rendering;

import core.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * This class represents a game object that can be rendered to the screen.
 */
public class Drawable {

    // FIELDS
    /**
     * Drawable name.
     */
    private String name;

    /**
     * Drawable color.
     */
    private Vector4f color;

    /**
     * Stores the current position (screen position, top-left coordinate) and scale (size) of the quad that this drawable is mapped to.
     * Modifying this variable will modify the position and scale of this drawable.
     */
    public Transform transform;

    /**
     * Stores the last position (screen position) and scale (size) of the quad that this drawable is mapped to.
     * If this is different from the current position, then this drawable will be redrawn/updated.
     */
    private Transform lastTransform;

    /**
     * Drawable sprite.
     */
    private Sprite sprite;

    /**
     * Flag to check whether the position (screen position), color, or sprite of this drawable has changed since the
     * last update/frame.
     * If so, then this drawable will be redrawn/updated.
     * The initial value is set to true to force a draw on the first frame that this drawable exists on.
     */
    private boolean dirty = true;


    // CONSTRUCTORS
    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param color color of this drawable
     */
    public Drawable(String name, Vector4f color) {
        this.name = name;
        this.color = color;
        this.transform = new Transform();
        this.lastTransform = transform.copy();
        this.sprite = new Sprite(null);
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param color color of this drawable
     * @param transform position and scale of this drawable
     */
    public Drawable(String name, Vector4f color, Transform transform) {
        this.name = name;
        this.color = color;
        this.transform = transform;
        this.lastTransform = transform.copy();
        this.sprite = new Sprite(null);
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param name name of this drawable
     * @param sprite sprite of this drawable
     * @param transform position and scale of this drawable
     */
    public Drawable(String name, Transform transform, Sprite sprite) {
        this.name = name;
        this.color = new Vector4f(1, 1, 1, 1);
        this.transform = transform;
        this.lastTransform = transform.copy();
        this.sprite = sprite;
    }


    // METHOD
    /**
     * Updates the state of this drawable by one frame.
     */
    public void update() {

        if (!lastTransform.equals(transform)) {

            transform.copy(lastTransform);
            dirty = true;
        }
    }


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

    public boolean isDirty() {
        return dirty;
    }


    // SETTERS
    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            dirty = true;
        }
    }

    public void setSprite(Sprite sprite) {
        // TODO : Make like `setColor()` where it's only changed/flagged as dirty if the passed sprite is different from current.
        this.sprite = sprite;
        dirty = true;
    }

    public void setClean() {
        this.dirty = false;
    }
}
