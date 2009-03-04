package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DataBuffer;
import uk.org.netvu.util.Images;

public final class MonochromeFilter implements ImageFilter
{
    private final double redWeight;
    private final double greenWeight;
    private final double blueWeight;

    public MonochromeFilter(final double redWeight, final double greenWeight, final double blueWeight)
    {
        this.redWeight = redWeight;
        this.greenWeight = greenWeight;
        this.blueWeight = blueWeight;
    }

    public Image filter(Image source)
    {
        BufferedImage image = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(source, 0, 0, null);
        DataBuffer dataBuffer = image.getData().getDataBuffer();
        int[] array = new int[dataBuffer.getSize()];
        
        for (int a=0;a<array.length;a++)
        {
            int elem = dataBuffer.getElem(a);
            int red = elem >> 16 & 0xFF;
            int green = elem >> 8 & 0xFF;
            int blue = elem & 0xFF;
            int grey = Filters.bound((int)Math.round(red * redWeight + green * greenWeight + blue * blueWeight));
            array[a] = 0xFF << 24 | grey << 16 | grey << 8 | grey;
        }

        return Images.loadFully(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(source.getWidth(null), source.getHeight(null), array, 0, source.getWidth(null))));
    }
}