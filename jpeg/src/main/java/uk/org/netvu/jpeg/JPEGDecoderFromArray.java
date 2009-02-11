package uk.org.netvu.jpeg;

import java.awt.Image;

/**
 * An interface that all decoders capable of decoding an array of bytes
 * containing JPEG data into an Image implement.
 */
public interface JPEGDecoderFromArray
{
    /**
     * Decodes an array of bytes containing JPEG data into an Image.
     * 
     * @param array
     *        the JPEG data.
     * @throws NullPointerException if array is null.
     * @return the decoded Image.
     */
    Image decodeJPEGFromArray( byte[] array );
}
