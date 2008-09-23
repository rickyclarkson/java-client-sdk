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

    /**
     * A Conversion that, given a String, will produce a Some containing SHORT
     * or LONG if the String matches it (ignoring case), and a None otherwise.
     */
    static final Conversion<String, Option<DirectoryPathFormat>> fromString = new Conversion<String, Option<DirectoryPathFormat>>()
    {
        @Override
        public Option<DirectoryPathFormat> convert( final String t )
        {
            try
            {
                return Option.some( DirectoryPathFormat.valueOf( t.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.none();
            }
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
