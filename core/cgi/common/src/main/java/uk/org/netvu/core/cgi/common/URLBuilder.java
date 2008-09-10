package uk.org.netvu.core.cgi.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility methods for encoding URLs. For internal use only.
 */
public class URLBuilder
{
    static final Conversion<String, String> encode = new Conversion<String, String>()
    {
        @Override
        public String convert( final String unencoded )
        {
            return encode( unencoded );
        }
    };

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
            return URLEncoder.encode( value, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw null;
        }
    }
}
