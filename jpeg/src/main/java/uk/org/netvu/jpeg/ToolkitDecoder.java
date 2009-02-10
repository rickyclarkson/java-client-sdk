package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

/**
 * A JPEG decoder that uses java.awt.Toolkit.
 */
public final class ToolkitDecoder implements JPEGDecoder, JPEGDecoderFromArray
{
    /**
     * {@inheritDoc}
     */
    public Image decodeJPEG( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return decodeJPEGFromArray( bytes );
    }

    /**
     * {@inheritDoc}
     */
    public Image decodeJPEGFromArray( final byte[] bytes )
    {
        return JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage( bytes ) );
    }

    /**
     * Gives an instance of ToolkitDecoder.
     * 
     * @return an instance of ToolkitDecoder.
     */
    public static ToolkitDecoder getInstance()
    {
        return new ToolkitDecoder();
    }
}
