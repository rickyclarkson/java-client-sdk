package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DataBuffer;
import uk.org.netvu.util.Images;

public final class MonochromeFilter
{
    private MonochromeFilter()
    {
    }

    public static ImageFilter monochromeFilter(final double redWeight, final double greenWeight, final double blueWeight)
    {
        return Filters.createFilter(new PixelProcessor()
        {
            public void setPixels(int[] array, DataBuffer dataBuffer)
            {
                for (int a=0;a<array.length;a++)
                {
                    int elem = dataBuffer.getElem(a);
                    int red = elem >> 16 & 0xFF;
                    int green = elem >> 8 & 0xFF;
                    int blue = elem & 0xFF;
                    int grey = Filters.bound((int)Math.round(red * redWeight + green * greenWeight + blue * blueWeight));
                    array[a] = 0xFF << 24 | grey << 16 | grey << 8 | grey;
                }
            }
        });
    }
}