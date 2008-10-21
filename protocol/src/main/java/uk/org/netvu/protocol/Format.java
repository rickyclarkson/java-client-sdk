package uk.org.netvu.protocol;

import java.util.Locale;
import java.util.Random;

/**
 * The format of the results from the events database.
 */
public enum Format
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
     * A Function that converts Strings to Formats according to the String
     * representation of Formats.
     */
    public static final Function<String, Option<Format>> fromString = new Function<String, Option<Format>>()
    {
        @Override
        public Option<Format> apply( final String t )
        {
            try
            {
                return Option.getFullOption( Format.valueOf( t.toUpperCase( Locale.ENGLISH ) ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.getEmptyOption( t + " is not a valid Format" );
            }
        }
    };

    /**
     * Gives a random Format. For internal use only.
     * 
     * @param random
     *        the random number generator to use.
     * @return a random Format.
     */
    public static Format oneOf( final Random random )
    {
        if ( random.nextInt( 3 ) == 0 )
        {
            return JS;
        }
        if ( random.nextInt( 2 ) == 0 )
        {
            return HTML;
        }
        return CSV;
    }

    /**
     * Gives the name of the Format in lowercase.
     */
    @Override
    public String toString()
    {
        return super.toString().toLowerCase( Locale.ENGLISH );
    }
}
