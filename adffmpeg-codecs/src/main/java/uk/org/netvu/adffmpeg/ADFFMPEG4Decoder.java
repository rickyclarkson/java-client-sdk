package uk.org.netvu.adffmpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.mpeg.MPEGDecoder;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Images;

/**
 * An MPEG-4 decoder that uses the ADFFMPEG Java bindings.
 */
public final class ADFFMPEG4Decoder implements MPEGDecoder
{
    /**
     * The context instance for ADFFMPEG.
     */
    private AVCodecContext codecContext;

    /**
     * The frame used for storing decoded data.
     */
    private AVFrame picture;

    /**
     * Prevents this decoder from being used concurrently or recursively, as
     * ADFFMPEG is known to behave indeterministically in such cases.
     */
    private static final Semaphore semaphore = new Semaphore( 1, true );

    /**
     * The codec used for decoding MPEG-4 frames.
     */
    private final AVCodec codec;

    /**
     * Private to prevent direct instantiation - {@link #getInstance()} should
     * be used instead.
     */
    private ADFFMPEG4Decoder()
    {
        try
        {
            semaphore.acquire();
            codec = ADFFMPEG.avcodec_find_decoder_by_name( "mpeg4" );
            codecContext = ADFFMPEG.avcodec_alloc_context();
            CheckParameters.areNotNull( codec, codecContext );
            codecContext.setWorkaround_bugs( ADFFMPEGConstants.FF_BUG_NO_PADDING );
            if ( ADFFMPEG.avcodec_open( codecContext, codec ) < 0 )
            {
                throw new InstantiationError( "Unable to open native codec" );
            }
            picture = ADFFMPEG.avcodec_alloc_frame();
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            semaphore.release();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Image decodeMPEG4( final ByteBuffer originalBuffer )
    {
        try
        {
            semaphore.acquire();
            final ByteBuffer buffer = originalBuffer.duplicate();
            final IntBuffer gotPicture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();
            final ByteBuffer directBuffer = ByteBuffer.allocateDirect( buffer.limit() + 10 ); // +10
            // is
            // an
            // experiment
            directBuffer.put( buffer );
            for ( int a = 0; a < 10; a++ )
            {
                directBuffer.put( (byte) 0 );
            }
            directBuffer.position( 0 );
            final int len = ADFFMPEG.avcodec_decode_video( codecContext, picture, gotPicture, directBuffer );
            if ( len < 0 )
            {
                throw new IllegalStateException( "ADFFMPEG did not decode any bytes" );
            }
            final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
            final IntBuffer decodeBuffer =
                    ByteBuffer.allocateDirect( numberOfPixels * 4 ).order( ByteOrder.nativeOrder() ).asIntBuffer();
            final int[] decodedData = new int[numberOfPixels];
            ADFFMPEG.extractPixelData( codecContext.getCoded_frame(), codecContext, decodeBuffer );
            decodeBuffer.get( decodedData );
            final int width = codecContext.getWidth();
            final Image image =
                    Images.loadFully( Toolkit.getDefaultToolkit().createImage(
                            new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ) );
            return image;
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            semaphore.release();
        }
    }

    /**
     * Gives an instance of ADFFMPEG4Decoder. ADFFMPEG4Decoder is implemented as
     * a singleton, to control access to the underlying codec, which is not
     * thread-safe.
     * 
     * @return an instance of ADFFMPEG4Decoder.
     */
    public static ADFFMPEG4Decoder getInstance()
    {
        return new ADFFMPEG4Decoder();
    }
}
