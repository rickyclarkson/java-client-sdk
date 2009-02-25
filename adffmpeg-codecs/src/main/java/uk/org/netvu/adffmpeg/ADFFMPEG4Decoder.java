package uk.org.netvu.adffmpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.jpeg.JPEGDecoders;
import uk.org.netvu.mpeg.MPEGDecoder;
import uk.org.netvu.util.CheckParameters;

public final class ADFFMPEG4Decoder implements MPEGDecoder
{
    private AVCodecContext codecContext;
    private AVFrame picture;
    private static final Semaphore semaphore = new Semaphore( 1, true );

    private final AVCodec codec;

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

    FFMPEGCodec ffmpegCodec = new FFMPEGCodec( "mpeg4" );

    // remove
    public Image decodeMPEG4Original( final ByteBuffer originalBuffer )
    {
        final byte[] array = new byte[originalBuffer.limit()];
        originalBuffer.get( array );
        final int[] decoded = ffmpegCodec.decode( array, array.length );
        final int width = ffmpegCodec.getFrameWidth();
        return JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource( width, ffmpegCodec.getFrameHeight(), decoded, 0, width ) ) );
    }

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
                    JPEGDecoders.loadFully( Toolkit.getDefaultToolkit().createImage(
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

    public static ADFFMPEG4Decoder getInstance()
    {
        return new ADFFMPEG4Decoder();
    }
}
