package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Parameter<T, R>
{
    public final String name;
    public final String description;
    public final R defaultValue;
    public final Reduction<T, R> reduction;
    public final Conversion<Pair<String, String>, T> fromURLParameter;
    public final Conversion<Pair<String, R>, String> toURLParameter;

    private Parameter( final String name, final String description,
            final R defaultValue, final Reduction<T, R> reduction,
            final Conversion<Pair<String, String>, T> fromURLParameter,
            final Conversion<Pair<String, R>, String> toURLParameter )
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.reduction = reduction;
        this.fromURLParameter = fromURLParameter;
        this.toURLParameter = toURLParameter;
    }

    public static <T> Parameter<T, Option<T>> param( final String name,
            final String description, final Conversion<String, T> fromString,
            final Conversion<T, String> toString )
    {
        return new Parameter<T, Option<T>>( name, description,
                new Option.None<T>(), new Reduction<T, Option<T>>()
                {
                    @Override
                    public Option<T> reduce( final T newValue,
                            final Option<T> original )
                    {
                        if ( original.isNone() )
                        {
                            return new Option.Some<T>( newValue );
                        }

                        throw new IllegalStateException();
                    }
                }, createFromURL( fromString ),
                new Conversion<Pair<String, Option<T>>, String>()
                {
                    @Override
                    public String convert(
                            final Pair<String, Option<T>> nameAndValue )
                    {
                        return nameAndValue.first()
                                + '='
                                + toString.convert( nameAndValue.second().get() );
                    }
                } );
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
                }, createFromURL( fromString ),
                new Conversion<Pair<String, T>, String>()
                {
                    @Override
                    public String convert( final Pair<String, T> nameAndValue )
                    {
                        return nameAndValue.first() + '='
                                + nameAndValue.second();
                    }
                } );
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
                }, param.fromURLParameter, param.toURLParameter );
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
                }, param.fromURLParameter, param.toURLParameter );
    }

    public static <T> Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>> sparseArrayParam(
            final String name, final String description,
            final Conversion<String, T> fromString,
            final Conversion<T, String> valueEncoder )
    {
        return new Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>>(
                name,
                description,
                new TreeMap<Integer, T>(),
                new Reduction<List<Pair<Integer, T>>, TreeMap<Integer, T>>()
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
                },
                new Conversion<Pair<String, String>, List<Pair<Integer, T>>>()
                {
                    @Override
                    public List<Pair<Integer, T>> convert(
                            final Pair<String, String> keyAndValue )
                    {
                        final List<String> values = Strings.splitIgnoringQuotedSections(
                                keyAndValue.second(), ',' );
                        int startIndex = Integer.parseInt( keyAndValue.first().substring(
                                name.length() + 1,
                                keyAndValue.first().length() - 1 ) );

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
                }, new Conversion<Pair<String, TreeMap<Integer, T>>, String>()
                {
                    @Override
                    public String convert(
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
                } );
    }

    public String withURLParameter( final GenericBuilder genericBuilder )
    {
        return genericBuilder.isDefault( this ) ? ""
                : toURLParameter.convert( Pair.pair( name,
                        genericBuilder.get( this ) ) );
    }

    private static <T> Conversion<Pair<String, String>, T> createFromURL(
            final Conversion<String, T> conversion )
    {
        return new Conversion<Pair<String, String>, T>()
        {
            @Override
            public T convert( final Pair<String, String> pair )
            {
                return conversion.convert( pair.second() );
            }
        };
    }
}
