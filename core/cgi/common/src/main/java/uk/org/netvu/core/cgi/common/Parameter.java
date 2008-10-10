package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An object that specifies a parameter for a Builder, including conversions from Strings to the input type
 * of the Parameter and back, and validation.  For internal use only.
 * 
 * @param <T>
 *        the input type of the Parameter.
 * @param <R>
 *        the output type of the Parameter.
 */
public abstract class Parameter<T, R>
{
    /**
     * Constructs a Parameter that can take Integers between two inclusive
     * bounds, and yields a U, via a specified Parameter.
     * 
     * @param <U>
     *        the output type of the Parameter.
     * @param lowerInclusive
     *        the lower bound of the values that the Parameter can take,
     *        inclusive.
     * @param higherInclusive
     *        the higher bound of the values that the Parameter can take,
     *        inclusive.
     * @param param
     *        the parameter to pass values on to.
     * @return a Parameter that can take Integers between two inclusive bounds,
     *         and yields a U, via a specified Parameter.
     */
    public static <U> Parameter<Integer, U> bound( final int lowerInclusive,
            final int higherInclusive, final Parameter<Integer, U> param )
    {
        return new Parameter<Integer, U>( param.name, param.defaultValue )
        {
            @Override
            public Option<Integer> fromURLParameter(
                    final URLParameter nameAndValue )
            {
                return param.fromURLParameter( nameAndValue );
            }

            @Override
            public U reduce( final Integer newValue, final U original )
            {
                if ( newValue >= lowerInclusive && newValue <= higherInclusive )
                {
                    return param.reduce( newValue, original );
                }

                throw new IllegalArgumentException( "The value " + newValue
                        + " is not within the bounds for the " + param.name
                        + " parameter (" + lowerInclusive + " to "
                        + higherInclusive + " inclusive)." );
            }

            @Override
            public Option<String> toURLParameter(
                    final Pair<String, U> nameAndValue )
            {
                return param.toURLParameter( nameAndValue );
            }

        };
    }

    /**
     * Constructs a Parameter with one disallowed value, passing all allowed
     * values on to the specified Parameter.
     * 
     * @param <T>
     *        the input type of the Parameter.
     * @param <U>
     *        the output type of the Parameter.
     * @param banned
     *        the value that is disallowed.
     * @param param
     *        the Parameter to pass values on to.
     * @return a Parameter with one disallowed value.
     */
    public static <T, U> Parameter<T, U> not( final T banned,
            final Parameter<T, U> param )
    {
        Checks.notNull( banned );

        return new Parameter<T, U>( param.name, param.defaultValue )
        {
            @Override
            public Option<T> fromURLParameter( final URLParameter nameAndValue )
            {
                return param.fromURLParameter( nameAndValue );
            }

            @Override
            public U reduce( final T newValue, final U original )
            {
                if ( newValue.equals( banned ) )
                {
                    throw new IllegalArgumentException(
                            "The "
                                    + param.name
                                    + " parameter is not allowed to take the supplied value, "
                                    + newValue + '.' );
                }
                return param.reduce( newValue, original );
            }

            @Override
            public Option<String> toURLParameter(
                    final Pair<String, U> nameAndValue )
            {
                return param.toURLParameter( nameAndValue );
            }
        };
    }

    /**
     * Constructs a Parameter that accepts non-negative Integers and yields
     * values of type R by passing them on to the specified Parameter.
     * 
     * @param <R>
     *        the output type of this Parameter.
     * @param param
     *        the Parameter to pass values to.
     * @return a Parameter that accepts non-negative Integers and yields values
     *         of type R by passing them on to the specified Parameter.
     */
    public static <R> Parameter<Integer, R> notNegative(
            final Parameter<Integer, R> param )
    {
        return bound( 0, Integer.MAX_VALUE, param );
    }

    /**
     * Constructs a Parameter that can take 0 or 1 values of type T, yielding
     * them as an Option<T>.
     * 
     * @param <T>
     *        the type that the Parameter can take.
     * @param name
     *        the name of the Parameter.
     * @param conversions
     *        an object that can convert a String to a T and back.
     * @return a Parameter that can take 0 or 1 values of type T, yielding them
     *         as an Option<T>.
     */
    public static <T> Parameter<T, Option<T>> parameter( final String name,
            final StringConversion<T> conversions )
    {
        Checks.notNull( name, conversions );

        return new Parameter<T, Option<T>>( name,
                Option.<T> none( "The value for the " + name
                        + " parameter has not been set yet" ) )
        {
            @Override
            public Option<T> fromURLParameter( final URLParameter nameAndValue )
            {
                return conversions.fromString(nameAndValue.value);
            }

            @Override
            public Option<T> reduce( final T newValue, final Option<T> original )
            {
                if ( original.isEmpty() )
                {
                    return Option.some( newValue );
                }

                throw new IllegalStateException( "The " + name
                        + " parameter has already been set to a value." );
            }

            @Override
            public Option<String> toURLParameter(
                    final Pair<String, Option<T>> nameAndValue )
            {
                return nameAndValue.second().bind(
                        new Conversion<T, Option<String>>()
                        {
                            @Override
                            public Option<String> convert( final T value )
                            {
                                return conversions.toString( value ).map(
                                        new Conversion<String, String>()
                                        {
                                            @Override
                                            public String convert(
                                                    final String valuePart )
                                            {
                                                return nameAndValue.first()
                                                        + '=' + valuePart;
                                            }

                                        } );
                            }
                        } );
            }
        };

    }

    /**
     * Constructs a Parameter that can take 0 or 1 values of type T, yielding
     * that value or a default value.
     * 
     * @param <T>
     *        the type that the Parameter can take.
     * @param name
     *        the name of the Parameter.
     * @param defaultValue
     *        the default value for the Parameter.
     * @param conversions
     *        an object that can convert a String to a T and back.
     * @return a Parameter that can take 0 or 1 values of type T, yield that
     *         value or a default value.
     */
    public static <T> Parameter<T, T> parameterWithDefault( final String name,
            final T defaultValue, final StringConversion<T> conversions )
    {
        Checks.notNull( name, defaultValue, conversions );

        return new Parameter<T, T>( name, defaultValue )
        {
            @Override
            public Option<T> fromURLParameter( final URLParameter nameAndValue )
            {
                return conversions.fromString(nameAndValue.value);
            }

            @Override
            public T reduce( final T newValue, final T original )
            {
                if ( original.equals( defaultValue ) )
                {
                    return newValue;
                }

                throw new IllegalStateException(
                        "The "
                                + name
                                + " parameter has already been set to a value other than its default" );
            }

            @Override
            public Option<String> toURLParameter(
                    final Pair<String, T> nameAndValue )
            {
                return conversions.toString( nameAndValue.second() ).map(
                        new Conversion<String, String>()
                        {
                            @Override
                            public String convert( final String t )
                            {
                                return nameAndValue.first() + '='
                                        + URLBuilder.encode( t );
                            }
                        } );
            }
        };
    }

    /**
     * Constructs a Parameter that accepts Pairs of Integers and values of type
     * T, and yields a TreeMap of Integers to values of type T accordingly,
     * representing a sparse array.
     * 
     * @param <T>
     *        the input type of the sparse array.
     * @param name
     *        the name of the Parameter.
     * @param conversions
     *        an object that can convert a String to a T and back.
     * @return a Parameter that accepts Pairs of Integers and values of type T,
     *         and yields a TreeMap of Integers to values of type T accordingly,
     *         representing a sparse array.
     */
    public static <T> Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>> sparseArrayParameter(
            final String name, final StringConversion<T> conversions )
    {
        Checks.notNull( name, conversions );

        return new Parameter<List<Pair<Integer, T>>, TreeMap<Integer, T>>(
                name, new TreeMap<Integer, T>() )
        {
            @Override
            public Option<List<Pair<Integer, T>>> fromURLParameter(
                    final URLParameter keyAndValue )
            {
                final List<String> values = Strings.splitIgnoringQuotedSections(
                        keyAndValue.value, ',' );
                int startIndex = Integer.parseInt( keyAndValue.name.substring(
                        name.length() + 1, keyAndValue.name.length() - 1 ) );

                final List<Pair<Integer, T>> results = new ArrayList<Pair<Integer, T>>();

                for ( final String value : values )
                {
                    final int hack = startIndex;
                    for (final T t: conversions.fromString(
                                                              Strings.removeSurroundingQuotesLeniently( value ) ))
                        {
                            results.add( Pair.pair( hack, t ) );
                        }

                    startIndex++;
                }

                return Option.some( results );
            }

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
            public Option<String> toURLParameter(
                    final Pair<String, TreeMap<Integer, T>> nameAndMap )
            {
                final StringBuilder result = new StringBuilder();

                for ( final Map.Entry<Integer, T> entry : nameAndMap.second().entrySet() )
                {
                    for (final String value: conversions.toString( entry.getValue() ))
                    {
                        if ( result.length() != 0 )
                        {
                            result.append( "&" );
                        }

                        result.append( name + '[' + entry.getKey()
                                       + ']' + "="
                                       + URLBuilder.encode( value ) );
                    }
                }

                return Option.some( result.toString() );
            }
        };
    }

    private static <T> Option<T> createFromURL(
            final Conversion<String, Option<T>> conversion,
            final URLParameter pair )
    {
        return conversion.convert( pair.value );
    }

    /**
     * The name of the Parameter, as it appears in URL parameters and in
     * diagnostic messages.
     */
    public final String name;

    private final R defaultValue;

    private Parameter( final String name, final R defaultValue )
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Parses the specified URLParameter into a value of type T.
     * 
     * @param nameAndValue
     *        the URLParameter to parse.
     * @return a value of type T, after parsing.
     */
    public abstract Option<T> fromURLParameter( final URLParameter nameAndValue );

    /**
     * Converts this Parameter plus the value stored for it in the specified
     * ParameterMap into a URL-style parameter, if the value is not the default
     * value.
     * 
     * @param parameterMap
     *        the ParameterMap to get the value from.
     * @return this Parameter as a URL parameter, or an empty String if the
     *         value of the parameter is the Parameter's default value.
     */
    public Option<String> toURLParameter( final ParameterMap parameterMap )
    {
        return parameterMap.isDefault( this ) ? Option.some( "" )
                : toURLParameter( Pair.pair( name, parameterMap.get( this ) ) );
    }

    /**
     * Gives the default value for this Parameter.
     * 
     * @return the default value for this Parameter.
     */
    R getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Takes in a new value and an original value and produces a new value from
     * merging them. What this actually does depends on the particular Parameter
     * implementation.
     * 
     * @param newValue
     * @param original
     * @return
     */
    abstract R reduce( final T newValue, final R original );

    /**
     * Converts a name and value into a URLParameter, placing it in a Some if
     * that succeeds, or in a None if that is an unsupported operation for this
     * Parameter.
     * 
     * @param nameAndValue
     * @return
     */
    abstract Option<String> toURLParameter( final Pair<String, R> nameAndValue );
}
