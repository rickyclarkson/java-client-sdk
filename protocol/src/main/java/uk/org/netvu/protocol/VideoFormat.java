package uk.org.netvu.protocol;

/**
 * The possible formats that the video stream can be returned as.
 */
public enum VideoFormat
{
    /**
     * Complete JFIF (JPEG) image data.
     */
    JFIF,

    /**
     * Truncated JPEG image data.
     */
    JPEG,

    /**
     * MPEG-4 image data.
     */
    MP4;

    /**
     * A Function that, given a String, will produce an Option containing a
     * member of VideoFormat if the passed-in String matches it (ignoring case),
     * and an empty Option otherwise.
     * 
     * @return a Function that parses a String into a VideoFormat
     */
    static Function<String, Option<VideoFormat>> fromStringFunction()
    {
        return new Function<String, Option<VideoFormat>>()
        {
            @Override
            public Option<VideoFormat> apply( final String s )
            {
                for ( final VideoFormat element : values() )
                {
                    if ( element.toString().equalsIgnoreCase( s ) )
                    {
                        return Option.getFullOption( element );
                    }
                }
                return Option.getEmptyOption( s + " is not a valid VideoFormat element " );
            }
        };
    }

}
