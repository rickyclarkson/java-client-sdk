package uk.org.netvu.core.cgi.variable;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;

/**
 * Builds and parses variable.cgi URLs.
 */
public final class VariableCGI
{
    private final ParameterMap parameterMap;

    VariableCGI( final ParameterMap parameterMap )
    {
        if ( parameterMap.get( VARIABLE ).isNone() )
        {
            throw new IllegalStateException( VARIABLE.getName()
                    + " has not been set to a value" );
        }

        this.parameterMap = parameterMap;
    }

    private static final Parameter<Variable, Option<Variable>> VARIABLE = Parameter.param(
            "variable", "Name of the variable", Variable.fromString );

    private static final Parameter<VariableType, VariableType> TYPE = Parameter.param(
            "type", "Specifies return type of variable", VariableType.HTTP,
            VariableType.fromString );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( VARIABLE );
            add( TYPE );
        }
    };

    /**
     * A Builder to create VariableCGIs.
     */
    public static final class Builder
    {
        Option<ParameterMap> real = new Option.Some<ParameterMap>(
                new ParameterMap() );

        /**
         * Sets the variable parameter to the specified Variable.
         * 
         * @param variable
         *        the Variable to store.
         * @return the Builder.
         */
        public Builder variable( final Variable variable )
        {
            real = real.map( ParameterMap.withRef( VARIABLE, variable ) );
            return this;
        }

        /**
         * Sets the type parameter to the specified VariableType.
         * 
         * @param type
         *        the VariableType to store.
         * @return the Builder.
         */
        public Builder type( final VariableType type )
        {
            real = real.map( ParameterMap.withRef( TYPE, type ) );
            return this;
        }

        /**
         * Builds a VariableCGI with the stored values.
         * 
         * @return a VariableCGI containing the stored values.
         */
        public VariableCGI build()
        {
            try
            {
                return new VariableCGI( real.get() );
            }
            finally
            {
                real = new Option.None<ParameterMap>();
            }
        }
    }

    /**
     * Gets the stored Variable.
     * 
     * @return the stored Variable.
     */
    public Variable getVariable()
    {
        return parameterMap.get( VARIABLE ).get();
    }

    /**
     * Gets the stored VariableType.
     * 
     * @return the stored VariableType.
     */
    public VariableType getType()
    {
        return parameterMap.get( TYPE );
    }

    @Override
    public String toString()
    {
        return "/variable.cgi?" + parameterMap.toURLParameters( params );
    }

    /**
     * Parses a variable.cgi request, returning a VariableCGI holding the
     * request's values.
     * 
     * @param url
     *        the request to parse.
     * @return a VariableCGI holding the request's values.
     */
    public static VariableCGI fromString( final String url )
    {
        return new VariableCGI( ParameterMap.fromURL( url, params ) );
    }
}
