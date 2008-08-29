package uk.org.netvu.core.cgi.variable;

import uk.org.netvu.core.cgi.common.Conversion;

public enum VariableType
{
    HTTP, INCLUDE;

    public static final Conversion<String, VariableType> fromString = new Conversion<String, VariableType>()
    {
        @Override
        public VariableType convert( final String t )
        {
            return VariableType.valueOf( t.toUpperCase() );
        }
    };
}
