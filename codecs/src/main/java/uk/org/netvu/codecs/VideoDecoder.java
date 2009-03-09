package uk.org.netvu.codecs;

import java.awt.Image;
import java.nio.ByteBuffer;

// In SVN revision 2500, there is a second interface JPEGDecoderFromArray, which
// may still be worth benchmarking, but cannot be in the public API.

/**
 * An interface that all decoders capable of decoding a {@link ByteBuffer}
 * containing video data into an {@link Image} implement.
 * 
 * @param <T>
 *        the type of video data that this {@code VideoDecoder} can decode.
 */
public interface VideoDecoder<T extends VideoCodec>
{
    /**
     * Decodes a ByteBuffer containing JPEG data into an Image.
     * 
     * @param buffer
     *        the ByteBuffer containing JPEG data.
     * @throws NullPointerException
     *         if buffer is null.
     * @return the decoded Image.
     */
    Image decode( ByteBuffer buffer );

    /**
     * Disposes this {@code VideoDecoder}. This is necessary because some
     * {@code VideoDecoder}s have native resources that need cleaning up before
     * being reused.
     */
    void dispose();
}
