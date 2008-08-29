package uk.org.netvu.core.cgi.decoder;

public enum Layout
{
    SINGLE, FOUR_WAY, NINE_WAY, SIXTEEN_WAY;

    @Override
    public String toString()
    {
        return super.toString().toLowerCase().replaceAll( "_", " " );
    }
}
