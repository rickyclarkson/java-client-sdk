package uk.org.netvu.core.cgi.common;

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

    public static Format oneOf( final Random random )
    {
        return random.nextInt( 3 ) == 0 ? JS : random.nextInt( 2 ) == 0 ? HTML
                : CSV;
    }

    public static final Conversion<String, Format> fromString = new Conversion<String, Format>()
    {
        @Override
        public Format convert( final String t )
        {
            return Format.valueOf( t.toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
