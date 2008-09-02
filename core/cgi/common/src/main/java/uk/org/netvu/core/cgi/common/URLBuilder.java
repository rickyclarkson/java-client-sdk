package uk.org.netvu.core.cgi.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLBuilder
{
    public static final Conversion<String, String> encode = new Conversion<String, String>()
    {
        @Override
        public String convert( final String unencoded )
        {
            return encode( unencoded );
        }
    };

    public static String param( final String name, final String encodedValue )
    {
        return name + "=" + encodedValue;
    }

    public static String encode( final String value )
    {
        try
        {
            return URLEncoder.encode( value, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw null;
        }
    }
}
