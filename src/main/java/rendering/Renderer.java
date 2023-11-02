package rendering;

import core.GamePanel;
import core.Transform;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.drawable.Drawable;
import rendering.drawable.DrawableBatch;
import rendering.drawable.DrawableSingle;
import rendering.font.CFont;
import rendering.font.FontBatch;
import rendering.font.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class manages the rendering of drawable objects (i.e., sending instructions to the GPU).
 */
public class Renderer {

    // FIELDS
    private final GamePanel gp;

    /**
     * List to store batches of drawables to render.
     */
    private final ArrayList<DrawableBatch> drawableBatches = new ArrayList<>();

    /**
     * List to store single drawables to render.
     */
    private final ArrayList<DrawableSingle> drawableSingles = new ArrayList<>();

    /**
     * Map to store font batches to render; font name is the key, font batch is the value.
     */
    private final HashMap<String, FontBatch> fontBatches = new HashMap<>();

    /**
     * List to store staged text to render.
     */
    private final HashMap<String, ArrayList<Text>> stagedText = new HashMap<>();

    /**
     * Map to store loaded fonts; font name is the key, font is the value.
     */
    private final HashMap<String, CFont> fonts = new HashMap<>();


    // CONSTRUCTOR
    /**
     * Constructs a Renderer instance.
     *
     * @param gp GamePanel instance
     */
    public Renderer(GamePanel gp) {
        this.gp = gp;
        initializeFonts();
    }


    // METHODS
    /**
     * Renders all added drawables and fonts.
     */
    public void render() {

        for (DrawableBatch batch : drawableBatches) {

            batch.render();
        }

        for (DrawableSingle single : drawableSingles) {

            single.render();
        }

        for (String font : stagedText.keySet()) {                                                                       // Loop through each type of font stored in the staged text.

            for (Text text : stagedText.get(font)) {                                                                    // Render all text of the current font.

                fontBatches.get(font).addString(text.getText(), text.getScreenX(), text.getScreenY(), text.getScale(), text.getColor());
            }
            fontBatches.get(font).flush();                                                                              // Must flush at the end to actually render entire batch.
            stagedText.get(font).clear();                                                                               // Remove all staged text of the current font as it has already been rendered.
        }
    }


    /**
     * Adds a drawable to the render pipeline.
     *
     * @param drawable Drawable instance to add
     */
    public void addDrawable(Drawable drawable) {

        if (drawable != null) {

            addDrawableToBatch(drawable);
        }
    }


    /**
     * Adds a string of characters to the render pipeline.
     *
     * @param text text to add
     * @param screenX x-coordinate (leftmost)
     * @param screenY y-coordinate (topmost)
     * @param scale scale factor compared to native font size
     * @param color color (r, g, b)
     * @param font name of font to use
     */
    public void addString(String text, int screenX, int screenY, float scale, Vector3f color, String font) {

        if (stagedText.get(font) == null) {                                                                             // Check if any text with this font has already been processed.

            stagedText.put(font, new ArrayList<>());                                                                    // Create a new list of staged text for this new font.
            FontBatch newBatch = new FontBatch(gp);
            newBatch.init();
            newBatch.setFont(fonts.get(font));
            fontBatches.put(font, newBatch);                                                                            // Create a new batch for this new font.
        }
        stagedText.get(font).add(new Text(text, screenX, screenY, scale, color, font));
    }


    /**
     * Adds a rectangle with square corners to the render pipeline.
     *
     * @param color color of this rectangle (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of this rectangle
     */
    public void addRectangle(Vector4f color, Transform transform) {

        Drawable rectangle = new Drawable("auto-generated-rectangle", color, transform);
        addDrawableToBatch(rectangle);
    }


    /**
     * Adds a rectangle with round corners to the render pipeline.
     *
     * @param color color of this rectangle (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of this rectangle
     * @param radius arc radius at four corners of this rectangle
     */
    public void addRoundRectangle(Vector4f color, Transform transform, int radius) {

        Drawable rectangle = new Drawable("auto-generated-round-rectangle", color, transform);
        addDrawableToSingle(rectangle, radius);
    }


    /**
     * Adds a single drawable to render.
     *
     * @param drawable drawable to add
     * @param radius arc radius at four corners of quad
     */
    public void addDrawableToSingle(Drawable drawable, int radius) {

        DrawableSingle single = new DrawableSingle(gp);
        single.init();
        single.setDrawable(drawable);
        single.setRadius(radius);
        drawableSingles.add(single);
    }


    /**
     * Adds a drawable to a batch to render.
     *
     * @param drawable drawable to add
     */
    private void addDrawableToBatch(Drawable drawable) {

        boolean added = false;

        for (DrawableBatch batch : drawableBatches) {

            if (batch.hasRoom()) {

                Texture texture = drawable.getTexture();

                if ((texture == null) || (batch.hasTexture(texture) || batch.hasTextureRoom())) {

                    batch.addDrawable(drawable);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {

            DrawableBatch newBatch = new DrawableBatch(gp);
            newBatch.init();
            drawableBatches.add(newBatch);
            newBatch.addDrawable(drawable);
        }
    }


    /**
     * Loads available fonts.
     */
    private void initializeFonts() {

        CFont font = new CFont("/fonts/Arimo-mO92.ttf", 128);
        fonts.put(font.getName(), font);

        font = new CFont("/fonts/ArimoBold-dVDx.ttf", 128);
        fonts.put(font.getName(), font);
    }
}
