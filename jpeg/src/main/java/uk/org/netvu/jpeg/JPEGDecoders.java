package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import uk.org.netvu.adffmpeg.ADFFMPEG;
import uk.org.netvu.adffmpeg.AVCodecContext;
import uk.org.netvu.adffmpeg.AVFrame;

public final class JPEGDecoders
{
    private static final class ADFFMPEGDecoder implements JPEGDecoder
    {
        AVCodecContext codecContext = ADFFMPEG.avcodec_alloc_context();
        AVFrame picture;
        {
            if ( ADFFMPEG.avcodec_open( codecContext, ADFFMPEG.avcodec_find_decoder_by_name( "mjpeg" ) ) < 0 )
            {
                throw new InstantiationError( "Unable to open native codec" );
            }
            picture = ADFFMPEG.avcodec_alloc_frame();
        }

        public BufferedImage decode( ByteBuffer buffer )
        {
            buffer = buffer.duplicate();

            final IntBuffer got_picture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();

            final int len = ADFFMPEG.avcodec_decode_video( codecContext, picture, got_picture, buffer );
            if ( len < 0 )
            {
                throw null;
            }

            final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
            final IntBuffer decodeBuffer =
                    ByteBuffer.allocateDirect( numberOfPixels * 4 ).order( ByteOrder.nativeOrder() ).asIntBuffer();
            final int[] decodedData = new int[numberOfPixels];
            ADFFMPEG.extractPixelData( picture, codecContext, decodeBuffer );
            decodeBuffer.get( decodedData );
            final int width = codecContext.getWidth();
            return toBufferedImage( Toolkit.getDefaultToolkit().createImage(
                    new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ) );
        }
    }

    private static final class ToolkitDecoder implements JPEGDecoder
    {
        public BufferedImage decode( ByteBuffer buffer )
        {
            buffer = buffer.duplicate();

            byte[] bytes;
            if ( buffer.hasArray() )
            {
                bytes = buffer.array();
            }
            else
            {
                bytes = new byte[buffer.limit()];
                buffer.get( bytes );
            }

            final Image result = Toolkit.getDefaultToolkit().createImage( bytes );
            try
            {
                final MediaTracker tracker = new MediaTracker( new JPanel() );
                tracker.addImage( result, 0 );
                tracker.waitForAll();
            }
            catch ( final InterruptedException e )
            {
                throw new RuntimeException( e );
            }

            return toBufferedImage( result );
        }
    }

    private static final class ImageIODecoder implements JPEGDecoder
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

    public static final JPEGDecoder imageIODecoder = new ImageIODecoder();

    public static final JPEGDecoder toolkitDecoder = new ToolkitDecoder();

    private static BufferedImage toBufferedImage( final Image result )
    {
        if ( result instanceof BufferedImage )
        {
            return (BufferedImage) result;
        }
        else
        {
            final BufferedImage bi =
                    new BufferedImage( result.getWidth( null ), result.getHeight( null ), BufferedImage.TYPE_INT_RGB );
            bi.getGraphics().drawImage( result, 0, 0, null );
            return bi;
        }
    }

    public static final JPEGDecoder adffmpegDecoder = new ADFFMPEGDecoder();

}
