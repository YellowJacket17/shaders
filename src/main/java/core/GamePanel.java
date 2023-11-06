package core;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.*;
import org.joml.Vector2f;
import rendering.drawable.Drawable;
import utility.AssetPool;

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
     * System camera.
     */
    private SystemCamera systemCamera;

    /**
     * Native tile size of rendered tiles.
     * Tiles are the same width and height.
     */
    private final int nativeTileSize = 32;

    /**
     * Tiles per column in the screen space.
     */
    private final int maxScreenCol = 24;

    /**
     * Tiles per row in the screen space.
     */
    private final int maxScreenRow = 14;

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

    /**
     * Tracks time for motion of game object 1.
     */
    private double motionTracker = 0;


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

        systemCamera = new SystemCamera(nativeScreenWidth, nativeScreenHeight);
        loadResources();
        initGameObjects();
    }


    /**
     * Loads and stores resources like shaders and spritesheets in memory.
     */
    private void loadResources() {

        // Shaders.
        AssetPool.getShader("/shaders/default.glsl");
        AssetPool.getShader("/shaders/rounded.glsl");
        AssetPool.getShader("/shaders/font.glsl");

        // Spritesheets.
        String filePath = "/characters/transparent.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 6, 32, 48, 0));

        filePath = "/landmarks/transparent.png";
        int[] widths = new int[] {62, 32};
        int[] heights = new int[] {90, 70};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, widths, heights, 2));
    }


    /**
     * Initializes game objects (i.e., drawable objects).
     */
    private void initGameObjects() {

        // Retrieve spritesheet.
        Spritesheet sprites = AssetPool.getSpritesheet(0);

        // Create game objects.
        Sprite sprite = sprites.getSprite(0);
        Drawable gameObject1 = new Drawable("Obj1",
                new Transform(new Vector2f(0, 0), new Vector2f(sprite.getNativeWidth(), sprite.getNativeHeight())),
                sprite);
        gameObjects.add(gameObject1);

        sprite = sprites.getSprite(3);
        Drawable gameObject2 = new Drawable("Obj2",
                new Transform(new Vector2f(32, 40), new Vector2f(sprite.getNativeWidth(), sprite.getNativeHeight())),
                sprite);
        gameObjects.add(gameObject2);
    }


    /**
     * Progresses the state of the entire game by one frame.
     */
    public void update(double dt) {

        // Keyboard input.
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            systemCamera.adjustPosition(new Vector2f(50, 20));
        }

        // Update each game object.
        // Game object 1 will move 120 units per second to the right, regardless of frame rate.
        gameObjects.get(0).transform.position.x += 120 * dt;
        for (Drawable gameObject : gameObjects) {
            gameObject.update();
        }
    }


    /**
     * Sends necessary items to the render pipeline and renders them.
     */
    public void render() {

        // Add game objects to render pipeline.
        for (Drawable gameObject : gameObjects) {
            renderer.addDrawable(gameObject);
        }

        // Add round rectangle to render pipeline.
        renderer.addRoundRectangle(new Vector4f(0, 191, 255, 180),
                new Transform(new Vector2f(5, 300), new Vector2f(120, 60)), 20);

        // Add square rectangle to render pipeline.
        renderer.addRectangle(new Vector4f(0, 255, 0, 100),
                new Transform(new Vector2f(150, 90), new Vector2f(200, 75)));

        // Add text to render pipeline.
        renderer.addString("Hello, World! g p y", 0, 0, 0.5f, new Vector3f(0, 0, 0), "Arimo");
        renderer.addString("Have a great day?", 0, 90, 0.3f, new Vector3f(0, 0, 0), "Arimo Bold");
        renderer.addString("Yes indeed.", 20, 130, 0.5f, new Vector3f(200, 143, 15), "Arimo");

        // Render everything in pipeline.
        renderer.render();
    }


    // GETTERS
    public SystemCamera getSystemCamera() {
        return systemCamera;
    }
}
