package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

/**
 * A PixelProcessor is responsible for processing pixel data and storing the
 * processed data in an array.
 */
interface PixelProcessor
{
    /**
     * Processes the specified data, storing the processed data in the specified
     * array.
     * 
     * @param pixels
     *        the array to store the resulting pixel data in.
     * @param originalData
     *        the original pixel data.
     */
    void setPixels( int[] pixels, DataBuffer originalData );
}
