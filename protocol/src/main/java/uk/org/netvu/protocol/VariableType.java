package uk.org.netvu.protocol;

import java.util.Locale;

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
     * A Function that, given a String, will return an Option containing HTTP or
     * INCLUDE if the String case-insensitively matches one of those, and an
     * empty Option otherwise.
     */
    static Function<String, Option<VariableType>> functionFromStringToVariableType()
    {
        return new Function<String, Option<VariableType>>()
        {
            @Override
            public Option<VariableType> apply( final String t )
            {
                try
                {
                    return Option.getFullOption( VariableType.valueOf( t.toUpperCase( Locale.ENGLISH ) ) );
                }
                catch ( final IllegalArgumentException exception )
                {
                    return Option.getEmptyOption( "Cannot parse " + t + " into a VariableType" );
                }
            }
        };
    }

    /**
     * Returns the name of the enum constant, in lowercase.
     */
    @Override
    public String toString()
    {
        return super.toString().toLowerCase( Locale.ENGLISH );
    }
}
