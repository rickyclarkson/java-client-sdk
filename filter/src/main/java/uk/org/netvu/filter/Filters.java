package uk.org.netvu.filter;

import java.awt.Image;
final class Filters
{
    private Filters()
    {
    }

    public static int bound(int x)
    {
        return x < 0 ? 0 : x > 255 ? 255 : x;
    }

    public static ImageFilter andThen(final ImageFilter first, final ImageFilter second)
    {
        return new ImageFilter()
        {
            public Image filter(Image image)
            {
                return first.filter(second.filter(image));
            }
        };
    }
}