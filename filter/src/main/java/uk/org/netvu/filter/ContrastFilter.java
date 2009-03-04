package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import uk.org.netvu.util.Images;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DataBuffer;

public final class ContrastFilter
{
    private ContrastFilter()
    {
    }

    public static ImageFilter contrastFilter(final double contrast)
    {
        return Filters.createFilter(new PixelProcessor()
        {
            public void setPixels(int[] array, DataBuffer dataBuffer)
            {
                double totalRed = 0;
                double totalGreen = 0;
                double totalBlue = 0;
                
                for (int a = 0; a < array.length; a++)
                {
                    int elem = dataBuffer.getElem(a);
                    totalRed += elem >> 16 & 0xFF;
                    totalGreen += elem >> 8 & 0xFF;
                    totalBlue += elem & 0xFF;
                }

                totalRed /= array.length;
                totalGreen /= array.length;
                totalBlue /= array.length;
                
                for (int a = 0; a < array.length; a++)
                {
                    int elem = dataBuffer.getElem(a);
                    int red = (int)Math.round(totalRed + ((elem >> 16 & 0xFF) - totalRed) * contrast);
                    int green = (int)Math.round(totalGreen + ((elem >> 8 & 0xFF) - totalGreen) * contrast);
                    int blue = (int)Math.round(totalBlue + ((elem & 0xFF) - totalBlue) * contrast);
                    red = Math.min(255, red);
                    green = Math.min(255, green);
                    blue = Math.min(255, blue);
                    red = Math.max(0, red);
                    green = Math.max(0, green);
                    blue = Math.max(0, blue);
                    array[a] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
                }
            }
        });
    }
}