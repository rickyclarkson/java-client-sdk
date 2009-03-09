package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class AbstractFilter implements ImageFilter
{
    public final Image filter( final Image source )
    {
        final BufferedImage image = new BufferedImage( source.getWidth( null ), source.getHeight( null ),
                                                       BufferedImage.TYPE_INT_RGB );
        image.getGraphics().drawImage(source, 0, 0, null);
        process(image);
        return image;
    }

    protected abstract void process(BufferedImage image);
}