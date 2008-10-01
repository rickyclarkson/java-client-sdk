package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for parsing Strings into URL parts. For internal use only.
 */
final class URLExtractor
{
    /**
     * Parses key-value pairs separated by &, and internally separated by =,
     * into a List of URLParameters.
     * 
     * @param url
     *        the URL or URL-part to parse.
     * @return a List of URLParameters parsed from the given String.
     */
    static List<URLParameter> nameValuePairs( final String url )
    {
        return url.indexOf( '=' ) < 0 ? new ArrayList<URLParameter>()
                : Lists.map(
                        parameters( url ),
                        Strings.partition( '=' ).andThen( URLParameter.fromPair ) );
    }

    /**
     * Parses a URL to get the X, Y, Z parts, the query parameters, in this
     * example: http://foo.bar/baz/spam?eggs?X&Y&Z. It treats quoted sections as
     * whole parts (ampersands inside them are ignored).
     * 
     * @param url
     *        the URL to split.
     * @return a List containing each parameter as a String.
     */
    static List<String> parameters( final String url )
    {
        return Strings.splitIgnoringQuotedSections(
                Strings.afterFirstLeniently( url, "?" ), '&' );
    }

    /**
     * Parses a URL to get the X part, the name of the query, in this example:
     * http://foo.bar/baz/spam/eggs/X?a=b It looks for anything between the last
     * / and the first ?, and if one or both of those is not present, it
     * defaults to the beginning and end of the String respectively.
     * 
     * @param url
     *        the URL to parse.
     * @return the name of the query that the URL points to.
     */
    static String queryName( final String url )
    {
        return Strings.afterLastLeniently( Strings.beforeFirstLeniently( url,
                "?" ), "/" );
    }

    private URLExtractor()
    {
    }
}
