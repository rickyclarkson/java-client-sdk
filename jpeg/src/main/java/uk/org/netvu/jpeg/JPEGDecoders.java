package uk.org.netvu.jpeg;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import uk.org.netvu.util.CheckParameters;

/**
 * A class providing utility methods relating to JPEGDecoder and
 * JPEGDecoderFromArray implementations.
 */
public final class JPEGDecoders
{
    /**
     * To prevent instantiation.
     */
    private JPEGDecoders()
    {
    }

    /**
     * Blocks until an Image is fully loaded.
     * 
     * @param result
     *        the Image to fully load.
     * @throws NullPointerException if result is null.
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
