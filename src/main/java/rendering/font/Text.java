package rendering.font;

/**
 * This class represent text to be rendered.
 */
public class Text {

    // FIELDS
    /**
     * Text contents.
     */
    private final String text;

    /**
     * X-coordinate (leftmost) of this text.
     */
    private final int screenX;

    /**
     * Y-coordinate (topmost) of this text.
     */
    private final int screenY;

    /**
     * Scale factor compared to native font size.
     */
    private final float scale;

    /**
     * Color in hexadecimal format.
     */
    private final int rgb;

    /**
     * Font name.
     */
    private final String font;


    // CONSTRUCTOR
    /**
     * Constructs a Text instance
     *
     * @param text text contents
     * @param screenX x-coordinate (leftmost) of text
     * @param screenY y-coordinate (topmost) of text
     * @param scale sale factor compared to native font size
     * @param rgb color in hexadecimal format
     * @param font font name
     */
    public Text(String text, int screenX, int screenY, float scale, int rgb, String font) {
        this.text = text;
        this.screenX = screenX;
        this.screenY = screenY;
        this.scale = scale;
        this.rgb = rgb;
        this.font = font;
    }


    // GETTERS
    public String getText() {
        return text;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public float getScale() {
        return scale;
    }

    public int getRgb() {
        return rgb;
    }

    public String getFont() {
        return font;
    }
}
