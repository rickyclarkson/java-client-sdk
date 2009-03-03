package uk.org.netvu.mpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

/**
 * An interface that all decoders capable of decoding a ByteBuffer containing an
 * MPEG-4 frame into an Image implement.
 */
public interface MPEG4Decoder
{
    /**
     * Decodes a ByteBuffer containing an MPEG-4 frame into an Image.
     * 
     * @param buffer
     *        the ByteBuffer containing an MPEG-4 frame.
     * @throws NullPointerException
     *         if buffer is null.
     * @return the decoded Image.
     */
    Image decodeMPEG4( ByteBuffer buffer );
}
