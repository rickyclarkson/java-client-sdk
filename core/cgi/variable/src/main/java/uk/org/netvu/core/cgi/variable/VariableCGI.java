package uk.org.netvu.core.cgi.variable;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;

/**
 * Builds and parses variable.cgi URLs.
 */
public final class VariableCGI
{
    private final GenericBuilder builder;

    VariableCGI( final GenericBuilder builder )
    {
        if ( builder.get( variableParam ).isNone() )
        {
            throw new IllegalStateException( variableParam.getName()
                    + " has not been set to a value" );
        }

        this.builder = builder;
    }

    private static final Parameter<Variable, Option<Variable>> variableParam = Parameter.param(
            "variable", "Name of the variable", Variable.fromString );

    private static final Parameter<VariableType, VariableType> typeParam = Parameter.param(
            "type", "Specifies return type of variable", VariableType.HTTP,
            VariableType.fromString );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( variableParam );
            add( typeParam );
        }
    };

    /**
     * A Builder to create VariableCGIs.
     */
    public static final class Builder
    {
        GenericBuilder real = new GenericBuilder();

        /**
         * Sets the variable parameter to the specified Variable.
         * 
         * @param variable
         *        the Variable to store.
         * @return the Builder.
         */
        public Builder variable( final Variable variable )
        {
            real = real.with( variableParam, variable );
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
            real = real.with( typeParam, type );
            return this;
        }

        /**
         * Builds a VariableCGI with the stored values.
         * 
         * @return a VariableCGI containing the stored values.
         */
        public VariableCGI build()
        {
            return new VariableCGI( real );
        }
    }

    /**
     * Gets the stored Variable.
     * 
     * @return the stored Variable.
     */
    public Variable getVariable()
    {
        return builder.get( variableParam ).get();
    }

    /**
     * Gets the stored VariableType.
     * 
     * @return the stored VariableType.
     */
    public VariableType getType()
    {
        return builder.get( typeParam );
    }

    @Override
    public String toString()
    {
        return "/variable.cgi?" + builder.toURLParameters( params );
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
        return new VariableCGI( GenericBuilder.fromURL( url, params ) );
    }
}
