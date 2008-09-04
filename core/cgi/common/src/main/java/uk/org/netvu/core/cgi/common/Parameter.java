package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Parameter<T, R>
{
    private final String name;
    private final String description;
    private final R defaultValue;

    private Parameter( final String name, final String description,
            final R defaultValue )
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public static <T> Parameter<T, Option<T>> param( final String name,
            final String description, final Conversion<String, T> fromString,
            final Conversion<T, String> toString )
    {
        return new Parameter<T, Option<T>>( name, description,
                new Option.None<T>() )
        {
            @Override
            public Option<T> reduce( final T newValue, final Option<T> original )
            {
                if ( original.isNone() )
                {
                    return new Option.Some<T>( newValue );
                }

                throw new IllegalStateException();
            }

            @Override
            public T fromURLParameter( final URLParameter nameAndValue )
            {
                return createFromURL( fromString, nameAndValue );
            }

            @Override
            public String toURLParameter(
                    final Pair<String, Option<T>> nameAndValue )
            {
                return nameAndValue.first() + '='
                        + toString.convert( nameAndValue.second().get() );
            }
        };

    }

    public static <T> Parameter<T, Option<T>> param( final String name,
            final String description, final Conversion<String, T> fromString )
    {
        return param( name, description, fromString,
                Conversion.<T> objectToString() );
    }

    public static <T> Parameter<T, T> param( final String name,
            final String description, final T defaultValue,
            final Conversion<String, T> fromString )
    {
        return new Parameter<T, T>( name, description, defaultValue )
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

            @Override
            public T fromURLParameter( final URLParameter nameAndValue )
            {
                return createFromURL( fromString, nameAndValue );
            }

            @Override
            public String toURLParameter( final Pair<String, T> nameAndValue )
            {
                return nameAndValue.first() + '='
                        + URLBuilder.encode( nameAndValue.second().toString() );
            }
        };
    }

    public static <T> Parameter<T, T> param( final String name,
            final String description, final T defaultValue,
            final Conversion<String, T> fromString,
            final Conversion<T, String> toString )
    {
        return new Parameter<T, T>( name, description, defaultValue )
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

            @Override
            public T fromURLParameter( final URLParameter nameAndValue )
            {
                return createFromURL( fromString, nameAndValue );
            }

            @Override
            public String toURLParameter( final Pair<String, T> nameAndValue )
            {
                return nameAndValue.first()
                        + '='
                        + URLBuilder.encode( toString.convert( nameAndValue.second() ) );
            }
        };
    }

    public static <U> Parameter<Integer, U> bound( final int lowerInclusive,
            final int higherInclusive, final Parameter<Integer, U> param )
    {
        return new Parameter<Integer, U>( param.name, param.description,
                param.defaultValue )
        {
            @Override
            public U reduce( final Integer newValue, final U original )
            {
                if ( newValue >= lowerInclusive && newValue <= higherInclusive )
                {
                    return param.reduce( newValue, original );
                }

                throw new IllegalArgumentException();
            }

            @Override
            public Integer fromURLParameter( final URLParameter nameAndValue )
            {
                return param.fromURLParameter( nameAndValue );
            }

            @Override
            public String toURLParameter( final Pair<String, U> nameAndValue )
            {
                return param.toURLParameter( nameAndValue );
            }

        };
    }

    public static <T, U> Parameter<T, U> not( final T banned,
            final Parameter<T, U> param )
    {
        return new Parameter<T, U>( param.name, param.description,
                param.defaultValue )
        {
            @Override
            public U reduce( final T newValue, final U original )
            {
                if ( newValue.equals( banned ) )
                {
                    throw new IllegalArgumentException();
                }
                return param.reduce( newValue, original );
            }

            @Override
            public T fromURLParameter( final URLParameter nameAndValue )
            {
                return param.fromURLParameter( nameAndValue );
            }

            @Override
            public String toURLParameter( final Pair<String, U> nameAndValue )
            {
                return param.toURLParameter( nameAndValue );
            }
        };
    }

    public static <T> Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>> sparseArrayParam(
            final String name, final String description,
            final Conversion<String, T> fromString,
            final Conversion<T, String> valueEncoder )
    {
        return new Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>>(
                name, description, new TreeMap<Integer, T>() )
        {
            @Override
            public TreeMap<Integer, T> reduce(
                    final List<Pair<Integer, T>> newValue,
                    final TreeMap<Integer, T> original )
            {
                final TreeMap<Integer, T> copy = new TreeMap<Integer, T>(
                        original );
                for ( final Pair<Integer, T> pair : newValue )
                {
                    copy.put( pair.first(), pair.second() );
                }

                return copy;
            }

            @Override
            public List<Pair<Integer, T>> fromURLParameter(
                    final URLParameter keyAndValue )
            {
                final List<String> values = Strings.splitIgnoringQuotedSections(
                        keyAndValue.value, ',' );
                int startIndex = Integer.parseInt( keyAndValue.name.substring(
                        name.length() + 1, keyAndValue.name.length() - 1 ) );

                final List<Pair<Integer, T>> results = new ArrayList<Pair<Integer, T>>();

                for ( final String value : values )
                {
                    results.add( Pair.pair( startIndex,
                            fromString.convert( value.substring( 1,
                                    value.length() - 1 ) ) ) );

                    startIndex++;
                }

                return results;
            }

            @Override
            public String toURLParameter(
                    final Pair<String, TreeMap<Integer, T>> nameAndMap )
            {
                final StringBuilder result = new StringBuilder();

                for ( final Map.Entry<Integer, T> entry : nameAndMap.second().entrySet() )
                {
                    if ( result.length() != 0 )
                    {
                        result.append( "&" );
                    }

                    result.append( URLBuilder.param(
                            name + '[' + entry.getKey() + ']',
                            URLBuilder.encode( valueEncoder.convert( entry.getValue() ) ) ) );
                }

                return result.toString();
            }
        };
    }

    public static <R> Parameter<Integer, R> notNegative(
            final Parameter<Integer, R> param )
    {
        return bound( 0, Integer.MAX_VALUE, param );
    }

    public String withURLParameter( final GenericBuilder genericBuilder )
    {
        return genericBuilder.isDefault( this ) ? ""
                : toURLParameter( Pair.pair( name, genericBuilder.get( this ) ) );
    }

    private static <T> T createFromURL( final Conversion<String, T> conversion,
            final URLParameter pair )
    {
        return conversion.convert( pair.value );
    }

    public String getName()
    {
        return name;
    }

    public abstract T fromURLParameter( final URLParameter nameAndValue );

    public R getDefaultValue()
    {
        return defaultValue;
    }

    public abstract R reduce( final T newValue, final R original );

    public abstract String toURLParameter( final Pair<String, R> nameAndValue );

    public T fromString( final String value )
    {
        return fromURLParameter( new URLParameter( name, value ) );
    }
}
