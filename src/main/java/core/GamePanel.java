package core;

import rendering.*;
import org.joml.Vector2f;
import rendering.drawable.Drawable;
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


    // SCREEN SETTINGS
    /**
     * Game camera.
     */
    private Camera camera;

    /**
     * Native tile size of rendered tiles.
     * Tiles are the same width and height.
     */
    private final int nativeTileSize = 32;

    /**
     * Tiles per column in the screen space.
     */
    private final int maxScreenCol = 24; //40

    /**
     * Tiles per row in the screen space.
     */
    private final int maxScreenRow = 14; //21

    /**
     * Native screen width as determined by the native tile size and number of columns.
     */
    private final int nativeScreenWidth = nativeTileSize * maxScreenCol;

    /**
     * Native screen height as determined by the native tile size and number of rows.
     */
    private final int nativeScreenHeight = nativeTileSize * maxScreenRow;


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

        camera = new Camera(nativeScreenWidth, nativeScreenHeight, new Vector2f());
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

        renderer.addStringToBatch("Hello, World! g p", 0, 0, 0.5f, 0xFF01BB, "Arimo");

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
