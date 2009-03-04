package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.MemoryImageSource;

import uk.org.netvu.util.Images;

final class Filters
{
    private Filters()
    {
    }

    public static int bound( final int x )
    {
        return x < 0 ? 0 : x > 255 ? 255 : x;
    }

    public static ImageFilter andThen( final ImageFilter first, final ImageFilter second )
    {
        return new ImageFilter()
        {
            public Image filter( final Image image )
            {
                return first.filter( second.filter( image ) );
            }
        };
    }

    public static ImageFilter createFilter( final PixelProcessor processor )
    {
        return new ImageFilter()
        {
            public Image filter( final Image source )
            {
                final BufferedImage image =
                        new BufferedImage( source.getWidth( null ), source.getHeight( null ),
                                BufferedImage.TYPE_INT_RGB );
                image.getGraphics().drawImage( source, 0, 0, null );
                final DataBuffer dataBuffer = image.getData().getDataBuffer();
                final int[] array = new int[dataBuffer.getSize()];
                processor.setPixels( array, dataBuffer );

                return Images.loadFully( Toolkit.getDefaultToolkit().createImage(
                        new MemoryImageSource( source.getWidth( null ), source.getHeight( null ), array, 0, source
                            .getWidth( null ) ) ) );
            }
        };
    }
}
