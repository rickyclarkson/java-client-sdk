package uk.org.netvu.core.cgi.variable;

import uk.org.netvu.core.cgi.common.Conversion;

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

    static final Conversion<String, VariableType> fromString = new Conversion<String, VariableType>()
    {
        @Override
        public VariableType convert( final String t )
        {
            return VariableType.valueOf( t.toUpperCase() );
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
