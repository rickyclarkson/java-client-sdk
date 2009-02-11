package uk.org.netvu.jpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

/**
 * An interface that all decoders capable of decoding a ByteBuffer containing
 * JPEG data into an Image implement.
 */
public interface JPEGDecoder
{
    /**
     * Decodes a ByteBuffer containing JPEG data into an Image.
     * 
     * @param buffer
     *        the ByteBuffer containing JPEG data.
     * @throws NullPointerException if buffer is null.
     * @return the decoded Image.
     */
    Image decodeJPEG( ByteBuffer buffer );
}
