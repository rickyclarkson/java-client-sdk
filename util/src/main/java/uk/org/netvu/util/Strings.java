package uk.org.netvu.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for dealing with Strings.
 */
public final class Strings
{
    /**
     * A named String literal, used to improve the readability of uses of double
     * quotes.
     */
    private static final String DOUBLE_QUOTES = "\"";

    /**
     * Gives the part of the given String that is after the first instance of
     * the given separator, or the whole String if the separator isn't found in
     * the String.
     * 
     * @param string
     *        the String to parse.
     * @param separator
     *        the separator to split on.
     * @throws NullPointerException
     *         if string or separator are null.
     * @return the part of the given String that is after the first instance of
     *         the given separator, or the whole String if the separator isn't
     *         found in the String.
     */
    public static String afterFirstLeniently( final String string, final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() > 1 ? 1 : 0 );
    }

    /**
     * Gives the part of the given String that is after the last instance of the
     * given separator, or the whole string if the separator isn't found in the
     * String.
     * 
     * @param string
     *        the String to parse.
     * @param separator
     *        the separator to split on.
     * @throws NullPointerException
     *         if string or separator are null.
     * @return the part of the given String that is after the last instance of
     *         the given separator, or the whole string if the separator isn't
     *         found in the string.
     */
    public static String afterLastLeniently( final String string, final String separator )
    {
        final List<String> list = partitionLeniently( string, separator );
        return list.get( list.size() - 1 );
    }

    /**
     * Gives the part of the given String that is before the first instance of
     * the given separator, or the whole string if the separator isn't found in
     * the String.
     * 
     * @param string
     *        the String to parse.
     * @param separator
     *        the separator to split on.
     * @throws NullPointerException
     *         if string or separator are null.
     * @return the part of the given String that is before the first instance of
     *         the given separator, or the whole string if the separator isn't
     *         found in the String.
     */
    public static String beforeFirstLeniently( final String string, final String separator )
    {
        return partitionLeniently( string, separator ).get( 0 );
    }

    /**
     * Gives the substring of the specified String after the specified char.
     * 
     * @param c
     *        the char to search for.
     * @param string
     *        the String to give a substring of.
     * @throws NullPointerException
     *         if string is null.
     * @return the substring of the specified String after the specified char.
     */
    public static String fromFirst( final char c, final String string )
    {
        return string.substring( string.indexOf( c ) + 1 );
    }

    /**
     * Intersperses a List of Strings with a separator.
     * 
     * @param separator
     *        the separator to insert.
     * @param strings
     *        the List to intersperse.
     * @throws NullPointerException
     *         if separator or strings are null.
     * @return the interspersed String.
     */
    public static String intersperse( final String separator, final List<String> strings )
    {
        CheckParameters.areNotNull( separator );

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

    /**
     * Gives a Function that can split a given String into two parts around the
     * given separator.
     * 
     * @param separator
     *        the separator to use.
     * @return a Function that can split a given String into two parts around
     *         the given separator.
     */
    public static Function<String, Pair<String, String>> partition( final char separator )
    {
        return new Function<String, Pair<String, String>>()
        {
            @Override
            public Pair<String, String> apply( final String param )
            {
                return new Pair<String, String>( param.substring( 0, param.indexOf( separator ) ),
                        param.substring( param.indexOf( separator ) + 1 ) );
            }
        };
    }

    /**
     * Removes leading and trailing double quotes from a String. If they don't
     * exist then it returns the original String.
     * 
     * @param value
     *        the String to trim double quotes from.
     * @return a String without leading and trailing double quotes.
     */
    public static String removeSurroundingQuotesLeniently( final String value )
    {
        return value.startsWith( DOUBLE_QUOTES ) && value.endsWith( DOUBLE_QUOTES ) ? value.substring( 1,
                value.length() - 1 ) : value;
    }

    /**
     * Splits a comma-separated String into an array of Strings, ensuring that
     * whitespace after commas is ignored.
     * 
     * @param line
     *        the line to split.
     * @throws NullPointerException
     *         if line is null.
     * @return an array of Strings.
     */
    public static String[] splitCSV( final String line )
    {
        return line.replaceAll( ",([^ ])", ", $1" ).split( ", " );
    }

    /**
     * Splits a String on a specified character, treating quoted sections
     * containing the character as one item.
     * 
     * @param string
     *        the String to split.
     * @param separator
     *        the character to look for to separate on.
     * @throws NullPointerException
     *         if string is null.
     * @return a List of Strings that, in the original String, were before and
     *         after the separator.
     */
    public static List<String> splitIgnoringQuotedSections( final String string, final char separator )
    {
        final List<String> results = new ArrayList<String>( Arrays.asList( "" ) );

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
                results.set( results.size() - 1, results.get( results.size() - 1 ) + c );
            }
        }

        return results;
    }

    /**
     * A Function that will surround any String passed to it with double quotes.
     * 
     * @return a Function that will surround any String passed to it with double
     *         quotes.
     */
    public static Function<String, String> surroundWithQuotes()
    {
        return prepend( DOUBLE_QUOTES ).andThen( append( DOUBLE_QUOTES ) );
    }

    /**
     * Gives a Function that will append the specified String to its input.
     * 
     * @param toAppend
     *        the String to append. This is never null.
     * @return a Function that will append the specified String to its input.
     */
    private static Function<String, String> append( final String toAppend )
    {
        return new Function<String, String>()
        {
            @Override
            public String apply( final String t )
            {
                return t + toAppend;
            }
        };
    }

    /**
     * Partitions a String into 2 parts on a separator. If that separator is not
     * found, the returned List has one element, the original String.
     * 
     * @param string
     *        the String to partition. This is never null.
     * @param separator
     *        the String that separates the two parts of the first String. This
     *        is never null.
     * @return a List containing the text to the left of the separator, and the
     *         text to the right of the separator, if any.
     */
    private static List<String> partitionLeniently( final String string, final String separator )
    {
        final int index = string.indexOf( separator );

        if ( index == string.length() - 1 )
        {
            return Arrays.asList( "" );
        }

        return index >= 0 ? Arrays.asList( string.substring( 0, index ), string.substring( index + 1 ) )
                : Arrays.asList( string );
    }

    /**
     * Gives a Function that will prepend the given String to its input.
     * 
     * @param toPrepend
     *        the String to prepend. This is never null.
     * @return a Function that will prepend the given String to its input.
     */
    private static Function<String, String> prepend( final String toPrepend )
    {
        return new Function<String, String>()
        {
            @Override
            public String apply( final String t )
            {
                CheckParameters.areNotNull( t );

                return toPrepend + t;
            }
        };
    }

    /**
     * Private to prevent instantiation.
     */
    private Strings()
    {
    }
}
