package uk.org.netvu.protocol;

/**
 * Specifies whether a DecoderCGI's query sets variables temporarily or
 * persistently.
 */
public enum Persistence
{
    /**
     * Sets variables persistently (on-disk).
     */
    PERSISTENT( ".frm" ),

    /**
     * Sets variables temporarily (in-memory).
     */
    TEMPORARY( ".var" );

    private final String extension;

    Persistence( final String extension )
    {
        this.extension = extension;
    }

    /**
     * Gives ".frm" for PERSISTENT and ".var" for TEMPORARY.
     */
    @Override
    public String toString()
    {
        return extension;
    }
}
