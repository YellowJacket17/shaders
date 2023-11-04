package core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

public class SystemCamera {

    /*
     * Below are general definitions for the projection, view, and position matrices.
     *
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

    // FIELDS
    /**
     * Projection matrix.
     * The projection matrix determines how large the screen space is.
     * In this case, the projection matrix is an orthographic matrix of fixed perspective.
     * In this application, it will always be fixed unless debugging.
     */
    private final Matrix4f projectionMatrix;

    /**
     * View matrix.
     * The view matrix determines where the system camera is in relation to the screen space.
     * In this application, it will always be fixed unless debugging.
     */
    private final Matrix4f viewMatrix;

    /**
     * Position matrix.
     * The position matrix determines what the screen space coordinates are.
     * For this application, it will always be fixed unless debugging.
     */
    private final Vector2f positionMatrix;

    /**
     * Visible screen width.
     * Note that this are NOT necessarily pixels being defined: it's our own screen coordinate system.
     */
    private int screenWidth;

    /**
     * Visible screen height.
     * Note that this are NOT pixels necessarily being defined: it's our own screen coordinate system.
     */
    private int screenHeight;


    // CONSTRUCTOR
    /**
     * Constructs a Camera instance.
     *
     * @param screenWidth visible screen width (NOT necessarily pixels but some amount of units)
     * @param screenHeight visible screen height (NOT necessarily pixels but some amount of units)
     */
    public SystemCamera(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.positionMatrix = new Vector2f();
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
        adjustView();
    }


    // METHODS
    /**
     * Adjusts the projection matrix.
     * The current visible screen size (width and height) will be applied.
     */
    public void adjustProjection() {

        projectionMatrix.identity();                                                                                    // Sets the projection matrix to equal the identity matrix.
        projectionMatrix.ortho(0.0f, (float)screenWidth, (float)screenHeight, 0.0f, 0.0f, 100.0f);                      // Screen coordinate (0, 0) is defined at the top-left.
    }


    /**
     * Adjusts the view matrix.
     * The current position matrix will be applied.
     */
    public void adjustView() {

        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);                                                         // Camera pointing in -1 of the z direction.
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();                                                                                          // Modifies the view matrix directly
        viewMatrix.lookAt(new Vector3f(positionMatrix.x, positionMatrix.y, 20.0f),
                cameraFront.add(positionMatrix.x, positionMatrix.y, 0.0f),
                cameraUp);                                                                                              // Modifies the view matrix directly.
    }


    /**
     * Adjusts the position matrix.
     *
     * @param position system camera position (top-left coordinate)
     */
    public void adjustPosition(Vector2f position) {

        this.positionMatrix.set(position);
        adjustView();
    }


    /**
     * Retrieves the projection matrix.
     *
     * @return perspective matrix
     */
    public Matrix4f getProjectionMatrix() {

        return projectionMatrix;
    }


    /**
     * Retrieves the view matrix.
     *
     * @return view matrix
     */
    public Matrix4f getViewMatrix() {

        return viewMatrix;
    }


    /**
     * Retrieves the position matrix.
     *
     * @return position matrix
     */
    public Vector2f getPositionMatrix() {

        return positionMatrix;
    }
}
