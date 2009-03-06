package uk.org.netvu.util;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
/**
 * A class providing utility methods for processing Images. JPEGDecoderFromArray
 * implementations.
 */
public final class Images
{
    /**
     * To prevent instantiation.
     */
    private Images()
    {
    }

    /**
     * Blocks until an Image is fully loaded.
     * 
     * @param result
     *        the Image to fully load.
     * @throws NullPointerException
     *         if result is null.
     * @return the fully loaded Image.
     */
    public static Image loadFully( final Image result )
    {
        CheckParameters.areNotNull( result );
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

    public static boolean equal(Image one, Image two, int tolerance)
    {
        BufferedImage bi1 = asBufferedImage(one);
        BufferedImage bi2 = asBufferedImage(two);

        DataBuffer d1 = bi1.getData().getDataBuffer();
        DataBuffer d2 = bi2.getData().getDataBuffer();
        
        if (d1.getSize() != d2.getSize())
        {
            return false;
        }

        for (int a = 0; a < d1.getSize(); a++)
        {
            int e1 = d1.getElem(a);
            int e2 = d2.getElem(a);
            int r1 = e1 >> 16 & 0xFF;
            int r2 = e2 >> 16 & 0xFF;
            int g1 = e1 >> 8 & 0xFF;
            int g2 = e2 >> 8 & 0xFF;
            int b1 = e1 & 0xFF;
            int b2 = e2 & 0xFF;
            
            int rDiff = Math.abs(r2-r1);
            int gDiff = Math.abs(g2-g1);
            int bDiff = Math.abs(b2-b1);
            
            if (rDiff > tolerance || gDiff > tolerance || bDiff > tolerance)
            {
                return false;
            }
        }
        return true;
    }

    // not as good performance as it could be, but the datatype of the DataBuffers must match.
    private static BufferedImage asBufferedImage(Image image)
    {
        BufferedImage result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image, 0, 0, null);
        return result;
    }
}
