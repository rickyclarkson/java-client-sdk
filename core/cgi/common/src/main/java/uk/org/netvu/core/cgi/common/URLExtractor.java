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

    public static List<Pair<String, String>> keyValuePairs( final String url )
    {
        return url.indexOf( '=' ) < 0 ? new ArrayList<Pair<String, String>>()
                : Iterables.map( parameters( url ), Strings.partition( "=" ) );
    }
}
