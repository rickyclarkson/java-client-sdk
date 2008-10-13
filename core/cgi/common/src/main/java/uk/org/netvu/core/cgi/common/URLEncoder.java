package uk.org.netvu.core.cgi.common;

import java.io.UnsupportedEncodingException;

/**
 * A Conversion for encoding text for safe use in URLs.  For internal use only.
 */
public class URLEncoder extends Conversion<String, String>
{
    /**
     * Encodes the specified value for safe use in URLs.
     * 
     * @param value
     *        the value to encode.
     * @return the encoded value.
     */
    public String convert( final String value )
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

    public URLEncoder()
    {
    }
}