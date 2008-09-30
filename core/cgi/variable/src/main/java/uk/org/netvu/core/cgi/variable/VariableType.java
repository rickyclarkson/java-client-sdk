package uk.org.netvu.core.cgi.variable;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Option;

/**
 * The return type of the variable.
 */
public enum VariableType
{
    /**
     * The server will return a MIME header for this type of variable.
     */
    HTTP,

    /**
     * The server will not return a MIME header for this type of variable.
     */
    INCLUDE;

    /**
     * A Conversion that, given a String, will return a Some containing HTTP or
     * INCLUDE if the String case-insensitively matches one of those, and None
     * otherwise.
     */
    static final Conversion<String, Option<VariableType>> fromString = new Conversion<String, Option<VariableType>>()
    {
        @Override
        public Option<VariableType> convert( final String t )
        {
            try
            {
                return Option.some( VariableType.valueOf( t.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.none( "Cannot parse " + t
                        + " into a VariableType" );
            }
        }
    };

    /**
     * Returns the name of the enum constant, lowercase.
     */
    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
