package uk.org.netvu.jpeg;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import uk.org.netvu.util.Function;
import java.nio.ByteBuffer;

/**
 * A class providing access to all the available JPEG decoder implementations.
 */
public final class JPEGDecoders
{
    /**
     * A JPEG decoder that uses javax.imageio.
     */
    public static final ImageIODecoder imageIODecoder = new ImageIODecoder();

    /**
     * A JPEG decoder that uses java.awt.Toolkit.
     */
    public static final ToolkitDecoder toolkitDecoder = new ToolkitDecoder();

    public static Image loadFully( final Image result )
    {
        try
        {
            final MediaTracker tracker = new MediaTracker( new Container() );
            tracker.addImage( result, 0 );
            tracker.waitForAll();
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException( e );
        }

        return result;
    }

  public static Function<ByteBuffer, Image> decodeJPEG(final JPEGDecoder decoder)
  {
    return new Function<ByteBuffer, Image>()
      {
        public Image apply(ByteBuffer buffer)
        {
          return decoder.decodeJPEG(buffer);
        }
      };
  }

  public static Function<byte[], Image> decodeJPEGFromArray(final JPEGDecoderFromArray decoder)
  {
    return new Function<byte[], Image>()
      {
        public Image apply(byte[] array)
        {
          return decoder.decodeJPEGFromArray(array);
        }
      };
  }
}
