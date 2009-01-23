package uk.org.netvu.protocol;

import java.util.Locale;

/**
 * The format of the results to be returned from a server.
 */
public enum TextResultsFormat
{
    /**
     * A JavaScript snippet that includes the results in a JavaScript object
     * array.
     */
    JS,

    /**
     * A HTML page containing the results in a JavaScript object array and that
     * calls a JavaScript function parent.control.ProcessEvent() when the
     * results list is complete.
     */
    HTML,

    /**
     * The results as a comma separated variable list.
     */
    CSV;

    /**
     * A Function that converts Strings to TextResultsFormats according to the
     * String representation of TextResultsFormats.
     * 
     * @return a Function that converts Strings to TextResultsFormats.
     */
    public static final Function<String, Option<TextResultsFormat>> fromStringFunction()
    {
        return new Function<String, Option<TextResultsFormat>>()
        {
            @Override
            public Option<TextResultsFormat> apply( final String t )
            {
                try
                {
                    return Option.getFullOption( TextResultsFormat.valueOf( t.toUpperCase( Locale.ENGLISH ) ) );
                }
                catch ( final IllegalArgumentException exception )
                {
                    return Option.getEmptyOption( t + " is not a valid TextResultsFormat" );
                }
            }
        };
    }

    /**
     * Gives the name of the TextResultsFormat in lowercase.
     */
    @Override
    public String toString()
    {
        return super.toString().toLowerCase( Locale.ENGLISH );
    }
}
