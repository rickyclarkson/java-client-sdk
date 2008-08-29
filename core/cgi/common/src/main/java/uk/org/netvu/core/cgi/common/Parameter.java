package uk.org.netvu.core.cgi.common;

import java.util.Map;
import java.util.TreeMap;

public class Parameter<T, R>
{
    public final String name;
    public final String description;
    public final R defaultValue;
    public final Reduction<T, R> reduction;
    public final Conversion<String, T> fromString;
    public final Reduction<R, URLBuilder> toURLParameter;

    private Parameter( final String name, final String description,
            final R defaultValue, final Reduction<T, R> reduction,
            final Conversion<String, T> fromString,
            final Reduction<R, URLBuilder> toURLParameter )
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.reduction = reduction;
        this.fromString = fromString;
        this.toURLParameter = toURLParameter;
    }

    public static <T> Parameter<T, Option<T>> param( final String name,
            final String description, final Conversion<String, T> fromString )
    {
        return new Parameter<T, Option<T>>( name, description,
                new Option.None<T>(), new Reduction<T, Option<T>>()
                {
                    @Override
                    public Option<T> reduce( final T value,
                            final Option<T> original )
                    {
                        if ( original.isNone() )
                        {
                            return new Option.Some<T>( value );
                        }
                        throw new IllegalStateException();
                    }
                }, fromString, new Reduction<Option<T>, URLBuilder>()
                {
                    @Override
                    public URLBuilder reduce( final Option<T> newValue,
                            final URLBuilder original )
                    {
                        return original.withParam( name,
                                newValue.get().toString() );
                    }
                } );
    }

    public static <T> Parameter<T, T> param( final String name,
            final String description, final T defaultValue,
            final Conversion<String, T> fromString )
    {
        return new Parameter<T, T>( name, description, defaultValue,
                new Reduction<T, T>()
                {
                    @Override
                    public T reduce( final T newValue, final T original )
                    {
                        if ( original.equals( defaultValue ) )
                        {
                            return newValue;
                        }

                        throw new IllegalStateException();
                    }
                }, fromString, Parameter.<T> singleToURLParameter( name ) );
    }

    public static <U> Parameter<Integer, U> bound( final int lowerInclusive,
            final int higherInclusive, final Parameter<Integer, U> param )
    {
        return new Parameter<Integer, U>( param.name, param.description,
                param.defaultValue, new Reduction<Integer, U>()
                {
                    @Override
                    public U reduce( final Integer newValue, final U original )
                    {
                        if ( newValue >= lowerInclusive
                                && newValue <= higherInclusive )
                        {
                            return param.reduction.reduce( newValue, original );
                        }

                        throw new IllegalArgumentException();
                    }
                }, param.fromString, param.toURLParameter );
    }

    public static <T, U> Parameter<T, U> not( final T banned,
            final Parameter<T, U> param )
    {
        return new Parameter<T, U>( param.name, param.description,
                param.defaultValue, new Reduction<T, U>()
                {
                    @Override
                    public U reduce( final T newValue, final U original )
                    {
                        if ( newValue.equals( banned ) )
                        {
                            throw new IllegalArgumentException();
                        }
                        return param.reduction.reduce( newValue, original );
                    }
                }, param.fromString, param.toURLParameter );
    }

    public static <T> Parameter<Pair<Integer, T>, TreeMap<Integer, T>> sparseArrayParam(
            final String name, final String description )
    {
        return new Parameter<Pair<Integer, T>, TreeMap<Integer, T>>(
                name,
                description,
                new TreeMap<Integer, T>(),
                new Reduction<Pair<Integer, T>, TreeMap<Integer, T>>()
                {
                    @Override
                    public TreeMap<Integer, T> reduce(
                            final Pair<Integer, T> newValue,
                            final TreeMap<Integer, T> original )
                    {
                        final TreeMap<Integer, T> copy = new TreeMap<Integer, T>(
                                original );
                        copy.put( newValue.first(), newValue.second() );
                        return copy;
                    }
                },
                Conversion.<String, Pair<Integer, T>> throwUnsupportedOperationException(),
                new Reduction<TreeMap<Integer, T>, URLBuilder>()
                {
                    @Override
                    public URLBuilder reduce( final TreeMap<Integer, T> map,
                            URLBuilder urlBuilder )
                    {
                        for ( final Map.Entry<Integer, T> entry : map.entrySet() )
                        {
                            urlBuilder = urlBuilder.withParam( name + '['
                                    + entry.getKey() + ']',
                                    entry.getValue().toString() );
                        }

                        return urlBuilder;
                    }
                } );
    }

    private static <T> Reduction<T, URLBuilder> singleToURLParameter(
            final String name )
    {
        return new Reduction<T, URLBuilder>()
        {
            @Override
            public URLBuilder reduce( final T t, final URLBuilder builder )
            {
                return builder.withParam( name, t.toString() );
            }
        };
    }

    public URLBuilder withURLParameter( final URLBuilder builder,
            final GenericBuilder genericBuilder )
    {
        return genericBuilder.isDefault( this ) ? builder
                : toURLParameter.reduce( genericBuilder.get( this ), builder );
    }
}
