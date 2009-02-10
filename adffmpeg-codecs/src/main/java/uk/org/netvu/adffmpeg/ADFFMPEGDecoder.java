package uk.org.netvu.adffmpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.adffmpeg.ADFFMPEG;
import uk.org.netvu.adffmpeg.AVCodec;
import uk.org.netvu.adffmpeg.AVCodecContext;
import uk.org.netvu.adffmpeg.AVFrame;
import uk.org.netvu.jpeg.JPEGDecoder;
import uk.org.netvu.jpeg.JPEGDecoderFromArray;
import uk.org.netvu.jpeg.JPEGDecoders;

/**
 * A JPEG decoder that uses the ADFFMPEG Java bindings.
 */
public final class ADFFMPEGDecoder implements JPEGDecoder, JPEGDecoderFromArray
{
    /**
     * The context instance for ADFFMPEG. Currently this is cleared for every
     * frame. An optimisation might be to reuse the same context for multiple
     * frames if they have the same resolution.
     */
    private AVCodecContext codecContext = ADFFMPEG.avcodec_alloc_context();

    /**
     * The frame used for storing decoded data.
     */
    private AVFrame picture;

    /**
     * The MJPEG codec used for decoding JPEG frames.
     */
    private final AVCodec codec = ADFFMPEG.avcodec_find_decoder_by_name( "mjpeg" );
    {
        if ( ADFFMPEG.avcodec_open( codecContext, codec ) < 0 )
        {
            throw new InstantiationError( "Unable to open native codec" );
        }
        picture = ADFFMPEG.avcodec_alloc_frame();
    }

    /**
     * ${inheritDoc}
     */
    @Override
    public Image decodeJPEGFromArray( final byte[] array )
    {
        final ByteBuffer buffer = ByteBuffer.allocateDirect( array.length );
        buffer.put( array );
        buffer.position( 0 );
        return decodeJPEG( buffer );
    }

    /**
     * Prevents this decoder from being used concurrently or recursively, as
     * ADFFMPEG is known to behave indeterministically in such cases.
     */
    private static final Semaphore semaphore = new Semaphore( 1, true );

    /**
     * ${inheritDoc}
     */
    @Override
    public Image decodeJPEG( ByteBuffer buffer )
    {
        try
        {
            semaphore.acquire();
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException( e );
        }
        try
        {
            buffer = buffer.duplicate();
            ADFFMPEG.avcodec_close( codecContext );
            ADFFMPEG.av_free( codecContext.getVoidPointer() );
            codecContext = ADFFMPEG.avcodec_alloc_context();
            ADFFMPEG.avcodec_open( codecContext, codec );

            final IntBuffer gotPicture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();

            final int len = ADFFMPEG.avcodec_decode_video( codecContext, picture, gotPicture, buffer );
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
            final Image image =
                    JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage(
                            new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ) );
            return image;
        }
        finally
        {
            semaphore.release();
        }
    }

  private static final ADFFMPEGDecoder instance = new ADFFMPEGDecoder();

  public static ADFFMPEGDecoder getInstance()
  {
    return instance;
  }
}