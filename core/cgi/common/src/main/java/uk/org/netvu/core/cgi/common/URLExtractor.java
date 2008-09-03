package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;

public class URLExtractor
{
    public static String queryName( final String url )
    {
        return Strings.afterLastLeniently( Strings.beforeFirstLeniently( url,
                "?" ), "/" );
    }

    public static List<String> parameters( final String url )
    {
        return Strings.splitIgnoringQuotedSections(
                Strings.afterFirstLeniently( url, "?" ), '&' );
    }

    public static List<URLParameter> nameValuePairs( final String url )
    {
        return url.indexOf( '=' ) < 0 ? new ArrayList<URLParameter>()
                : Iterables.map(
                        parameters( url ),
                        Strings.partition( "=" ).andThen( URLParameter.fromPair ) );
    }
}
