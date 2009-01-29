package uk.org.netvu.jpeg;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.JPanel;

public final class JPEGDecoders
{
  public static final JPEGDecoder imageIODecoder = new JPEGDecoder()
    {
      public BufferedImage decode(final ByteBuffer buffer)
      {
        try
        {
          return ImageIO.read(new InputStream()
          {
            public int read() throws IOException
            {              
              return buffer.hasRemaining() ? buffer.get() : -1;
            }

            public int read(byte[] bytes, int offset, int length) throws IOException
            {
              if (buffer.remaining() == 0)
                return -1;

              length = Math.min(length, buffer.remaining());
              buffer.get(bytes, offset, length);
              return length;
            }
          });
        }
        catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      }
    };

  public static final JPEGDecoder toolkitDecoder = new JPEGDecoder()
  {
    public BufferedImage decode(ByteBuffer buffer)
    {
      byte[] bytes;
      if (buffer.hasArray())
        bytes = buffer.array();
      else
        {
          bytes = new byte[buffer.limit()];
          buffer.get(bytes);
        }

      Image result = Toolkit.getDefaultToolkit().createImage(bytes);
      try
      {
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(result, 0);
        tracker.waitForAll();
      }
      catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      
      if (result instanceof BufferedImage)
        return (BufferedImage)result;
      else
      {
        BufferedImage bi = new BufferedImage(result.getWidth(null), result.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(result, 0, 0, null);
        return bi;
      }
    }
  };
}