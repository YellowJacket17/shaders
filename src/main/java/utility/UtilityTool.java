package utility;

import org.lwjgl.BufferUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * This class contains miscellaneous methods to support the application.
 */
public class UtilityTool {

    /**
     * Loads a resource as a ByteBuffer.
     *
     * @param filePath file path of resource from resources directory
     * @param bufferSize allocated buffer size (bytes)
     * @return resource as ByteBuffer
     */
    public static ByteBuffer ioResourceToByteBuffer(String filePath, int bufferSize) {

        ByteBuffer buffer;

        try (InputStream is = UtilityTool.class.getResourceAsStream(filePath);
             ReadableByteChannel rbc = Channels.newChannel(is)) {

            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {

                int bytes = rbc.read(buffer);

                if (bytes == -1) {
                    break;
                }

                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }

        } catch (Exception e) {

            // TODO : Replace with AssetLoadException.
            throw new RuntimeException("Failed to load resource from " + filePath);
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
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {

        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
