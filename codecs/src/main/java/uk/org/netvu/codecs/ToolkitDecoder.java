package uk.org.netvu.codecs;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

import uk.org.netvu.util.Images;

/**
 * A JPEG decoder that uses java.awt.Toolkit.
 */
public final class ToolkitDecoder implements JPEGDecoder
{
    /**
     * Private to prevent instantiation - {@link #createInstance()} should be
     * used instead.
     */
    private ToolkitDecoder()
    {
    }

    /**
     * {@inheritDoc}
     */
    public Image decodeJPEG( final ByteBuffer originalBuffer )
    {
        final ByteBuffer buffer = originalBuffer.duplicate();
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return Images.loadFully( Toolkit.getDefaultToolkit().createImage( bytes ) );
    }

    /**
     * Creates an instance of ToolkitDecoder.
     * 
     * @return an instance of ToolkitDecoder.
     */
    public static ToolkitDecoder createInstance()
    {
        return new ToolkitDecoder();
    }
}
