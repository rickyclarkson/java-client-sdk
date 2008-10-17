package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Function;
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
     * A Function that, given a String, will produce an Option containing
     * SHORT or LONG if the String matches it (ignoring case), and an empty
     * Option otherwise.
     */
    static final Function<String, Option<DirectoryPathFormat>> fromString = new Function<String, Option<DirectoryPathFormat>>()
    {
        @Override
        public Option<DirectoryPathFormat> apply( final String t )
        {
            try
            {
                return Option.getFullOption( DirectoryPathFormat.valueOf( t.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.getEmptyOption( t
                        + " is not a valid DirectoryPathFormat" );
            }
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
