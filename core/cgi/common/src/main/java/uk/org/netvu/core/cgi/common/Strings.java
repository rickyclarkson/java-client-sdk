package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for dealing with Strings. For internal use only.
 */
public class Strings
{
    /**
     * Gives the substring of the specified String after the specified char.
     * 
     * @param c
     *        the char to search for.
     * @param string
     *        the String to give a substring of.
     * @return the substring of the specified String after the specified char.
     */
    public static String fromFirst( final char c, final String string )
    {
        return string.substring( string.indexOf( c ) + 1 );
    }

    /**
     * Splits a comma-separated String into an array of Strings, ensuring that
     * whitespace after commas is ignored.
     * 
     * @param line
     *        the line to split.
     * @return an array of Strings.
     */
    public static String[] split( final String line )
    {
        return line.replaceAll( ",([^ ])", ", $1" ).split( ", " );
    }

    /**
     * Intersperses a List of Strings with a separator.
     * 
     * @param separator
     *        the separator to insert.
     * @param strings
     *        the List to intersperse.
     * @return the interspersed String.
     */
    public static String intersperse( final String separator,
            final List<String> strings )
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

    private static List<String> partitionLeniently( final String string,
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

    static String beforeFirstLeniently( final String string,
            final String separator )
    {
        return partitionLeniently( string, separator ).get( 0 );
    }

    static String afterLastLeniently( final String string,
            final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() - 1 );
    }

    static String afterFirstLeniently( final String string,
            final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() > 1 ? 1 : 0 );
    }

    /**
     * Splits a String on a specified character, treating quoted sections
     * containing the character as one item.
     * 
     * @param string
     *        the String to split.
     * @param separator
     *        the character to look for to separate on.
     * @return a List of Strings that, in the original String, were before and
     *         after the separator.
     */
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

    static Conversion<String, Pair<String, String>> partition(
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

    static String removeSurroundingQuotesLeniently( final String value )
    {
        return value.startsWith( "\"" ) && value.endsWith( "\"" ) ? value.substring(
                1, value.length() - 1 )
                : value;
    }

    private static Conversion<String, String> append( final String toAppend )
    {
        return new Conversion<String, String>()
        {
            @Override
            public String convert( final String t )
            {
                return t + toAppend;
            }
        };
    }

    private static Conversion<String, String> prepend( final String toPrepend )
    {
        return new Conversion<String, String>()
        {
            @Override
            public String convert( final String t )
            {
                return toPrepend + t;
            }
        };
    }

    /**
     * A Conversion that will surround any String passed to it with double
     * quotes.
     */
    public static final Conversion<String, String> surroundWithQuotes = prepend(
            "\"" ).andThen( append( "\"" ) );

    /**
     * Constructs a ReversibleReplace capable of replacing the String toReplace
     * with the String 'with', and the reverse, in any Strings passed to it.
     * 
     * @param toReplace
     *        the String to replace.
     * @param with
     *        the String to replace with.
     * @return a ReversibleReplace capable of replacing the String toReplace
     *         with the String 'with' and the reverse.
     */
    public static ReversibleReplace reversibleReplace( final String toReplace,
            final String with )
    {
        return new ReversibleReplace()
        {
            public String replace( final String s )
            {
                return s.replaceAll( toReplace, with );
            }

            public String undo( final String s )
            {
                return s.replaceAll( with, toReplace );
            }
        };
    }
}
