package uk.org.netvu.util;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;

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
}
