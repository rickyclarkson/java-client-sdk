package uk.org.netvu.filter;

import java.awt.Image;

/**
 * A filter that takes in one {@link Image} and returns another after some processing.
 * The original <code>Image</code> is not changed.
 */
public interface ImageFilter
{
    /**
     * Processes the specified <code>Image</code> to give an <code>Image</code> based on it.
     * 
     * @param source
     *        the <code>Image</code> to process.
     * @return an <code>Image</code> after some processing has been performed on the source
     *         <code>Image</code>.
     */
    Image filter( Image source );
}
