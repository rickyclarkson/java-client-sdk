package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.MemoryImageSource;

import uk.org.netvu.util.Images;
import uk.org.netvu.util.CheckParameters;

/**
 * Utility methods for implementing ImageFilters.
 */
public final class Filters
{
    /**
     * Private to prevent instantiation.
     */
    private Filters()
    {
    }

    /**
     * Ensures that the specified number is bound between the specified lower and upper bounds inclusively.
     * This is useful in preventing overflow of colour components in
     * implementing ImageFilters.
     * 
     * @param x
     *        the number to bound.
     * @param lower
     *        the lower bound.
     * @param upper
     *        the upper bound.
     * @return x if x is between lower and upper inclusive, lower if x is less than lower, upper
     *         if x is greater than upper.
     */
    public static int bindToWithin( final int x, final int lower, final int upper )
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

    /**
     * A brightness filter that multiplies each pixel value by a constant
     * factor.
     * 
     * @param brightness
     *        the constant factor by which to multiply each pixel value.
     * @return a brightness filter that multiplies each pixel value by the
     *         specified brightness.
     */
    public static ImageFilter simpleBrightnessFilter( final double brightness )
    {
        return Filters.createFilter( new PixelProcessor()
        {
            public void setPixels( final int[] array, final DataBuffer dataBuffer )
            {
                for ( int a = 0; a < array.length; a++ )
                {
                    final int elem = dataBuffer.getElem( a );
                    final int red = Filters.bindToWithin( (int) ( ( elem >> 16 & 0xFF ) * brightness ), 0, 255 );
                    final int green = Filters.bindToWithin( (int) ( ( elem >> 8 & 0xFF ) * brightness ) , 0, 255);
                    final int blue = Filters.bindToWithin( (int) ( ( elem & 0xFF ) * brightness ), 0, 255 );
                    array[a] = 0xFF << 24 | red << 16 | green << 8 | blue;
                }
            }
        } );
    }

    /**
     * A brightness filter that linearly scales all pixel values to the range
     * between minValue and 255.
     * 
     * @param minValue
     *        the minimum pixel value in the output Image.
     * @throws IllegalArgumentException
     *         if minValue is not between 0 and 255.
     * @return a brightness filter that linearly scales all pixel values to the
     *         range between minValue and 255.
     */
    public static ImageFilter betterBrightnessFilter( final int minValue )
    {
        CheckParameters.from( 0 ).to( 255 ).bounds( minValue );
        return Filters.createFilter( new PixelProcessor()
        {
            public void setPixels( final int[] array, final DataBuffer dataBuffer )
            {
                for ( int a = 0; a < array.length; a++ )
                {
                    final int elem = dataBuffer.getElem( a );
                    final int red = ( elem >> 16 & 0xFF ) * ( 256 - minValue ) / 255 + minValue;
                    final int green = ( elem >> 8 & 0xFF ) * ( 256 - minValue ) / 255 + minValue;
                    final int blue = ( elem & 0xFF ) * ( 256 - minValue ) / 255 + minValue;
                    array[a] = 0xFF << 24 | red << 16 | green << 8 | blue;
                }
            }
        } );
    }

    /**
     * Gives a monochrome filter with the specified weightings. To get an
     * unweighted filter, specify 1.0/3 for each weighting. Weightings that add
     * up to something other than 1 are allowed, but are likely to be
     * suboptimal.
     * 
     * @param redWeight
     *        the weighting to apply to the red colour component.
     * @param greenWeight
     *        the weighting to apply to the green colour component.
     * @param blueWeight
     *        the weighting to apply to the blue colour component.
     * @return a monochrome filter that uses the specified weightings.
     */
    public static ImageFilter monochromeFilter( final double redWeight, final double greenWeight,
            final double blueWeight )
    {
        return Filters.createFilter( new PixelProcessor()
        {
            public void setPixels( final int[] array, final DataBuffer dataBuffer )
            {
                for ( int a = 0; a < array.length; a++ )
                {
                    final int elem = dataBuffer.getElem( a );
                    final int red = elem >> 16 & 0xFF;
                    final int green = elem >> 8 & 0xFF;
                    final int blue = elem & 0xFF;
                    final int grey =
                            Filters.bindToWithin( (int) Math
                                           .round( red * redWeight + green * greenWeight + blue * blueWeight ), 0, 255 );
                    array[a] = 0xFF << 24 | grey << 16 | grey << 8 | grey;
                }
            }
        } );
    }

    /**
     * A monochrome filter with its weightings based on <a
     * href="http://en.wikipedia.org/wiki/Luminance_(relative)">relative
     * luminance</a>.
     */
    public static final ImageFilter standardLuminanceMonochromeFilter = monochromeFilter( 0.2126, 0.7152, 0.0722 );

    /**
     * Gives a contrast filter that applies the specified contrast shift to
     * images.
     * 
     * @param contrast
     *        the amount to change the contrast by - 1.0 keeps the contrast the
     *        same, 0.5 halves the contrast, 2.0 doubles the contrast.
     * @return a contrast filter that applies the specified contrast shift to
     *         images.
     */
    public static ImageFilter contrastFilter( final double contrast )
    {
        return Filters.createFilter( new PixelProcessor()
        {
            public void setPixels( final int[] array, final DataBuffer dataBuffer )
            {
                double totalRed = 0;
                double totalGreen = 0;
                double totalBlue = 0;

                for ( int a = 0; a < array.length; a++ )
                {
                    final int elem = dataBuffer.getElem( a );
                    totalRed += elem >> 16 & 0xFF;
                    totalGreen += elem >> 8 & 0xFF;
                    totalBlue += elem & 0xFF;
                }

                totalRed /= array.length;
                totalGreen /= array.length;
                totalBlue /= array.length;

                for ( int a = 0; a < array.length; a++ )
                {
                    final int elem = dataBuffer.getElem( a );
                    int red = (int) Math.round( totalRed + ( ( elem >> 16 & 0xFF ) - totalRed ) * contrast );
                    int green = (int) Math.round( totalGreen + ( ( elem >> 8 & 0xFF ) - totalGreen ) * contrast );
                    int blue = (int) Math.round( totalBlue + ( ( elem & 0xFF ) - totalBlue ) * contrast );
                    red = Math.min( 255, red );
                    green = Math.min( 255, green );
                    blue = Math.min( 255, blue );
                    red = Math.max( 0, red );
                    green = Math.max( 0, green );
                    blue = Math.max( 0, blue );
                    array[a] = 0xFF << 24 | red << 16 | green << 8 | blue;
                }
            }
        } );
    }
}
