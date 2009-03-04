package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

/**
 * A utility class for obtaining contrast filters.
 */
public final class ContrastFilter
{
    /**
     * Private to prevent instantiation.
     */
    private ContrastFilter()
    {
    }

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
