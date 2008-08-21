package uk.org.netvu.core.cgi.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLBuilder
{
    private final String start;

    public URLBuilder( final String start )
    {
        this.start = start;
    }

    public URLBuilder withParam( final String name, final Object value )
    {
        try
        {
            return new URLBuilder( start + ( start.contains( "=" ) ? "&" : "" )
                    + URLEncoder.encode( name, "UTF-8" ) + '='
                    + URLEncoder.encode( value.toString(), "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw null;
        }
    }

    @Override
    public String toString()
    {
        return start;
    }

    public <T, U extends T> URLBuilder withOptionalParam( final String string,
            final T value, final U defaultValue )
    {
        return value.equals( defaultValue ) ? this : withParam( string, value );
    }
}
