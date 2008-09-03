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
            @Override
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

        copy.put( parameter, parameter.reduce( value, get( parameter ) ) );

        final GenericBuilder built = new GenericBuilder( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for "
                    + parameter.getName()
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
                : parameter.getDefaultValue();
    }

    private <T, R> GenericBuilder withFromString( final Parameter<T, R> param,
            final URLParameter keyAndValue )
    {
        return with( param, param.fromURLParameter( keyAndValue ) );
    }

    public static GenericBuilder fromURL( final String url,
            final Iterable<? extends Parameter<?, ?>> params )
    {
        GenericBuilder builder = new GenericBuilder();
        final List<URLParameter> parts = URLExtractor.nameValuePairs( url );

        for ( final URLParameter part : parts )
        {
            for ( final Parameter<?, ?> param : params )
            {
                if ( part.name.startsWith( param.getName() ) )
                {
                    builder = builder.withFromString( param, part );
                }
            }
        }

        return builder;
    }

    public String toURLParameters( final List<? extends Parameter<?, ?>> params )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final Parameter<?, ?> param : params )
        {
            builder.append( param.withURLParameter( this ) ).append( "&" );
        }

        return builder.toString().replaceAll( "&$", "" ).replaceAll( "[&]+",
                "&" );

    }
}
