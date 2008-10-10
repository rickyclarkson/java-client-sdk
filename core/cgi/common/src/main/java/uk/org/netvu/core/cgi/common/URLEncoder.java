package uk.org.netvu.core.cgi.common;

import java.io.UnsupportedEncodingException;

/**
 * Utility methods for encoding URLs. For internal use only.
 */
public class URLEncoder
{
    /**
     * Encodes the specified value for safe use in URLs.
     * 
     * @param value
     *        the value to encode.
     * @return the encoded value.
     */
    public static String encode( final String value )
    {
        try
        {
            return java.net.URLEncoder.encode( value, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Unsupported Encoding: UTF-8" );
        }
    }

    /**
     * A Conversion that, given a String, returns a URL-encoded version of it.
     * UTF-8 is assumed.
     */
    public static Conversion<String, String> encodeConversion()
    {
        return new Conversion<String, String>()
        {
            @Override
            public String convert( final String value )
            {
                return encode( value );
            }
        };
    }

    private URLEncoder()
    {
    }
}
