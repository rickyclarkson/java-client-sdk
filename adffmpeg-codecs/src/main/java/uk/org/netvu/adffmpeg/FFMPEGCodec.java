package uk.org.netvu.adffmpeg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import uk.org.netvu.adffmpeg.*;
//import uk.org.netvu.common.util.jni.NativeLibrary;

/**
 * A simple means of accessing native video decoders from the FFMPEG library.
 * Any valid FFMPEG decoder name can be passed to the constructor and the
 * corresponding decoder will be instaniated.
 * <p>
 * {@code FFMPEGCodec} objects theoretically have three states, although in
 * reality only two are relevant. They are <i>closed</i>, <i>open pending</i>,
 * and <i>open processing</i>.
 * <p>
 * The <i>closed</i> state indicates that the native codec is not open however,
 * the native codec is opened when the {@code FFMPEGCodec} object is
 * instantiated and closed when the {@code FFMPEGCodec} object is garbage
 * collected, so this state has limited meaning.
 * <p>
 * The <i>open pending</i> state indicates that the codec has not yet received
 * or processed any data. In this state, the {@link #getFrameWidth} and
 * {@link #getFrameHeight} methods will return invalid values.
 * <p>
 * The <i>open processing</i> state indicates that the codec has received one
 * or more frames of data and ready to process more data. In this state, the
 * {@link #getFrameWidth} and {@link #getFrameHeight} methods will return valid
 * values.
 * <p>
 * This class requries the following native libraries to function:
 * <ul>
 * <li>FFMPEGCodec
 * <li>avcodec
 * </ul>
 */
public class FFMPEGCodec implements ADFFMPEGConstants
{
    /**
     * A binary semaphore to restrict access to the FFMPEG library itself to one
     * codec instance at a time.
     */
    private static final Semaphore SEMAPHORE = new Semaphore( 1, true );

    private AVCodec codec;
    private AVCodecContext codecContext;
    private AVFrame picture;
    private ByteBuffer sourceBuffer;
    private IntBuffer decodeBuffer;

    /**
     * Creates a native FFMPEG decoder instance, of the type defined by the name
     * passed in the {@code codecName} parameter, which can be any decoder name
     * supported by the FFMPEG library. If {@code codecName} is {@code null}
     * then a {@code NullPointerException} will be thrown.
     * 
     * @param codecName
     *        the name of the FFMPEG decoder to create.
     * @throws NullPointerException
     *         if the required native data structures cannot be allocated, or if
     *         {@code codecName} is {@code null}.
     * @throws InstantiationError
     *         if the native codec cannot be opened.
     */
    public FFMPEGCodec( String codecName )
    {
        try
        {
            SEMAPHORE.acquire();
            if ( codecName == null )
            {
                throw new NullPointerException( "codecName cannot be null" );
            }
            codec = ADFFMPEG.avcodec_find_decoder_by_name( codecName );
            codecContext = ADFFMPEG.avcodec_alloc_context();
            if ( ( codec == null ) || ( codecContext == null ) )
            {
                throw new NullPointerException(
                        "Native codec initialization failed" );
            }
            codecContext.setWorkaround_bugs( FF_BUG_NO_PADDING );
            if ( ADFFMPEG.avcodec_open( codecContext, codec ) < 0 )
            {
                throw new InstantiationError( "Unable to open native codec" );
            }
            picture = ADFFMPEG.avcodec_alloc_frame();
            SEMAPHORE.release();
        }
        catch ( InterruptedException e )
        {
            System.err.println( "Unable to create FFMPEGCodec instance" + e );
            throw new InstantiationError(
                    "Unable to acquire semaphore to create codec" );
        }
    }

    /**
     * Decodes a single frame of video data. The first time this method is
     * called with valid data will cause the object to transition from the
     * <i>open pending</i> state to the <i>open processing</i> state.
     * <p>
     * If the {@code length} parameter is zero or less, or if it is larger than
     * the length of the {@code encodedData} array then {@code null} will be
     * returned. If {@code encodedData} is {@code null} or has a zero length
     * then {@code null} will be returned.
     * <p>
     * If the frame of data is successfully decoded then an array of {@code int}
     * will be returned. It will have
     * {@literal getFrameWidth() x getFrameHeight()} elements, and each element
     * represents one pixel of the decoded image in ARGB format. If the frame of
     * data was not successfullly decoded, then {@code null} will be returned.
     * 
     * @param encodedData
     *        a byte array containing one frame of encoded data.
     * @param length
     *        the number of bytes in the array containing encoded data.
     * @return an array of ARGB formatted {@code int} values if the frame of
     *         data was successfully decoded or {@code null} if it was not.
     */
    public int[] decode( byte[] encodedData, int length )
    {
        int[] decodedData = null;
        int len;
        IntBuffer got_picture = ByteBuffer.allocateDirect( 4 ).asIntBuffer();

        try
        {
            SEMAPHORE.acquire();
            if ( ( encodedData == null ) || ( encodedData.length == 0 )
                    || ( length <= 0 ) || ( length > encodedData.length ) )
            {
                return null;
            }
            if ( codecContext.getCodec_id() == CodecID.CODEC_ID_MJPEG )
            {
                ADFFMPEG.avcodec_close( codecContext );
                ADFFMPEG.av_free( codecContext.getVoidPointer() );
                codecContext = ADFFMPEG.avcodec_alloc_context();
                ADFFMPEG.avcodec_open( codecContext, codec );
            }
            if ( ( sourceBuffer == null )
                    || ( sourceBuffer.capacity() < encodedData.length ) )
            {
                sourceBuffer = ByteBuffer.allocateDirect( encodedData.length );
            }
            sourceBuffer.clear();
            sourceBuffer.put( encodedData );
            sourceBuffer.flip();
            
            len = ADFFMPEG.avcodec_decode_video( codecContext, picture,
                    got_picture, sourceBuffer );
            if ( len >= 0 )
            {
                int numberOfPixels = codecContext.getWidth()
                        * codecContext.getHeight();

                if ( ( decodeBuffer == null )
                        || ( decodeBuffer.capacity() < numberOfPixels ) )
                {
                    decodeBuffer = ByteBuffer.allocateDirect(
                            numberOfPixels * 4 ).order( ByteOrder.nativeOrder() ).asIntBuffer();
                }
                decodeBuffer.clear();
                decodedData = new int[codecContext.getWidth()
                        * codecContext.getHeight()];
                if ( codecContext.getCodec_id() == CodecID.CODEC_ID_MPEG4 )
                {
                    ADFFMPEG.extractPixelData( codecContext.getCoded_frame(),
                            codecContext, decodeBuffer );
                }
                else
                {
                    ADFFMPEG.extractPixelData( picture, codecContext,
                            decodeBuffer );
                }
                decodeBuffer.get( decodedData );
            }
            SEMAPHORE.release();
        }
        catch ( InterruptedException e )
        {
            System.err.println( "Unable to decode image " + e );
        }
        return decodedData;
    }

    /**
     * Returns the width in pixels of the last frame of data to be successfully
     * decoded by called {@link #decode}. The result returned by this method is
     * only valid if the last frame of data was successfully decoded.
     * 
     * @return the width in pixels of the last frame of data to be successfully
     *         decoded.
     */
    public int getFrameWidth()
    {
        return codecContext.getWidth();
    }

    /**
     * Returns the height in pixels of the last frame of data to be successfully
     * decoded by called {@link #decode}. The result returned by this method is
     * only valid if the last frame of data was successfully decoded.
     * 
     * @return the height in pixels of the last frame of data to be successfully
     *         decoded.
     */
    public int getFrameHeight()
    {
        return codecContext.getHeight();
    }

    public void closeCodec()
    {
        try
        {
            SEMAPHORE.acquire();
            ADFFMPEG.avcodec_close( codecContext );
            ADFFMPEG.av_free( codecContext.getVoidPointer() );
            ADFFMPEG.av_free( codec.getVoidPointer() );
            ADFFMPEG.av_free( picture.getVoidPointer() );
            SEMAPHORE.release();
        }
        catch ( InterruptedException e )
        {
            System.err.println( "Unable to close FFMPEGCodec instance " + e );
        }
    }
}
