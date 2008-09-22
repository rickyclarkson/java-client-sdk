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
     * (case-insensitive), returning a Some if it does, and a None otherwise.
     */
    static Conversion<String, Option<Mode>> fromString = new Conversion<String, Option<Mode>>()
    {
        @Override
        public Option<Mode> convert( final String s )
        {
            try
            {
                return new Option.Some<Mode>( Mode.valueOf( s.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return new Option.None<Mode>();
            }
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
