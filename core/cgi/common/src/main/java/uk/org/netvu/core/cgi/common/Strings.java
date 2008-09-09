package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Strings
{
    public static String fromFirst( final char c, final String string )
    {
        return string.substring( string.indexOf( '?' ) + 1 );
    }

    public static String[] split( final String line )
    {
        return line.replaceAll( ",([^ ])", ", $1" ).split( ", " );
    }

    public static String intersperse( final String separator,
            final Iterable<String> strings )
    {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;

        for ( final String string : strings )
        {
            if ( !first )
            {
                builder.append( separator );
            }
            else
            {
                first = false;
            }

            builder.append( string );
        }

        return builder.toString();
    }

    public static List<String> partitionLeniently( final String string,
            final String separator )
    {
        final int index = string.indexOf( separator );

        if ( index == string.length() - 1 )
        {
            return Arrays.asList( "" );
        }

        return index >= 0 ? Arrays.asList( string.substring( 0, index ),
                string.substring( index + 1 ) ) : Arrays.asList( string );
    }

    public static String beforeFirstLeniently( final String string,
            final String separator )
    {
        return partitionLeniently( string, separator ).get( 0 );
    }

    public static String afterLastLeniently( final String string,
            final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() - 1 );
    }

    public static String afterFirstLeniently( final String string,
            final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() > 1 ? 1 : 0 );
    }

    public static List<String> splitIgnoringQuotedSections(
            final String string, final char separator )
    {
        final List<String> results = new ArrayList<String>()
        {
            {
                add( "" );
            }
        };

        boolean insideQuotes = false;

        for ( final char c : string.toCharArray() )
        {
            if ( c == '"' )
            {
                insideQuotes = !insideQuotes;
            }

            if ( c == separator && !insideQuotes )
            {
                results.add( "" );
            }
            else
            {
                results.set( results.size() - 1,
                        results.get( results.size() - 1 ) + c );
            }
        }

        return results;
    }

    public static Conversion<String, Pair<String, String>> partition(
            final String string )
    {
        return new Conversion<String, Pair<String, String>>()
        {
            @Override
            public Pair<String, String> convert( final String param )
            {
                return Pair.pair( param.substring( 0, param.indexOf( '=' ) ),
                        param.substring( param.indexOf( '=' ) + 1 ) );
            }
        };
    }

    public static String removeSurroundingQuotesLeniently( final String value )
    {
        return value.startsWith( "\"" ) && value.endsWith( "\"" ) ? value.substring(
                1, value.length() - 1 )
                : value;
    }
}
