package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

public final class MonochromeFilter
{
    private MonochromeFilter()
    {
    }

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
}
