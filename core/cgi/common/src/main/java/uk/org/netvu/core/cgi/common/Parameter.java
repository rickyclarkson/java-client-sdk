package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameter<T, R>
{
    public final String name;
    public final String description;
    public final R defaultValue;
    public final Reduction<T, R> reduction;
    public final Conversion<String, T> fromString;

    private Parameter( final String name, final String description,
            final R defaultValue, final Reduction<T, R> reduction,
            final Conversion<String, T> fromString )
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.reduction = reduction;
        this.fromString = fromString;
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
                }, fromString );
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
                }, fromString );
    }

    @Override
    public String toString()
    {
        return "Parameter(name=" + name + ", description=" + description
                + ", defaultValue=" + defaultValue + ')';
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
                }, param.fromString );
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
                }, param.fromString );
    }

    public static <T> Parameter<T, List<T>> many( final String name,
            final String description )
    {
        return new Parameter<T, List<T>>( name, description,
                new ArrayList<T>(), new Reduction<T, List<T>>()
                {
                    @Override
                    public List<T> reduce( final T newValue,
                            final List<T> original )
                    {
                        final List<T> result = new ArrayList<T>( original );
                        result.add( newValue );
                        return result;
                    }
                }, Conversion.<String, T> throwUnsupportedOperationException() );
    }

    public static <T> Parameter<Pair<Integer, T>, Map<Integer, T>> sparseArrayParam(
            final String name, final String description )
    {
        return new Parameter<Pair<Integer, T>, Map<Integer, T>>(
                name,
                description,
                new HashMap<Integer, T>(),
                new Reduction<Pair<Integer, T>, Map<Integer, T>>()
                {
                    @Override
                    public Map<Integer, T> reduce(
                            final Pair<Integer, T> newValue,
                            final Map<Integer, T> original )
                    {
                        final Map<Integer, T> copy = new HashMap<Integer, T>(
                                original );
                        copy.put( newValue.first(), newValue.second() );
                        return copy;
                    }
                },
                Conversion.<String, Pair<Integer, T>> throwUnsupportedOperationException() );
    }
}
