package core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

public class Camera {

    /*
     * The perspective matrix retains perspective.
     * For example, as an object gets further away from the camera, it appears smaller.
     *
     * The orthographic matrix is fixed perspective.
     * No matter the distance from the camera, objects remain the same size.
     *
     * Both the perspective and orthographic matrices have a near and far clipping plain.
     *
     * The projection matrix is either the perspective matrix or the orthographic matrix.
     * In our case, it's the orthographic matrix since we're working on a 2D game.
     *
     * The view matrix defines where the camera is looking from.
     *
     * The position matrix (aPos) defines world coordinates.
     * Essentially, this defines the world coordinates of objects placed in the game.
     *
     * Matrix multiplication is: projection*view*position
     * The order is position multiplied by view, then that result multiplied by projection.
     *
     * In summary:
     * Projection matrix tells us how big we want the screen space to be.
     * View matrix tells us where the camera is in relation to the world.
     * Position matrix (aPos) tells us what the world coordinates are.
     * By doing projection*view*position, we go from world coordinates to normalized coordinates (-1 to 1).
     * The normalized coordinates are what are actually drawn on screen.
     */

    // BASIC FIELDS
    /**
     * Projection matrix.
     * The projection matrix determines how large the screen space is.
     * In this case, the projection matrix is an orthographic matrix of fixed perspective.
     */
    private Matrix4f projectionMatrix;

    /**
     * View matrix.
     * The view matrix determines where the camera is in relation to the screen space.
     */
    private Matrix4f viewMatrix;

    /**
     * Position matrix.
     * The position matrix determines what the screen space coordinates are.
     */
    private Vector2f position;


    // SCREEN SETTINGS
    /**
     * Native tile size of rendered tiles.
     * Tiles are the same width and height.
     */
    private final int nativeTileSize = 32;

    /**
     * Tiles per column in the screen space.
     */
    private final int maxScreenCol = 40; //24

    /**
     * Tiles per row in the screen space.
     */
    private final int maxScreenRow = 21;  //14

    /**
     * Native screen width as determined by the native tile size and number of columns.
     */
    private final int nativeScreenWidth = nativeTileSize * maxScreenCol;

    /**
     * Native screen height as determined by the native tile size and number of rows.
     */
    private final int nativeScreenHeight = nativeTileSize * maxScreenRow;


    // CONSTRUCTOR
    /**
     * Constructs a Camera instance.
     *
     * @param position initial camera position (bottom-left coordinate)
     */
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }


    // METHODS
    /**
     * Adjusts the projection matrix if the window size is changed.
     */
    public void adjustProjection() {

        projectionMatrix.identity();                                                                                    // Sets the projection matrix to equal the identity matrix.
//        projectionMatrix.ortho(0.0f, (float)nativeScreenWidth, 0.0f,  (float)nativeScreenHeight, 0.0f, 100.0f);  // Default form tutorial!
        projectionMatrix.ortho(0.0f, (float)nativeScreenWidth, (float)nativeScreenHeight, 0.0f, 0.0f, 100.0f);          // These are NOT pixels being defined; it's our own screen coordinate system.
    }


    /**
     * Retrieves the view matrix, which defines where the camera is looking from.
     *
     * @return view matrix
     */
    public Matrix4f getViewMatrix() {

        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);                                                         // Camera pointing in -1 of the z direction.
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();                                                                                          // Modifies the view matrix directly
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                                            cameraFront.add(position.x, position.y, 0.0f),
                                            cameraUp);                                                                  // Modifies the view matrix directly.
        return viewMatrix;
    }


    /**
     * Retrieves the projection matrix, which is an orthographic matrix of fixed perspective.
     *
     * @return perspective matrix
     */
    public Matrix4f getProjectionMatrix() {

        return this.projectionMatrix;
    }
}
