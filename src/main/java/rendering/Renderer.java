package rendering;

import core.GamePanel;
import rendering.drawable.Drawable;
import rendering.drawable.DrawableBatch;
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
     * List to store drawable batches to render.
     */
    private final ArrayList<DrawableBatch> drawableBatches  = new ArrayList<>();

    /**
     * List to store font batches to render.
     */
    private final FontBatch fontBatch;

    /**
     * List to store staged text to render.
     */
    private final ArrayList<Text> stagedText = new ArrayList<>();

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
        this.fontBatch = new FontBatch(gp);
        this.fontBatch.init();
        initializeFonts();
    }


    // METHODS
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
     * Renders all added drawables and fonts.
     */
    public void render() {

        for (DrawableBatch batch : drawableBatches) {

            batch.render();
        }

        for (Text text : stagedText) {

            fontBatch.setFont(fonts.get(text.getFont()));
            fontBatch.addString(text.getText(), text.getScreenX(), text.getScreenY(), text.getScale(), text.getRgb());
            fontBatch.flush();                                                                                          // Must flush at the end of the frame to actually render entire batch.
        }
        stagedText.clear();                                                                                             // Remove all staged text as it has already been rendered.
    }


    /**
     * Adds a drawable to a render batch.
     *
     * @param drawable Drawable instance to add
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
     * Adds a string of characters to the render pipeline.
     *
     * @param text text to add
     * @param screenX x-coordinate (leftmost)
     * @param screenY y-coordinate (topmost)
     * @param scale scale factor compared to native font size
     * @param rgb color in hexadecimal format; 0x00000000 produces black text with transparent background
     * @param font name of font to use
     */
    public void addStringToBatch(String text, int screenX, int screenY, float scale, int rgb, String font) {

        stagedText.add(new Text(text, screenX, screenY, scale, rgb, font));
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
