package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Option;

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

    static final Conversion<String, Option<DirectoryPathFormat>> fromString = new Conversion<String, Option<DirectoryPathFormat>>()
    {
        @Override
        public Option<DirectoryPathFormat> convert( final String t )
        {
            try
            {
                return new Option.Some<DirectoryPathFormat>(
                        DirectoryPathFormat.valueOf( t.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return new Option.None<DirectoryPathFormat>();
            }
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
