package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;

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
    public static VariableCGI fromURL( final String url )
    {
        final Option<ParameterMap> map = ParameterMap.fromURL( url, VariableCGI.params );

        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( "Cannot parse " + url + " into a VariableCGI because " + map.reason() );
        }

        try
        {
            return new VariableCGI( map.get() );
        }
        catch ( final IllegalStateException e )
        {
            throw new IllegalArgumentException( "Cannot parse " + url + " into a VariableCGI because "
                    + e.getMessage() );
        }
    }

    private final ParameterMap parameterMap;

    private static final ParameterDescription<Variable, Option<Variable>> VARIABLE =
            ParameterDescription.parameterWithoutDefault( "variable", StringConversion.convenientTotal( Variable
                .fromStringFunction() ) );

    private static final ParameterDescription<VariableType, VariableType> TYPE =
            ParameterDescription.parameterWithDefault( "type", VariableType.HTTP, StringConversion
                .convenientPartial( VariableType.fromStringFunction() ) );

    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            // this is an anonymous intialiser - it is creating a new ArrayList
            // and adding values to it inline.
            add( VariableCGI.VARIABLE );
            add( VariableCGI.TYPE );
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
        if ( parameterMap.get( VariableCGI.VARIABLE ).isEmpty() )
        {
            throw new IllegalStateException( VariableCGI.VARIABLE.name + " has not been set to a value" );
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
        return parameterMap.get( VariableCGI.TYPE );
    }

    /**
     * Gets the stored Variable.
     * 
     * @return the stored Variable.
     */
    public Variable getVariable()
    {
        return parameterMap.get( VariableCGI.VARIABLE ).get();
    }

    /**
     * Gives /variable.cgi? followed by the values stored in this VariableCGI,
     * as URL parameters.
     */
    @Override
    public String toString()
    {
        return "/variable.cgi?" + parameterMap.toURLParameters( VariableCGI.params );
    }

    /**
     * A Builder to create VariableCGIs.
     */
    public static final class Builder
    {
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

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
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
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
            return set( VariableCGI.TYPE, type );
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
            return set( VariableCGI.VARIABLE, variable );
        }

        /**
         * Sets the value of a parameter to a given value, and returns the
         * Builder.
         * 
         * @param <T>
         *        the input type of the specified parameter.
         * @param parameter
         *        the parameter to set a value for.
         * @param value
         *        the value to give that parameter.
         * @return the Builder.
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }

            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }
    }
}
