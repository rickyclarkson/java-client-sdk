package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

public final class BrightnessFilter
{
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

    public static ImageFilter betterBrightnessFilter( final int minValue )
    {
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
