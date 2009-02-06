package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

/**
 * A JPEG decoder that uses java.awt.Toolkit.
 */
final class ToolkitDecoder extends JPEGDecoder
{
    /**
     * ${inheritDoc}
     */
    @Override
    public Image decode( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return decodeByteArray( bytes );
    }

    /**
     * ${inheritDoc}
     */
    @Override
    Image decodeByteArray( final byte[] bytes )
    {
        return JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage( bytes ) );
    }
}
