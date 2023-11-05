package core;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import rendering.Framebuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;

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
     * Maximum permitted frame rate.
     */
    private int frameRateCap = 60;


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
        loadWindowIcon("/miscellaneous/test_icon.png");

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
        glfwSwapInterval(1);                                                                                            // Lock frames per second to interval rate (frame rate) of physical display.

        // Make window visible.
        glfwShowWindow(glfwWindow);

        // Create capabilities.
        // This is critical for LWJGL's interpolation with GLFW's OpenGL context.
        // This makes OpenGL bindings available for use.
        GL.createCapabilities();

        // Enable blending (alpha values).
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
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

        // Initialize variables for tracking time.
        // Note that `glfwGetTime()` returns time elapsed since GLFW was initialized.
        double endLoopTime = glfwGetTime();                                                                             // Time at the end of a complete loop (used for limiting frame rate).
        double startFrameTime = glfwGetTime();                                                                          // Time at the start of a frame.
        double endFrameTime = 0;                                                                                        // Time at the end of a frame.
        double dt = 0;                                                                                                  // Time between each rendered frame (frame pacing).

        // Core game loop.
        while (!glfwWindowShouldClose(glfwWindow)) {

            // Prepare the frame.
            glClearColor(r, g, b, a);                                                                                   // Set window clear color.
            glClear(GL_COLOR_BUFFER_BIT);                                                                               // Tell OpenGL how to clear the buffer.

            // Poll, update, and render.
            glfwPollEvents();                                                                                           // Poll user input (keyboard, gamepad, etc.).
            gp.update();                                                                                                // Update all game logic by one frame.
            gp.render();                                                                                                // Render the updated frame.

            // Swap front and back window buffers.
            glfwSwapBuffers(glfwWindow);

            // Iterate frame time.
            endFrameTime = glfwGetTime();                                                                               // Time at the end of this frame.
            dt = endFrameTime - startFrameTime;                                                                         // Amount of time lapsed to render the previous frame.
            startFrameTime = endFrameTime;                                                                              // Time at the start of the next frame.

            // Iterate loop time + limit frame rate if needed.
            while (glfwGetTime() < (endLoopTime + (1.0 / frameRateCap))) {}                                             // Wait if frame rate cap is lower than refresh rate from v-sync.
            endLoopTime += 1.0 / frameRateCap;                                                                          // Time at the end of this loop.
//            System.out.println(1.0 / dt + " FPS");
        }

        // Free memory.
        glfwFreeCallbacks(glfwWindow);                                                                                  // Free any callbacks attached to the window.
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


    /**
     * Loads the window icon.
     *
     * @param filePath file path of icon from resources directory
     */
    private void loadWindowIcon(String filePath) {

        IntBuffer bufferWidth = BufferUtils.createIntBuffer(1);
        IntBuffer bufferHeight = BufferUtils.createIntBuffer(1);
        IntBuffer bufferChannels = BufferUtils.createIntBuffer(1);

        ByteBuffer icon32 = ioResourceToByteBuffer(filePath, 4096);

        try (GLFWImage.Buffer icons = GLFWImage.malloc(1)) {

            ByteBuffer pixels32 = stbi_load_from_memory(icon32, bufferWidth, bufferHeight, bufferChannels, 4);

            icons
                    .position(0)
                    .width(bufferWidth.get(0))
                    .height(bufferHeight.get(0))
                    .pixels(pixels32);

            glfwSetWindowIcon(glfwWindow, icons);

            stbi_image_free(pixels32);
            memFree(bufferWidth);
            memFree(bufferHeight);
            memFree(bufferChannels);

        }
    }


    /**
     * Loads a resource as a ByteBuffer.
     *
     * @param filePath file path of resource from resources directory
     * @param bufferSize allocated buffer size (bytes)
     * @return resource as ByteBuffer
     */
    private ByteBuffer ioResourceToByteBuffer(String filePath, int bufferSize) {

        ByteBuffer buffer;

        try (InputStream is = getClass().getResourceAsStream(filePath);
             ReadableByteChannel rbc = Channels.newChannel(is)) {

            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {

                int bytes = rbc.read(buffer);

                if ( bytes == -1 )
                    break;

                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        buffer.flip();
        return buffer;
    }


    /**
     * Resizes an existing ByteBuffer.
     *
     * @param buffer ByteBuffer to resize
     * @param newCapacity new allocated buffer size (bytes)
     * @return resized ByteBuffer
     */
    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {

        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
