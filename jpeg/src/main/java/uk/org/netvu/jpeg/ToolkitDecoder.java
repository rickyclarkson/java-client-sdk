package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

/**
 * A JPEG decoder that uses java.awt.Toolkit.
 */
final class ToolkitDecoder implements JPEGDecoder, JPEGDecoderFromArray
{
    /**
     * ${inheritDoc}
     */
    @Override
    public Image decodeJPEG( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return decodeJPEGFromArray( bytes );
    }

    /**
     * ${inheritDoc}
     */
    public Image decodeJPEGFromArray( final byte[] bytes )
    {
        return JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage( bytes ) );
    }
}
