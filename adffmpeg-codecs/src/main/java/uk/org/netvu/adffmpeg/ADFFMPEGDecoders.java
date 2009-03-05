package uk.org.netvu.adffmpeg;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.codecs.VideoDecoder;
import uk.org.netvu.codecs.VideoCodec;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;
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
    private static final class JPEG implements VideoDecoder<VideoCodec.JPEG>
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

        // anonymous initialiser
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
        public Image decode( final ByteBuffer originalBuffer )
        {
            CheckParameters.areNotNull( originalBuffer );
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

                    return decodeFrame( codecContext, picture, buffer, Function
                        .<AVCodecContext, AVFrame> constant( picture ) );
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

        public void dispose()
        {
        }
    }

    /**
     * An MPEG4 decoder that uses ADFFMPEG.
     */
    private static final class MPEG4 implements VideoDecoder<VideoCodec.MPEG4>
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
                try
                {
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

        /**
         * {@inheritDoc}
         */
        public Image decode( final ByteBuffer originalBuffer )
        {
            CheckParameters.areNotNull( originalBuffer );
            try
            {
                semaphore.acquire();
                try
                {
                    final ByteBuffer buffer = originalBuffer.duplicate();

                    final ByteBuffer directBuffer = ByteBuffer.allocateDirect( buffer.limit() + 10 );
                    directBuffer.put( buffer );
                    for ( int a = 0; a < 10; a++ )
                    {
                        directBuffer.put( (byte) 0 );
                    }

                    directBuffer.position( 0 );
                    return decodeFrame( codecContext, picture, directBuffer, getCodedFrame );
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

        public void dispose()
        {
            try
            {
                semaphore.acquire();
                try
                {
                    ADFFMPEG.avcodec_close(codecContext);
                    ADFFMPEG.av_free(codecContext.getVoidPointer());
                    ADFFMPEG.av_free(codec.getVoidPointer());
                    ADFFMPEG.av_free(picture.getVoidPointer());
                }
                finally
                {
                    semaphore.release();
                }
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
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
    public static VideoDecoder<VideoCodec.JPEG> getJPEGDecoder()
    {
        return jpeg;
    }

    /**
     * Gives the singleton instance of a private implementation of MPEG4Decoder.
     * It is a singleton to control access to the underlying codec, which is not
     * thread-safe.
     * 
     * @return the singleton instance of a private implementation of
     *         MPEG4Decoder.
     */
    public static VideoDecoder<VideoCodec.MPEG4> getMPEG4Decoder()
    {
        return mpeg4;
    }

    /**
     * Decodes a ByteBuffer and produces an Image from the resulting pixel data.
     * 
     * @param codecContext
     *        the ADFFMPEG context to use.
     * @param picture
     *        the AVFrame to supply to
     *        {@link ADFFMPEG#avcodec_decode_video(AVCodecContext, AVFrame, IntBuffer, ByteBuffer)}
     * @param buffer
     *        the ByteBuffer to read data from.
     * @param extractInto
     *        a Function that, given the AVCodecContext, returns the AVFrame to
     *        decode image data into. This is passed in as a Function instead of
     *        a value because the order of operation is important (
     *        {@link AVCodecContext#getCoded_frame()} depends on
     *        {@link ADFFMPEG#avcodec_decode_video(AVCodecContext, AVFrame, IntBuffer, ByteBuffer)}
     *        occurring first).
     * @return an Image containing the decoded data.
     */
    private static Image decodeFrame( final AVCodecContext codecContext, final AVFrame picture,
            final ByteBuffer buffer, final Function<AVCodecContext, AVFrame> extractInto )
    {
        final int len =
                ADFFMPEG.avcodec_decode_video( codecContext, picture, ByteBuffer.allocateDirect( 4 ).asIntBuffer(),
                        buffer );
        if ( len < 0 )
        {
            throw new IllegalStateException( "ADFFMPEG did not decode any bytes" );
        }

        final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
        final IntBuffer decodeBuffer =
                ByteBuffer.allocateDirect( numberOfPixels * 4 ).order( ByteOrder.nativeOrder() ).asIntBuffer();
        final int[] decodedData = new int[numberOfPixels];
        ADFFMPEG.extractPixelData( extractInto.apply( codecContext ), codecContext, decodeBuffer );
        decodeBuffer.get( decodedData );
        final int width = codecContext.getWidth();
        return Images.loadFully( Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ) );
    }

    /**
     * A Function that gives the result of calling
     * {@link AVCodecContext#getCoded_frame()} on its input.
     */
    private static final Function<AVCodecContext, AVFrame> getCodedFrame = new Function<AVCodecContext, AVFrame>()
    {
        @Override
        public AVFrame apply( final AVCodecContext context )
        {
            return context.getCoded_frame();
        }
    };
}
