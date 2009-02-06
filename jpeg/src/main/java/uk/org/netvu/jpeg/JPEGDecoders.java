package uk.org.netvu.jpeg;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;


/**
 * A class providing access to all the available JPEG decoder implementations.
 */
public final class JPEGDecoders
{
    /**
     * A JPEG decoder that uses javax.imageio.
     */
    public static final JPEGDecoder imageIODecoder = new ImageIODecoder();

    /**
     * A JPEG decoder that uses java.awt.Toolkit.
     */
    public static final JPEGDecoder toolkitDecoder = new ToolkitDecoder();

    /**
     * A JPEG decoder that uses the ADFFMPEG Java bindings.
     */
    public static final JPEGDecoder adffmpegDecoder = new ADFFMPEGDecoder();

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
}
