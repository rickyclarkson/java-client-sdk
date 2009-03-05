package uk.org.netvu.codecs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import uk.org.netvu.util.CheckParameters;

/**
 * A JPEG decoder that uses javax.imageio.
 */
public final class ImageIODecoder implements VideoDecoder<VideoCodec.JPEG>
{
    /**
     * Private to prevent instantiation - instead {@link #createInstance()}
     * should be used.
     */
    private ImageIODecoder()
    {
    }

    /**
     * Provides an InputStream interface to a ByteBuffer.
     */
    private static final class ByteBufferInputStream extends InputStream
    {
        /**
         * The ByteBuffer to get data from.
         */
        private final ByteBuffer buffer;

        /**
         * Constructs a ByteBufferInputStream with the specified ByteBuffer
         * source.
         * 
         * @param buffer
         *        the ByteBuffer to get data from.
         * @throws NullPointerException
         *         if buffer is null.
         */
        private ByteBufferInputStream( final ByteBuffer buffer )
        {
            CheckParameters.areNotNull( buffer );
            this.buffer = buffer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int read() throws IOException
        {
            return buffer.hasRemaining() ? buffer.get() : -1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int read( final byte[] bytes, final int offset, final int originalLength ) throws IOException
        {
            CheckParameters.areNotNull( bytes );
            if ( buffer.remaining() == 0 )
            {
                return -1;
            }

            final int length = Math.min( originalLength, buffer.remaining() );
            buffer.get( bytes, offset, length );
            return length;
        }
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage decode( final ByteBuffer original )
    {
        final ByteBuffer buffer = original.duplicate();
        try
        {
            return ImageIO.read( new ByteBufferInputStream( buffer ) );
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public void dispose()
    {
    }

    /**
     * Creates an instance of ImageIODecoder.
     * 
     * @return an instance of ImageIODecoder.
     */
    public static ImageIODecoder createInstance()
    {
        return new ImageIODecoder();
    }
}
