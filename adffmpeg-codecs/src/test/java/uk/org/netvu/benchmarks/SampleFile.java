package uk.org.netvu.benchmarks;

import uk.org.netvu.util.CheckParameters;

/**
 * Information about a sample image file.
 */
class SampleFile
{
    /**
     * The width of the image.
     */
    final int width;

    /**
     * The height of the image.
     */
    final int height;

    /**
     * The filename of the image.
     */
    final String filename;

    /**
     * Constructs a SampleFile with the specified width, height and filename.
     * 
     * @param width
     *        the width of the image.
     * @param height
     *        the height of the image.
     * @param filename
     *        the filename of the image.
     * @throws NullPointerException if filename is null.
     */
    SampleFile( final int width, final int height, final String filename )
    {
        CheckParameters.areNotNull( filename );
        this.width = width;
        this.height = height;
        this.filename = filename;
    }
}
