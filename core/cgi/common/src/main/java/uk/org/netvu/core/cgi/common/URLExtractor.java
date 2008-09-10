package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;

class URLExtractor
{
    static String queryName( final String url )
    {
        return Strings.afterLastLeniently( Strings.beforeFirstLeniently( url,
                "?" ), "/" );
    }

    static List<String> parameters( final String url )
    {
        return Strings.splitIgnoringQuotedSections(
                Strings.afterFirstLeniently( url, "?" ), '&' );
    }

    static List<URLParameter> nameValuePairs( final String url )
    {
        return url.indexOf( '=' ) < 0 ? new ArrayList<URLParameter>()
                : Iterables.map(
                        parameters( url ),
                        Strings.partition( "=" ).andThen( URLParameter.fromPair ) );
    }
}
