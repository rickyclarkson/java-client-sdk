package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.MediaTracker;

import javax.swing.JPanel;

public final class JPEGDecoders
{
    public static final JPEGDecoder imageIODecoder = new ImageIODecoder();

    public static final JPEGDecoder toolkitDecoder = new ToolkitDecoder();

    public static final JPEGDecoder adffmpegDecoder = new ADFFMPEGDecoder();

    public static Image loadFully( final Image result )
    {
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

        return result;
    }

    /**
     * public static byte[] inputStreamToByteArray(InputStream input) throws
     * IOException { ByteArrayOutputStream output = new ByteArrayOutputStream();
     * copy( input, output ); byte[] result = output.toByteArray(); if
     * (result.length == 0) throw null; return result; } public static int copy(
     * InputStream input, ByteArrayOutputStream output) throws IOException {
     * byte[] buffer = new byte[1024]; int count = 0; int n = 0; while (-1 != (n
     * = input.read(buffer))) { output.write(buffer, 0, n); count += n; } return
     * count; }
     */

}
