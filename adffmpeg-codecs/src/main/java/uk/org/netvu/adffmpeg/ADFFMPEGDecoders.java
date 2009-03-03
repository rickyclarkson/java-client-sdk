package uk.org.netvu.adffmpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.jpeg.JPEGDecoder;
import uk.org.netvu.mpeg.MPEGDecoder;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Images;

/**
 * ADFFMPEGDecoders provides access to the decoders that depend on ADFFMPEG. Use
 * {@link #getJPEGDecoder()} and {@link #getMPEG4Decoder()} to get the actual
 * decoders.
 */
public final class ADFFMPEGDecoders
{
    /**
     * A JPEGDecoder that uses ADFFMPEG.
     */
    private static final class JPEG implements JPEGDecoder
    {
        /**
         * The context instance for ADFFMPEG. Currently this is cleared for
         * every frame. An optimisation might be to reuse the same context for
         * multiple frames if they have the same resolution.
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
         * {@inheritDoc}
         */
        public Image decodeJPEG( final ByteBuffer originalBuffer )
        {
            try
            {
                semaphore.acquire();
                try
                {
                    final ByteBuffer buffer = originalBuffer.duplicate();
                    ADFFMPEG.avcodec_close( codecContext );
                    ADFFMPEG.av_free( codecContext.getVoidPointer() );
                    codecContext = ADFFMPEG.avcodec_alloc_context();
                    ADFFMPEG.avcodec_open( codecContext, codec );

                    final IntBuffer gotPicture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();

                    final int len = ADFFMPEG.avcodec_decode_video( codecContext, picture, gotPicture, buffer );
                    if ( len < 0 )
                    {
                        throw new IllegalStateException( "ADFFMPEG did not decode any bytes" );
                    }

                    final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
                    final IntBuffer decodeBuffer =
                            ByteBuffer.allocateDirect( numberOfPixels * 4 ).order( ByteOrder.nativeOrder() )
                                .asIntBuffer();
                    final int[] decodedData = new int[numberOfPixels];
                    ADFFMPEG.extractPixelData( picture, codecContext, decodeBuffer );
                    decodeBuffer.get( decodedData );
                    final int width = codecContext.getWidth();
                    final Image image =
                            Images.loadFully( Toolkit.getDefaultToolkit().createImage(
                                    new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ) );
                    return image;
                }
                finally
                {
                    semaphore.release();
                }
            }
            catch ( final InterruptedException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * An MPEG4 decoder that uses ADFFMPEG.
     */
    private static final class MPEG4 implements MPEGDecoder
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
         * The codec used for decoding MPEG-4 frames.
         */
        private final AVCodec codec;

        // anonymous initialiser
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
            CheckParameters.areNotNull( originalBuffer );
            try
            {
                semaphore.acquire();
                final ByteBuffer buffer = originalBuffer.duplicate();
                final IntBuffer gotPicture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();
                final ByteBuffer directBuffer = ByteBuffer.allocateDirect( buffer.limit() + 10 );
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
    }

    /**
     * Private to prevent instantiation.
     */
    private ADFFMPEGDecoders()
    {
    }

    /**
     * Prevents this decoder from being used concurrently or recursively, as
     * ADFFMPEG is known to behave indeterministically in such cases.
     */
    static final Semaphore semaphore = new Semaphore( 1, true );

    /**
     * The single instance of JPEG to give out to clients.
     */
    private static final JPEG jpeg = new JPEG();

    /**
     * The single instance of MPEG4 to give out to clients.
     */
    private static final MPEG4 mpeg4 = new MPEG4();

    /**
     * Gives the singleton instance of a private implementation of JPEGDecoder.
     * It is a singleton to control access to the underlying codec, which is not
     * thread-safe.
     * 
     * @return the singleton instance of a private implementation of
     *         JPEGDecoder.
     */
    public static JPEGDecoder getJPEGDecoder()
    {
        return jpeg;
    }

    /**
     * Gives the singleton instance of a private implementation of MPEGDecoder.
     * It is a singleton to control access to the underlying codec, which is not
     * thread-safe.
     * 
     * @return the singleton instance of a private implementation of
     *         MPEGDecoder.
     */
    public static MPEGDecoder getMPEG4Decoder()
    {
        return mpeg4;
    }
}
