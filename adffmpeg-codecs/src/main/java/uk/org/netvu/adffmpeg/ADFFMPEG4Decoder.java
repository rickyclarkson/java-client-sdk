package uk.org.netvu.adffmpeg;

import uk.org.netvu.mpeg.MPEGDecoder;
import java.util.concurrent.Semaphore;
import java.nio.ByteBuffer;
import java.awt.Image;
import java.nio.IntBuffer;
import java.nio.ByteOrder;
import java.awt.image.MemoryImageSource;
import java.awt.Toolkit;
import uk.org.netvu.jpeg.JPEGDecoders;

public final class ADFFMPEG4Decoder implements MPEGDecoder
{
    private AVCodecContext codecContext = ADFFMPEG.avcodec_alloc_context();
    private AVFrame picture;

    private final AVCodec codec = ADFFMPEG.avcodec_find_decoder_by_name("mpeg4");

    {
        if (ADFFMPEG.avcodec_open(codecContext, codec) < 0)
            throw new InstantiationError("Unable to open native codec");
        picture = ADFFMPEG.avcodec_alloc_frame();
    }
    
    private static final Semaphore semaphore = new Semaphore(1, true);
    
    public Image decodeMPEG4(ByteBuffer originalBuffer)
    {
        try
        {
            semaphore.acquire();
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            final ByteBuffer buffer = originalBuffer.duplicate();
            ADFFMPEG.avcodec_close(codecContext);
            ADFFMPEG.av_free(codecContext.getVoidPointer());
            codecContext = ADFFMPEG.avcodec_alloc_context();
            ADFFMPEG.avcodec_open(codecContext, codec);
            
            final IntBuffer gotPicture = ByteBuffer.allocateDirect(4).asIntBuffer();

            final int len = ADFFMPEG.avcodec_decode_video(codecContext, picture, gotPicture, buffer);
            if (len < 0)
                throw new IllegalStateException("ADFFMPEG did not decode any bytes");
            final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
            final IntBuffer decodeBuffer =
                ByteBuffer.allocateDirect(numberOfPixels * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
            final int[] decodedData = new int[numberOfPixels];
            ADFFMPEG.extractPixelData(picture, codecContext, decodeBuffer);
            decodeBuffer.get(decodedData);
            final int width = codecContext.getWidth();
            final Image image = JPEGDecoders.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, codecContext.getHeight(), decodedData, 0, width)));
            return image;
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