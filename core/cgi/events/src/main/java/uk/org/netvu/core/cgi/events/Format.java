package uk.org.netvu.core.cgi.events;

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

    static Format oneOf( final Random random )
    {
        return random.nextInt( 3 ) == 0 ? JS : random.nextInt( 2 ) == 0 ? HTML
                : CSV;
    }
}
