package uk.org.netvu.mpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

/**
 * An interface implemented by all decoders capable of decoding a ByteBuffer containing an
 * MPEG-4 frame into an Image.
 */
public interface MPEG4Decoder
{
    /**
     * Decodes a ByteBuffer containing an MPEG-4 frame into an Image.  Some attempt to validate
     * the ByteBuffer's contents is made, but undefined behaviour can occur if the ByteBuffer's
     * contents are corrupt.
     * 
     * @param buffer
     *        the ByteBuffer containing an MPEG-4 frame.
     * @throws NullPointerException
     *         if buffer is null.
     * @throws IllegalArgumentException
     *         if buffer is empty.
     * @return the decoded Image.
     */
    Image decodeMPEG4( ByteBuffer buffer );
}
