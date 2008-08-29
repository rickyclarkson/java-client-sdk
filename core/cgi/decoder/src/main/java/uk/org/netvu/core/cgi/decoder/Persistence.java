package uk.org.netvu.core.cgi.decoder;

public enum Persistence
{
    PERSISTENT( ".frm" ), TEMPORARY( ".var" );

    private final String extension;

    Persistence( final String extension )
    {
        this.extension = extension;
    }
}
