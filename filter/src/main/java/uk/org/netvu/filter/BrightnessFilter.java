package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import uk.org.netvu.util.Images;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DataBuffer;

public final class BrightnessFilter
{
    public static ImageFilter simpleBrightnessFilter(final double brightness)
    {
        return new ImageFilter()
        {
            public Image filter(Image source)
            {
                BufferedImage image = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(source, 0, 0, null);
                DataBuffer dataBuffer = image.getData().getDataBuffer();
                int[] array = new int[dataBuffer.getSize()];
                
                for (int a = 0; a < array.length; a++)
                {
                    int elem = dataBuffer.getElem(a);
                    int red = Filters.bound((int)((elem >> 16 & 0xFF) * brightness));
                    int green = Filters.bound((int)((elem >> 8 & 0xFF) * brightness));
                    int blue = Filters.bound((int)((elem & 0xFF) * brightness));
                    array[a] = (0xFF << 24) | (red << 16) | (green << 8) | blue;            
                }

                return Images.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(source.getWidth(null), source.getHeight(null), array, 0, source.getWidth(null))));
            }
        };
    }

    public static ImageFilter betterBrightnessFilter(final int minValue)
    {
        return new ImageFilter()
        {
            public Image filter(Image source)
            {
                BufferedImage image = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(source, 0, 0, null);
                DataBuffer dataBuffer = image.getData().getDataBuffer();
                int[] array = new int[dataBuffer.getSize()];
                
                for (int a = 0; a<array.length;a++)
                {
                    int elem = dataBuffer.getElem(a);
                    int red = (elem >> 16 & 0xFF) * (256-minValue) / 255 + minValue;
                    int green = (elem >> 8 & 0xFF) * (256-minValue) / 255 + minValue;
                    int blue = (elem & 0xFF) * (256-minValue) / 255 + minValue;
                    array[a] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
                }

                return Images.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(source.getWidth(null), source.getHeight(null), array, 0, source.getWidth(null))));
            }
        };
    }          
}