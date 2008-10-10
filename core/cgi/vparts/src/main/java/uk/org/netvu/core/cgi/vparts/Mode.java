package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Option;

/**
 * Determines the function of the CGI call.
 */
public enum Mode
{
    /**
     * Supplies a javascript array or comma separated variable list of video
     * partition information.
     */
    READ,

    /**
     * Manual protect/unprotect of video partitions. The time/range/expiry
     * parameters are used to set/clear expiration times on all stored video
     * data in the specified time range. If the expiry time is specified as 0
     * then any existing protection is removed
     */
    PROTECT,

    /**
     * Reset video partitions.
     */
    REINITIALISE;

    /**
     * Converts a String to a Mode if it matches a Mode's name
     * (case-insensitive), returning it in an Option if it does, and returning an empty Option otherwise.
     */
    static Conversion<String, Option<Mode>> fromString = new Conversion<String, Option<Mode>>()
    {
        @Override
        public Option<Mode> convert( final String s )
        {
            try
            {
                return Option.some( Mode.valueOf( s.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.none( s + " is not a valid Mode" );
            }
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
