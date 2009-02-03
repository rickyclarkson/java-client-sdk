package uk.org.netvu.jpeg;

import java.awt.Image;
import java.awt.image.BufferedImage;



public final class JPEGDecoders
{
    public static final JPEGDecoder imageIODecoder = new ImageIODecoder();

    public static final JPEGDecoder toolkitDecoder = new ToolkitDecoder();

    static BufferedImage toBufferedImage( final Image result )
    {
        if ( result instanceof BufferedImage )
        {
            return (BufferedImage) result;
        }
        else
        {
            final BufferedImage bi =
                    new BufferedImage( result.getWidth( null ), result.getHeight( null ), BufferedImage.TYPE_INT_RGB );
            bi.getGraphics().drawImage( result, 0, 0, null );
            return bi;
        }
    }

    public static final JPEGDecoder adffmpegDecoder = new ADFFMPEGDecoder();

}
