package uk.org.netvu.protocol;

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
    static final Function<String, Option<VariableType>> fromString = new Function<String, Option<VariableType>>()
    {
        @Override
        public Option<VariableType> apply( final String t )
        {
            try
            {
                return Option.getFullOption( VariableType.valueOf( t.toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.getEmptyOption( "Cannot parse " + t + " into a VariableType" );
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
