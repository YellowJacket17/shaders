package rendering;

import core.GamePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the rendering of drawable objects (i.e., sending instructions to the GPU).
 */
public class Renderer {

    // FIELDS
    private final GamePanel gp;

    /**
     * Maximum number of drawables allowed per render batch.
     */
    private final int maxBatchSize = 1000;

    /**
     * List to store render batches.
     */
    private List<RenderBatch> batches;


    // CONSTRUCTOR
    /**
     * Constructs a Renderer instance.
     *
     * @param gp GamePanel instance
     */
    public Renderer(GamePanel gp) {
        this.gp = gp;
        this.batches = new ArrayList<>();
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
     * Renders all added drawables.
     */
    public void render() {

        for (RenderBatch batch : batches) {

            batch.render();
        }
    }


    /**
     * Adds a drawable to a render batch.
     *
     * @param drawable Drawable instance to add
     */
    private void addDrawableToBatch(Drawable drawable) {

        boolean added = false;

        for (RenderBatch batch : batches) {

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

            RenderBatch newBatch = new RenderBatch(gp, maxBatchSize);
            newBatch.init();
            batches.add(newBatch);
            newBatch.addDrawable(drawable);
        }
    }
}
