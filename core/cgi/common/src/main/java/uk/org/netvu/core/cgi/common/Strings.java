package uk.org.netvu.core.cgi.common;

public class Strings
{
    public static String fromLast( final char c, final String string )
    {
        return string.contains( String.valueOf( c ) ) ? string.substring( string.lastIndexOf( c ) + 1 )
                : string;
    }

    public static String[] split( final String line )
    {
        return line.replaceAll( ",([^ ])", ", $1" ).split( ", " );
    }

    public static String intersperse( final String separator,
            final String[] strings )
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
}
