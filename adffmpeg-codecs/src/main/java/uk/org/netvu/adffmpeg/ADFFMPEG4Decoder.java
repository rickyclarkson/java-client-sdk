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
import uk.org.netvu.util.CheckParameters;

public final class ADFFMPEG4Decoder implements MPEGDecoder
{
    private AVCodecContext codecContext = ADFFMPEG.avcodec_alloc_context();
    private AVFrame picture;
    private static final Semaphore semaphore = new Semaphore(1, true);

    private final AVCodec codec;

    {
        try
        {
            semaphore.acquire();
            codec = ADFFMPEG.avcodec_find_decoder_by_name("mpeg4");
            codecContext = ADFFMPEG.avcodec_alloc_context();
            CheckParameters.areNotNull(codec, codecContext);
            codecContext.setWorkaround_bugs(ADFFMPEGConstants.FF_BUG_NO_PADDING);
            if (ADFFMPEG.avcodec_open(codecContext, codec) <0)
                throw new InstantiationError("Unable to open native codec");
            picture = ADFFMPEG.avcodec_alloc_frame();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            semaphore.release();
        }
    }
    

    
    public Image decodeMPEG4(ByteBuffer originalBuffer)
    {
        System.out.println("decodeMPEG4");
        try
        {
            semaphore.acquire();
            final ByteBuffer buffer = originalBuffer.duplicate();
            final IntBuffer gotPicture = ByteBuffer.allocateDirect(4).asIntBuffer();
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(buffer.limit()+10); // +10 is an experiment
            directBuffer.put(buffer);
            for (int a=0; a<10;a++)
                directBuffer.put((byte)0);
            directBuffer.flip();
            System.out.println("avcodec_decode_video("+codecContext+", "+picture+", "+gotPicture+", "+directBuffer+")");
            final int len = ADFFMPEG.avcodec_decode_video(codecContext, picture, gotPicture, directBuffer);
            System.out.println("len = "+ len);
            if (len < 0)
                throw new IllegalStateException("ADFFMPEG did not decode any bytes");
            final int numberOfPixels = codecContext.getWidth() * codecContext.getHeight();
            System.out.println("numberOfPixels = " + numberOfPixels);
            final IntBuffer decodeBuffer =
                ByteBuffer.allocateDirect(numberOfPixels * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
            final int[] decodedData = new int[numberOfPixels];
            System.out.println("codecContext.width = " + codecContext.getWidth());
            System.out.println("codecContext.height = " + codecContext.getHeight());
            System.out.println("codecContext.pix_fmt = " + codecContext.getPix_fmt());
            ADFFMPEG.extractPixelData(picture, codecContext, decodeBuffer);
            System.out.println("extractPixelData happened");
            decodeBuffer.get(decodedData);
            final int width = codecContext.getWidth();
            final Image image = JPEGDecoders.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, codecContext.getHeight(), decodedData, 0, width)));
            return image;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
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