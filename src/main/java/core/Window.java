package core;

import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import rendering.Framebuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Core class for the game that houses the main game loop and initializes the game itself.
 */
public class Window {

    // BASIC FIELDS
    /**
     * Memory address of GLFW window in memory space.
     */
    private long glfwWindow;

    /**
     * GamePanel instance.
     */
    private GamePanel gp;

    /**
     * Framebuffer instance.
     */
    private Framebuffer framebuffer;


    // WINDOW PROPERTIES
    /**
     * Window width.
     */
    private int width = 1280;

    /**
     * Window height.
     */
    private int height = 720;

    /**
     * Window title.
     */
    private String title = "Michael's Adventure";

    /**
     * Window background color (red component).
     */
    private float r = 1.0f;

    /**
     * Window background color (green component).
     */
    private float g = 1.0f;

    /**
     * Window background color (blue component).
     */
    private float b = 1.0f;

    /**
     * Window background color (alpha component).
     */
    private float a = 1.0f;


    // CONSTRUCTOR
    /**
     * Constructs a Window instance.
     */
    public Window() {}


    // METHODS
    /**
     * Initializes the GLFW window.
     */
    public void initWindow() {

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW.
        glfwDefaultWindowHints();                                                                                       // Enable default window hints (resizeable, default close operation, etc.).
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);                                                                       // Hide window during setup process.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);                                                                      // Enable window resizing.
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);                                                                     // Initialize window in non-maximized position.

        // Create window.
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to initialize GLFW window");
        }

        // Set window icon.
//        String filePath = "src/main/resources/miscellaneous/test_icon.png";
//        IntBuffer width = BufferUtils.createIntBuffer(1);
//        IntBuffer height = BufferUtils.createIntBuffer(1);
//        IntBuffer channels = BufferUtils.createIntBuffer(1);  // rgb or rgba.
//        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
//        glfwSetWindowIcon(glfwWindow, 1, images);

        // Listeners.
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resizeWindow(width, height);
            }
        });

        // Make OpenGL context current.
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync.
        glfwSwapInterval(1);                                                                                            // Locks FPS to interval rate (frame rate) of physical display.

        // Make window visible.
        glfwShowWindow(glfwWindow);

        // Create capabilities.
        // This is critical for LWJGL's interpolation with GLFW's OpenGL context.
        // This makes OpenGL bindings available for use.
        GL.createCapabilities();

        // Enable blending (alpha values).
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Create framebuffer.
        // TODO : Query for current monitor's actual size to pass as arguments.
        framebuffer = new Framebuffer(3840, 2160);
    }


    /**
     * Initializes a GamePanel instance and the game.
     */
    public void initGame() {

        gp = new GamePanel();
        gp.init();
    }


    /**
     * Starts the main game loop.
     */
    public void run() {

        // Initialize time tracking variables.
        double startTime = glfwGetTime();
        double endTime;
        double dt = -1.0f;

        // Core game loop.
        while (!glfwWindowShouldClose(glfwWindow)) {

            // Poll events.
            glfwPollEvents();

            // Bind framebuffer.
//            this.framebuffer.bind();

            // Prepare the frame (perhaps move into draw method OR at least in (dt >= 0) statement).
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);  // Tell OpenGL how to clear the buffer.

            // Check if a new frame must be rendered.
            if (dt >= 0) {

                // 1. UPDATE
                gp.update();

                // 2. DRAW?
            }

            // Unbind framebuffer.
//            this.framebuffer.unbind();

            // Swap buffers automatically.
            glfwSwapBuffers(glfwWindow);

            // Iterate time.
            endTime = glfwGetTime();
            dt = endTime - startTime;
            startTime = endTime;
        }

        // Free memory.
        glfwFreeCallbacks(glfwWindow);  // Free any callbacks attached to the window.
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW.
        glfwTerminate();
    }


    /**
     * Resizes the window and viewport.
     *
     * @param width new width (pixels)
     * @param height new height (pixels)
     */
    private void resizeWindow(int width, int height) {

        this.width = width;
        this.height = height;
        glfwSetWindowSize(glfwWindow, width, height);
        glViewport(0, 0, width, height);
        // https://stackoverflow.com/questions/62239485/window-resize-doesnt-effect-contents
    }
}
