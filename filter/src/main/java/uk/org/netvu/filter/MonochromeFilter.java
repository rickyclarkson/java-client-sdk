package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

/**
 * A utility class for obtaining monochrome filters.
 */
public final class MonochromeFilter
{
    /**
     * Private to prevent instantiation.
     */
    private MonochromeFilter()
    {
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
                            Filters.bound( (int) Math
                                .round( red * redWeight + green * greenWeight + blue * blueWeight ) );
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
}
