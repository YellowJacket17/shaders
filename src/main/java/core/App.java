package core;

/**
 * Entry point to the application.
 */
public class App {

    public static void main(String[] args) {

        Window window = new Window();
        window.initWindow();
        window.initGame();
        window.run();
    }
}

