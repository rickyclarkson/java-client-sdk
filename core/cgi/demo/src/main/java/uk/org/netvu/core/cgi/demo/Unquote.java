/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import uk.org.netvu.core.cgi.common.Conversion;

final class Unquote
        extends Conversion<String, String>
{
    @Override
    public String convert( final String quoted )
    {
        return quoted.substring( 1, quoted.length() - 1 );
    }
}