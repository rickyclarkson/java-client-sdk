package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;

/**
 * Specifies the format of directory paths returned by the server.
 */
public enum DirectoryPathFormat
{
    /**
     * The directory paths returned by the server are short.
     */
    SHORT,

    /**
     * The directory paths returned by the server are long.
     */
    LONG;

    static final Conversion<String, DirectoryPathFormat> fromString = new Conversion<String, DirectoryPathFormat>()
    {
        @Override
        public DirectoryPathFormat convert( final String t )
        {
            return DirectoryPathFormat.valueOf( t.toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
