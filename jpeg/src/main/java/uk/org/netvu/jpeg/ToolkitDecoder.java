/**
 * 
 */
package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.ByteBuffer;

final class ToolkitDecoder extends JPEGDecoder
{
    @Override
    public Image decodeByteBuffer( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return decodeByteArray( bytes );
    }

    @Override
    public Image decodeByteArray( final byte[] bytes )
    {
        return JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage( bytes ) );
    }
}
