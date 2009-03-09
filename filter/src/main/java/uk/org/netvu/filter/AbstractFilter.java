package uk.org.netvu.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * An abstract class to help make {@link ImageFilter}s easy to implement.
 * Instead of implementing {@link ImageFilter#filter(Image)} directly, you can
 * extend {@code AbstractFilter} and implement {@link #process(BufferedImage)}.
 */
public abstract class AbstractFilter implements ImageFilter
{
    /**
     * {@inheritDoc}
     */
    public final Image filter( final Image source )
    {
        final BufferedImage image =
                new BufferedImage( source.getWidth( null ), source.getHeight( null ), BufferedImage.TYPE_INT_RGB );
        image.getGraphics().drawImage( source, 0, 0, null );
        process( image );
        return image;
    }

    /**
     * Implement this method to edit the {@code BufferedImage} in-place. Note
     * that this {@code BufferedImage} is a copy of the {@code Image} passed
     * into filter, so there is no danger of modifying the original image
     * accidentally. {@code BufferedImage} has {@link BufferedImage#getWidth()},
     * {@link BufferedImage#getHeight()}, {@link BufferedImage#getRGB(int, int)}
     * and {@link BufferedImage#setRGB(int, int, int)}, which are all useful in
     * implementing this method.
     * 
     * @param image
     *        the <code>BufferedImage</code> to process.
     */
    protected abstract void process( BufferedImage image );
}
