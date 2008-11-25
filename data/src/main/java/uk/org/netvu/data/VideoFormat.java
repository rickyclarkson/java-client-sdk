package uk.org.netvu.data;

/**
 * A representation of the video format as contained in the ImageDataStruct.
 */
public enum VideoFormat
{
    /**
     * A JPEG with a 4:2:2 colorspace.
     */
    JPEG_422( 0 ),
    /**
     * A JPEG with a 4:1:1 colorspace.
     */
    JPEG_411( 1 ),
    /**
     * An MPEG-4 P-frame.
     */
    MPEG4_P_FRAME( 2 ),
    /**
     * An MPEG-4 I-frame.
     */
    MPEG4_I_FRAME( 3 ), MPEG4_GOV_P_FRAME( 4 ), MPEG4_GOV_I_FRAME( 5 );

    /**
     * Finds a VideoFormat whose value is the same as the specified value.
     * 
     * @param i
     *        the value to search for.
     * @return the VideoFormat whose value is the same as the specified value.
     * @throw IllegalArgumentException if there is no such VideoFormat.
     */
    public static VideoFormat valueOf( final int i )
    {
        for ( final VideoFormat format : values() )
        {
            if ( format.index == i )
            {
                return format;
            }
        }
        throw new IllegalArgumentException( Integer.toHexString( i ) + " is not a supported video format" );
    }

    /**
     * The numerical value associated with this VideoFormat.
     */
    final int index;

    /**
     * Constructs a VideoFormat with the specified index as its numerical value.
     * 
     * @param index
     *        the value to associate with this VideoFormat.
     */
    VideoFormat( final int index )
    {
        this.index = index;
    }
}
