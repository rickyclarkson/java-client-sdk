package uk.org.netvu.jpeg;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import uk.org.netvu.adffmpeg.ADFFMPEG;
import uk.org.netvu.adffmpeg.AVCodec;
import uk.org.netvu.adffmpeg.AVCodecContext;
import uk.org.netvu.adffmpeg.AVFrame;
import java.awt.Image;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

final class ADFFMPEGDecoder extends JPEGDecoder
{
    AVCodecContext codecContext = ADFFMPEG.avcodec_alloc_context();
    AVFrame picture;
  AVCodec codec = ADFFMPEG.avcodec_find_decoder_by_name("mjpeg");
    {
        if ( ADFFMPEG.avcodec_open( codecContext, codec ) < 0 )
        {
            throw new InstantiationError( "Unable to open native codec" );
        }
        picture = ADFFMPEG.avcodec_alloc_frame();
    }

  public Image decodeByteArray(byte[] array)
  {
    ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
    buffer.put(array);
    buffer.position(0);
    return decodeByteBuffer(buffer);
  }

  private static final Semaphore semaphore = new Semaphore(1, true);

    public Image decodeByteBuffer( ByteBuffer buffer )
    {
      try
      {
        semaphore.acquire();
      }
      catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      try
      {
        buffer = buffer.duplicate();
        ADFFMPEG.avcodec_close( codecContext );
        ADFFMPEG.av_free( codecContext.getVoidPointer() );
        codecContext = ADFFMPEG.avcodec_alloc_context();
        ADFFMPEG.avcodec_open(codecContext, codec);

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
        Image image = JPEGDecoders.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource( width, codecContext.getHeight(), decodedData, 0, width ) ));
        return image;
      }
      finally
        {
          semaphore.release();
        }
    }
}
