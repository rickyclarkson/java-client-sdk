/**
 * 
 */
package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;
import javax.swing.JPanel;

final class ToolkitDecoder extends JPEGDecoder
{
    public Image decodeByteBuffer( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return decodeByteArray(bytes);
    }

  public Image decodeByteArray( byte[] bytes)
  {
    return JPEGDecoders.loadFully(Toolkit.getDefaultToolkit().createImage( bytes ));
  }
}