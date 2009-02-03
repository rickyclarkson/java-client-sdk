/**
 * 
 */
package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.swing.JPanel;

final class ToolkitDecoder implements JPEGDecoder
{
    public BufferedImage decode( ByteBuffer buffer )
    {
        buffer = buffer.duplicate();

        byte[] bytes;
        if ( buffer.hasArray() )
        {
            bytes = buffer.array();
        }
        else
        {
            bytes = new byte[buffer.limit()];
            buffer.get( bytes );
        }

        final Image result = Toolkit.getDefaultToolkit().createImage( bytes );
        try
        {
            final MediaTracker tracker = new MediaTracker( new JPanel() );
            tracker.addImage( result, 0 );
            tracker.waitForAll();
        }
        catch ( final InterruptedException e )
        {
            throw new RuntimeException( e );
        }

        return JPEGDecoders.toBufferedImage( result );
    }
}