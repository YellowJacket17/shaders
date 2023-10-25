package core;

import rendering.*;
import org.joml.Vector2f;
import utilities.AssetPool;

import java.util.ArrayList;

/**
 * Primary class for managing game logic that houses essential functions and configurations.
 */
public class GamePanel  {

    // RENDERER
    /**
     * Game renderer.
     */
    private final Renderer renderer = new Renderer(this);

    /**
     * Game camera.
     */
    private final Camera camera = new Camera(new Vector2f());


    // GAME OBJECTS
    /**
     * List to store ame objects (i.e., drawable objects).
     */
    private final ArrayList<Drawable> gameObjects = new ArrayList<>();
    private Drawable gameObject1;
    private Drawable gameObject2;


    // CONSTRUCTOR
    /**
     * Constructs a GamePanel instance.
     */
    public GamePanel() {}


    // METHODS
    /**
     * Initializes the game.
     */
    public void init() {

        loadResources();
        initGameObjects();
    }


    /**
     * Loads and stores resources like shaders and spritesheets in memory.
     */
    private void loadResources() {

        AssetPool.getShader("/shaders/default.glsl");

        // TODO : Make everything a relative path from the resources folder!

        String spritesheetPath = "src/main/resources/characters/spritesheets/transparent.png";
        AssetPool.addSpritesheet(spritesheetPath,
                new Spritesheet(AssetPool.getTexture(spritesheetPath), 6, 32, 48, 0));
    }


    /**
     * Initializes game objects (i.e., drawable objects).
     */
    private void initGameObjects() {

        // Retrieve spritesheet.
        Spritesheet sprites = AssetPool.getSpritesheet("src/main/resources/characters/spritesheets/transparent.png");

        // Create game objects.
        Sprite sprite = sprites.getSprite(0);
        gameObject1 = new Drawable("Obj1",
                new Transform(new Vector2f(0, 0), new Vector2f(sprite.getNativeWidth(), sprite.getNativeHeight())),
                sprite);
        gameObjects.add(gameObject1);

        sprite = sprites.getSprite(3);
        gameObject2 = new Drawable("Obj2",
                new Transform(new Vector2f(32, 40), new Vector2f(sprite.getNativeWidth(), sprite.getNativeHeight())),
                sprite);
        gameObjects.add(gameObject2);

        // Add game objects to renderer.
        int i = 1;
        for (Drawable gameObject : gameObjects) {
            System.out.println("Adding " + i);
            i++;
            renderer.addDrawable(gameObject);
        }
        System.out.println("Finished");
    }


    /**
     * Progresses the state of the entire game by one frame.
     */
    public void update() {

        // Print FPS.
//        System.out.println((1.0 / dt) + " FPS");

        gameObject1.transform.position.x += 2;

        camera.adjustProjection();

        // Update each game object.
        for (Drawable gameObject : gameObjects) {
            gameObject.update();
        }
        renderer.render();
    }


    // GETTERS
    public Camera getCamera() {
        return camera;
    }
}
