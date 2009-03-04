package uk.org.netvu.filter;

import java.awt.Image;

/**
 * A filter that takes in one Image and returns another after some processing.
 * The original Image is not changed.
 */
public interface ImageFilter
{
    /**
     * Processes the specified Image to give an Image based on it.
     * 
     * @param source
     *        the Image to process.
     * @return an Image after some processing has been performed on the source
     *         Image.
     */
    Image filter( Image source );
}
