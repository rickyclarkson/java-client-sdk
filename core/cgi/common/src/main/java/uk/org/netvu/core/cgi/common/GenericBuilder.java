package uk.org.netvu.core.cgi.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenericBuilder
{
    public final Validator validator;
    public final Map<Parameter<?, ?>, Object> values;

    public GenericBuilder()
    {
        this( new Validator()
        {
            public boolean isValid( final GenericBuilder builder )
            {
                return true;
            }
        } );
    }

    public GenericBuilder( final Validator validator )
    {
        this( new HashMap<Parameter<?, ?>, Object>(), validator );
    }

    GenericBuilder( final Map<Parameter<?, ?>, Object> values,
            final Validator validator )
    {
        this.validator = validator;
        this.values = Collections.unmodifiableMap( values );
    }

    public <T, R> GenericBuilder with( final Parameter<T, R> parameter,
            final T value )
    {
        final Map<Parameter<?, ?>, Object> copy = new HashMap<Parameter<?, ?>, Object>(
                values );

        copy.put( parameter,
                parameter.reduction.reduce( value, get( parameter ) ) );

        final GenericBuilder built = new GenericBuilder( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for " + parameter.name
                    + " violates the constraints on this builder" );
        }

        return built;
    }

    public boolean isDefault( final Parameter<?, ?> parameter )
    {
        return values.get( parameter ) == null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get( final Parameter<?, T> parameter )
    {
        return values.containsKey( parameter ) ? (T) values.get( parameter )
                : parameter.defaultValue;
    }

    private <T, R> GenericBuilder withFromString( final Parameter<T, R> param,
            final String string )
    {
        return with( param, param.fromString.convert( string ) );
    }

    public static GenericBuilder fromURL( final String url,
            final List<Parameter<?, ?>> params )
    {
        GenericBuilder builder = new GenericBuilder();

        final String[] parts = Strings.fromLast( '?', url ).split( "&" );
        for ( final String part : parts )
        {
            final String[] both = part.split( "=" );
            for ( final Parameter<?, ?> param : params )
            {
                if ( param.name.equals( both[0] ) )
                {
                    builder = builder.withFromString( param, both[1] );
                }
            }
        }

        return builder;
    }

    public String toURLParameters( final List<Parameter<?, ?>> params )
    {
        URLBuilder builder = new URLBuilder( "" );
        for ( final Parameter<?, ?> param : params )
        {
            builder = param.withURLParameter( builder, this );
        }

        return builder.toString();
    }
}
