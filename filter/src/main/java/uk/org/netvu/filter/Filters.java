package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.MemoryImageSource;

import uk.org.netvu.util.Images;

/**
 * Utility methods for implementing ImageFilters.
 */
final class Filters
{
    /**
     * Private to prevent instantiation.
     */
    private Filters()
    {
    }

    /**
     * Ensures that the specified number is bound between 0 and 255 inclusive.
     * This is useful in preventing overflow of colour components in
     * implementing ImageFilters.
     * 
     * @param x
     *        the number to bound.
     * @return x if x is between 0 and 255 inclusive, 0 if x is less than 0, 255
     *         if x is greater than 255.
     */
    public static int bound( final int x )
    {
        return x < 0 ? 0 : x > 255 ? 255 : x;
    }

    /**
     * Creates an ImageFilter that delegates to the specified PixelProcessor.
     * 
     * @param processor
     *        the PixelProcessor that will process the pixel data.
     * @return an ImageFilter that delegates to the specified PixelProcessor.
     */
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
