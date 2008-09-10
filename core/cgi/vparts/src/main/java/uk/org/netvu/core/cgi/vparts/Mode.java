package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;

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

    static Conversion<String, Mode> fromString = new Conversion<String, Mode>()
    {
        @Override
        public Mode convert( final String s )
        {
            return Mode.valueOf( s.toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
