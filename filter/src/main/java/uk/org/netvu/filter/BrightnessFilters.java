package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A utility class for obtaining brightness filters.
 */
public final class BrightnessFilters
{
    /**
     * Private to prevent instantiation.
     */
    private BrightnessFilters()
    {
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
                    final int red = Filters.bound( (int) ( ( elem >> 16 & 0xFF ) * brightness ) );
                    final int green = Filters.bound( (int) ( ( elem >> 8 & 0xFF ) * brightness ) );
                    final int blue = Filters.bound( (int) ( ( elem & 0xFF ) * brightness ) );
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
}
