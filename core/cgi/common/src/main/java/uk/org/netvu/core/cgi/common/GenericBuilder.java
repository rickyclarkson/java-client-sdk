package uk.org.netvu.core.cgi.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class GenericBuilder
{
    public final Validator validator;
    public final Map<Parameter<?>, Object> values;

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
        this( new HashMap<Parameter<?>, Object>(), validator );
    }

    GenericBuilder( final Map<Parameter<?>, Object> values,
            final Validator validator )
    {
        this.validator = validator;
        this.values = Collections.unmodifiableMap( values );
    }

    public <T> GenericBuilder with( final Parameter<T> parameter, final T value )
    {
        if ( !parameter.constraint.isValid( this, parameter, value ) )
        {
            throw new IllegalArgumentException();
        }

        final Map<Parameter<?>, Object> copy = new HashMap<Parameter<?>, Object>(
                values );
        copy.put( parameter, value );

        final GenericBuilder built = new GenericBuilder( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalArgumentException( value + " for "
                    + parameter.name
                    + " violates the constraints on this builder" );
        }

        return built;
    }

    public boolean isSet( final Parameter<?> parameter )
    {
        return values.get( parameter ) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get( final Parameter<T> parameter )
    {
        return isSet( parameter ) ? (T) values.get( parameter )
                : parameter.defaultValue.get();
    }

    public <T> GenericBuilder withFromString( final Parameter<T> param,
            final String string )
    {
        return with( param, param.fromString.convert( string ) );
    }
}
