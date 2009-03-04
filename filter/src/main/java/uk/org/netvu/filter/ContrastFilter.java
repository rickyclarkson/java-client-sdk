package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import uk.org.netvu.util.Images;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DataBuffer;

public final class ContrastFilter implements ImageFilter
{
    private final double contrast;

    public ContrastFilter(double contrast)
    {
        this.contrast = contrast;
    }

    public Image filter(Image source)
    {
        BufferedImage image = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(source, 0, 0, null);
        DataBuffer dataBuffer = image.getData().getDataBuffer();
        int[] array = new int[dataBuffer.getSize()];
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

        return Images.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(source.getWidth(null), source.getHeight(null), array, 0, source.getWidth(null))));
    }    
}