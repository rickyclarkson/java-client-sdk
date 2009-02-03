/**
 * 
 */
package uk.org.netvu.jpeg;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

final class ImageIODecoder implements JPEGDecoder
{
    public BufferedImage decode( final ByteBuffer _buffer )
    {
        final ByteBuffer buffer = _buffer.duplicate();
        try
        {
            return ImageIO.read( new InputStream()
            {
                @Override
                public int read() throws IOException
                {
                    return buffer.hasRemaining() ? buffer.get() : -1;
                }

                @Override
                public int read( final byte[] bytes, final int offset, int length ) throws IOException
                {
                    if ( buffer.remaining() == 0 )
                    {
                        return -1;
                    }

                    length = Math.min( length, buffer.remaining() );
                    buffer.get( bytes, offset, length );
                    return length;
                }
            } );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}