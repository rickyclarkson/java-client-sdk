package uk.org.netvu.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

/**
 * A JPEG decoder that uses javax.imageio.
 */
public final class ImageIODecoder implements JPEGDecoder, JPEGDecoderFromArray
{
    /**
     * {@inheritDoc}
     */
    public BufferedImage decodeJPEGFromArray( final byte[] array )
    {
        try
        {
            return ImageIO.read( new ByteArrayInputStream( array ) );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage decodeJPEG( final ByteBuffer original )
    {
        final ByteBuffer buffer = original.duplicate();
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
                public int read( final byte[] bytes, final int offset, final int originalLength ) throws IOException
                {
                    if ( buffer.remaining() == 0 )
                    {
                        return -1;
                    }

                    final int length = Math.min( originalLength, buffer.remaining() );
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

    /**
     * Gets an instance of ImageIODecoder.
     * 
     * @return an instance of ImageIODecoder.
     */
    public static ImageIODecoder getInstance()
    {
        return new ImageIODecoder();
    }
}
