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

    @Override
    public String toString()
    {
        return start;
    }

    public URLBuilder param( final Conversion<URLBuilder, URLBuilder> name,
            final Conversion<URLBuilder, URLBuilder> value )
    {
        return value.convert( name.convert( this ).literal( "=" ) );
    }

    public URLBuilder literal( final String string )
    {
        return new URLBuilder( start + string );
    }

    public URLBuilder encoded( final String value )
    {
        try
        {
            return new URLBuilder( start + URLEncoder.encode( value, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw null;
        }
    }

    public static Conversion<URLBuilder, URLBuilder> asLiteral(
            final String value )
    {
        return new Conversion<URLBuilder, URLBuilder>()
        {

            @Override
            public URLBuilder convert( final URLBuilder t )
            {
                return t.literal( value );
            }
        };
    }

    public static Conversion<URLBuilder, URLBuilder> asEncoded(
            final String value )
    {
        return new Conversion<URLBuilder, URLBuilder>()
        {

            @Override
            public URLBuilder convert( final URLBuilder t )
            {
                return t.encoded( value );
            }
        };
    }
}
