package uk.org.netvu.core.cgi.decoder;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Persistence
{
    PERSISTENT( ".frm" ), TEMPORARY( ".var" );

    private final String extension;

    Persistence( final String extension )
    {
        this.extension = extension;
    }

    @Override
    public String toString()
    {
        return extension;
    }

    static final Conversion<String, Persistence> fromString = new Conversion<String, Persistence>()
    {
        @Override
        public Persistence convert( final String s )
        {
            return s.equals( PERSISTENT.extension ) ? PERSISTENT : TEMPORARY;
        }
    };
}
