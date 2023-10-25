package utilities;

import rendering.Shader;
import rendering.Spritesheet;
import rendering.Texture;

import java.io.File;
import java.util.HashMap;

/**
 * This class stores and manages resources loaded into memory.
 * The purpose of this is to prevent the same resource from being unnecessarily loaded multiple times.
 */
public class AssetPool {

    // FIELDS
    /**
     * Map to store all shaders loaded into the game.
     */
    private static HashMap<String, Shader> shaders = new HashMap<>();

    /**
     * Map to store all textures loaded into the game.
     */
    private static HashMap<String, Texture> textures = new HashMap<>();

    /**
     * Map to store all spritesheets loaded into the game.
     */
    private static HashMap<String, Spritesheet> spritesheets = new HashMap<>();


    // METHODS
    /**
     * Returns a shader loaded into memory.
     * If the specified shader is not yet loaded, it will first be loaded and then returned.
     *
     * @param resourceName file path of shader from program root
     * @return shader
     */
    public static Shader getShader(String resourceName) {

        File file = new File(resourceName);

        if (shaders.containsKey(file.getAbsolutePath())) {

            return shaders.get(file.getAbsolutePath());
        } else {

            Shader shader = new Shader(resourceName);
            shader.compileAndLink();
            shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }


    /**
     * Returns a texture loaded into memory.
     * If the specified texture is not yet loaded, it will first be loaded and then returned.
     *
     * @param resourceName file path of texture from program root
     * @return texture
     */
    public static Texture getTexture(String resourceName) {

        File file = new File(resourceName);

        if (textures.containsKey(file.getAbsolutePath())) {

            return textures.get(file.getAbsolutePath());
        } else {

            Texture texture = new Texture(resourceName);
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }


    /**
     * Loads a spritesheet into memory.
     * If the specified spritesheet is already loaded, then nothing will occur.
     *
     * @param resourceName file path of spritesheet from program root
     * @param spritesheet Spritesheet instance
     */
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {

        File file = new File(resourceName);

        if (!spritesheets.containsKey(file.getAbsolutePath())) {

            spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }


    /**
     * Returns a spritesheet loaded into memory.
     * If the specified spritesheet is not yet loaded, then an exception will occur.
     *
     * @param resourceName file path of spritesheet from program root
     * @return spritesheet
     * @throws RuntimeException
     */
    public static Spritesheet getSpritesheet(String resourceName) {

        File file = new File(resourceName);

        if (!spritesheets.containsKey(file.getAbsolutePath())) {

            // TODO : Replace this with AssetLoaderException or AssetException in final game.
            throw new RuntimeException("Attempted to access an unloaded spritesheet in " + resourceName);
        }
        return spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
