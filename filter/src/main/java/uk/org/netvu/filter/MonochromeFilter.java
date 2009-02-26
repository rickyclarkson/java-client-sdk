package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;

public final class MonochromeFilter implements ImageFilter
{
    public Image filter(Image source)
    {
        BufferedImage image = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);

        image.getGraphics().drawImage(source, 0, 0, null);
        return image;
    }
}