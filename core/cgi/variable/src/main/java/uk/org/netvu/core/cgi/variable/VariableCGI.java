package uk.org.netvu.core.cgi.variable;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;

public class VariableCGI
{
    private final GenericBuilder builder;

    public VariableCGI( final GenericBuilder builder )
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

    public static final class Builder
    {
        GenericBuilder real = new GenericBuilder();

        public Builder variable( final Variable variable )
        {
            real = real.with( variableParam, variable );
            return this;
        }

        public Builder type( final VariableType type )
        {
            real = real.with( typeParam, type );
            return this;
        }

        public VariableCGI build()
        {
            return new VariableCGI( real );
        }
    }

    public Variable getVariable()
    {
        return builder.get( variableParam ).get();
    }

    public VariableType getType()
    {
        return builder.get( typeParam );
    }

    @Override
    public String toString()
    {
        return "/variable.cgi?" + builder.toURLParameters( params );
    }

    public static VariableCGI fromString( final String url )
    {
        return new VariableCGI( GenericBuilder.fromURL( url, params ) );
    }
}
