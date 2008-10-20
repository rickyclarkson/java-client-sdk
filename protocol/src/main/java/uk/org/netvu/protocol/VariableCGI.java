package uk.org.netvu.core.cgi.variable;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Function;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.ParameterDescription;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.StringConversion;

/**
 * Builds and parses variable.cgi URLs.
 */
public final class VariableCGI
{
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
        final Option<ParameterMap> map = ParameterMap.fromURL( url, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( "Cannot parse " + url + " into a VariableCGI because " + map.reason() );
        }

        return new VariableCGI( map.get() );
    }

    private final ParameterMap parameterMap;

    private static final ParameterDescription<Variable, Option<Variable>> VARIABLE =
            ParameterDescription.parameterWithoutDefault( "variable",
                    StringConversion.convenientPartial( Variable.fromString ) );

    private static final ParameterDescription<VariableType, VariableType> TYPE =
            ParameterDescription.parameterWithDefault( "type", VariableType.HTTP,
                    StringConversion.convenientPartial( VariableType.fromString ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            add( VARIABLE );
            add( TYPE );
        }
    };

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
        if ( parameterMap.get( VARIABLE ).isEmpty() )
        {
            throw new IllegalStateException( VARIABLE.name + " has not been set to a value" );
        }

        this.parameterMap = parameterMap;
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

    /**
     * Gets the stored Variable.
     * 
     * @return the stored Variable.
     */
    public Variable getVariable()
    {
        return parameterMap.get( VARIABLE ).get();
    }

    @Override
    public String toString()
    {
        return "/variable.cgi?" + parameterMap.toURLParameters( params );
    }

    /**
     * A Builder to create VariableCGIs.
     */
    public static final class Builder
    {
        private Option<ParameterMap> real = Option.getFullOption( new ParameterMap() );

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
                real = Option.getEmptyOption( "This Builder has already been built once." );
            }
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
            return set( TYPE, type );
        }

        /**
         * Sets the variable parameter to the specified Variable.
         * 
         * @param variable
         *        the Variable to store.
         * @return the Builder.
         */
        public Builder variable( final Variable variable )
        {
            return set( VARIABLE, variable );
        }

        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( real.isEmpty() )
            {
                throw new IllegalStateException( "The Builder has already been built (build() has been called on it)." );
            }

            real = real.map( new Function<ParameterMap, ParameterMap>()
            {
                @Override
                public ParameterMap apply( final ParameterMap map )
                {
                    return map.set( parameter, value );
                }
            } );
            return this;
        }
    }
}
