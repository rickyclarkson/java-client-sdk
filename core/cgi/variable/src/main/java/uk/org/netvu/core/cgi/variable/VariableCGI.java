package uk.org.netvu.core.cgi.variable;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.TwoWayConversion;

/**
 * Builds and parses variable.cgi URLs.
 */
public final class VariableCGI
{
    private final ParameterMap parameterMap;

    /**
     * Constructs a VariableCGI using the values from the given ParameterMap.
     * 
     * @param parameterMap
     *        the map containing the values to use.
     * @throws IllegalStateException
     *         if the ParameterMap does not contain a value for the VARIABLE
     *         parameter.
     */
    VariableCGI( final ParameterMap parameterMap )
    {
        if ( parameterMap.get( VARIABLE ).isNone() )
        {
            throw new IllegalStateException( VARIABLE.name
                    + " has not been set to a value" );
        }

        this.parameterMap = parameterMap;
    }

    private static final Parameter<Variable, Option<Variable>> VARIABLE = Parameter.parameter(
            "variable", "Name of the variable",
            TwoWayConversion.convenientPartial( Variable.fromString ) );

    private static final Parameter<VariableType, VariableType> TYPE = Parameter.parameterWithDefault(
            "type", "Specifies return type of variable", VariableType.HTTP,
            TwoWayConversion.convenientPartial( VariableType.fromString ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
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
        private Option<ParameterMap> parameterMap = Option.some( new ParameterMap() );

        /**
         * Sets the variable parameter to the specified Variable.
         * 
         * @param variable
         *        the Variable to store.
         * @return the Builder.
         */
        public Builder variable( final Variable variable )
        {
            parameterMap = parameterMap.map( ParameterMap.setter( VARIABLE,
                    variable ) );
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
            parameterMap = parameterMap.map( ParameterMap.setter( TYPE, type ) );
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
                return new VariableCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.none( "This Builder has already been built once." );
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
        Option<ParameterMap> map = ParameterMap.fromURL( url, params );
        if ( map.isNone() )
            throw new IllegalArgumentException( "Cannot parse " + url
                    + " into a VariableCGI because " + map.reason() );

        return new VariableCGI( map.get() );
    }
}
