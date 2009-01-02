package uk.org.netvu.protocol;

import java.io.UnsupportedEncodingException;

import uk.org.netvu.util.CheckParameters;

/**
 * A Function that encodes text for safe use in URLs.
 */
public final class URLEncoder
        extends Function<String, String>
{
    /**
     * Constructs a URLEncoder.
     */
    public URLEncoder()
    {
    }

    /**
     * Encodes the specified value for safe use in URLs.
     * 
     * @param value
     *        the value to encode.
     * @throws NullPointerException
     *         if value is null.
     * @return the encoded value.
     */
    @Override
    public String apply( final String value )
    {
        CheckParameters.areNotNull( value );
        try
        {
            return java.net.URLEncoder.encode( value, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Unsupported Encoding: UTF-8" );
        }
    }
}
