package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.org.netvu.util.CheckParameters;

/**
 * An object that describes a parameter for a Builder, including conversions
 * from Strings to the input type of the parameter and back, and validation.
 * 
 * @param <T>
 *        the input type of the parameter that the ParameterDescription
 *        describes.
 * @param <R>
 *        the output type of the parameter that the ParameterDescription
 *        describes.
 */
public abstract class ParameterDescription<T, R>
{
    /**
     * Constructs a ParameterDescription that describes a parameter that accepts
     * non-negative Integers and yields values of type R by passing them on to
     * the specified ParameterDescription.
     * 
     * @param <R>
     *        the output type of the parameter.
     * @param param
     *        the ParameterDescription to pass values on to.
     * @return a ParameterDescription that describes a parameter that accepts
     *         non-negative Integers and yields values of type R by passing them
     *         on to the specified ParameterDescription.
     */
    public static <R> ParameterDescription<Integer, R> nonNegativeParameter(
            final ParameterDescription<Integer, R> param )
    {
        return parameterWithBounds( 0, Integer.MAX_VALUE, param );
    }

    public static <T> ParameterDescriptionWithoutDefault<T> parameter( final String name,
            final StringConversion<T> conversion )
    {
        return new ParameterDescriptionWithoutDefault<T>( name, conversion );
    }

    /**
     * Constructs a ParameterDescription that disallows one value, passing all
     * allowed values on to the specified ParameterDescription.
     * 
     * @param <T>
     *        the input type of the parameter.
     * @param <U>
     *        the output type of the parameter.
     * @param banned
     *        the value that is disallowed.
     * @param delegate
     *        the ParameterDescription to pass values on to.
     * @return a ParameterDescription that disallows a value.
     */
    public static <T, U> ParameterDescription<T, U> parameterDisallowing( final T banned,
            final ParameterDescription<T, U> delegate )
    {
        CheckParameters.areNotNull( banned );

        return new BannedParameterDescription<T, U>( banned, delegate );
    }

    /**
     * Constructs a ParameterDescription describing parameters that can take
     * Integers between two inclusive bounds, and yields a U, delegating
     * everything except the validation to a specified ParameterDescription.
     * 
     * @param <U>
     *        the output type of the parameter.
     * @param lowerInclusive
     *        the lower bound of the values that the parameter can take,
     *        inclusive.
     * @param higherInclusive
     *        the higher bound of the values that the parameter can take,
     *        inclusive.
     * @param delegate
     *        the ParameterDescription to pass valid values on to.
     * @return a ParameterDescription that describes parameters that can take
     *         Integers between two inclusive bounds, and yields a U, delegating
     *         everything except the validation to a specified
     *         ParameterDescription.
     */
    public static <U> ParameterDescription<Integer, U> parameterWithBounds( final int lowerInclusive,
            final int higherInclusive, final ParameterDescription<Integer, U> delegate )
    {
        return new BoundParameterDescription<Integer, U>( lowerInclusive, higherInclusive, Num.integer, delegate );
    }

    public static <T, U> ParameterDescription<T, U> parameterWithBounds( final T lower, final T upper,
            final Num<T> num, final ParameterDescription<T, U> delegate )
    {
        return new BoundParameterDescription<T, U>( lower, upper, num, delegate );
    }

    public static ParameterDescription<Integer, Integer> parameterWithBoundsAndAnException( final int lowerInclusive,
            final int higherInclusive, final int exception, final ParameterDescription<Integer, Integer> delegate )
    {
        return new ParameterDescription<Integer, Integer>( delegate.name, delegate.defaultValue )
        {
            @Override
            public Option<Integer> fromURLParameter( final URLParameter nameAndValue )
            {
                return delegate.fromURLParameter( nameAndValue );
            }

            @Override
            public Option<String> toURLParameter( final Integer value )
            {
                return delegate.toURLParameter( value );
            }

            @Override
            Integer reduce( final ParameterMap map, final Integer newValue, final Integer original )
            {
              if (original.equals( defaultValue ))
              {
                if (newValue.intValue() >= lowerInclusive && newValue.intValue() <= higherInclusive || newValue == exception)
                  return newValue;
                throw new IllegalStateException(newValue+" is not between "+lowerInclusive + " and " + higherInclusive + ", so is not valid for the " + name + " parameter.");
              }
              else
                throw new IllegalStateException(name+" has already been set to a value.");
            }
        };
    }

    public static <T> ParameterDescription<T, T> parameterWithDefault( final String name,
            final Function<ParameterMap, T> defaultValue, final StringConversion<T> conversions )
    {
        return new ParameterDescriptionWithDefault<T>( name, defaultValue, conversions );
    }

    /**
     * Constructs a ParameterDescription that describes parameters that can take
     * 0 or 1 values of type T, yielding that value or a default value.
     * 
     * @param <T>
     *        the type of value that the parameter can take.
     * @param name
     *        the name of the parameter.
     * @param defaultValue
     *        the default value for the parameter.
     * @param conversions
     *        an object that can convert a String to a T and back.
     * @return a ParameterDescription that can take 0 or 1 values of type T,
     *         yield that value or a default value.
     */
    public static <T> ParameterDescriptionWithDefault<T> parameterWithDefault( final String name,
            final T defaultValue, final StringConversion<T> conversions )
    {
        CheckParameters.areNotNull( name, defaultValue, conversions );

        return new ParameterDescriptionWithDefault<T>( name, Function.<ParameterMap, T> constant( defaultValue ),
                conversions );
    }

    /**
     * Constructs a ParameterDescription that describes parameters that can take
     * 0 or 1 values of type T, yielding them as an Option<T>.
     * 
     * @param <T>
     *        the input type of the parameter.
     * @param name
     *        the name of the parameter.
     * @param conversion
     *        an object that can convert a String to a T and back.
     * @return a parameter that can take 0 or 1 values of type T, yielding them
     *         as an Option<T>.
     */
    public static <T> ParameterDescriptionWithoutDefault<T> parameterWithoutDefault( final String name,
            final StringConversion<T> conversion )
    {
        CheckParameters.areNotNull( name, conversion );

        return new ParameterDescriptionWithoutDefault<T>( name, conversion );

    }

    public static <R> ParameterDescription<Integer, R> positiveInteger( final ParameterDescription<Integer, R> param )
    {
        return parameterWithBounds( 1, Integer.MAX_VALUE, param );
    }

    /**
     * Constructs a ParameterDescription that describes parameters that accept
     * Pairs of Integers and values of type T, and yield a TreeMap of Integers
     * to values of type T accordingly, representing a sparse array.
     * 
     * @param <T>
     *        the input type of the sparse array.
     * @param name
     *        the name of the parameter.
     * @param conversions
     *        an object that can convert a String to a T and back.
     * @return a ParameterDescription that describes parameters that accept
     *         Pairs of Integers and values of type T, and yield a TreeMap of
     *         Integers to values of type T accordingly, representing a sparse
     *         array.
     */
    public static <T> SparseArrayParameterDescription<T> sparseArrayParameter( final String name,
            final StringConversion<T> conversions )
    {
        CheckParameters.areNotNull( name, conversions );

        return new SparseArrayParameterDescription<T>( name, conversions );
    }

    private static <T> ParameterDescription<T, T> oneOfWithDefault( final String name,
            final Function<ParameterMap, T> theDefault, final StringConversion<T> stringConversion,
            final T... validValues )
    {
        return new ParameterDescription<T, T>( name, theDefault )
        {
            @Override
            public Option<T> fromURLParameter( final URLParameter nameAndValue )
            {
                return stringConversion.fromString( nameAndValue.value );
            }

            @Override
            public Option<String> toURLParameter( final T value )
            {
                return stringConversion.toString( value );
            }

            @Override
            T reduce( final ParameterMap map, final T newValue, final T original )
            {
              if (original.equals(theDefault))
                {
                  if (Arrays.contains(validValues, newValue))
                    throw new IllegalStateException(newValue+" is not a valid value for the "+name+" parameter.");
                  return newValue;
                }
              throw new IllegalStateException(name+" already has a value set.");
            }
        };
    }

    /**
     * The name of the Parameter, as it appears in URL parameters and in
     * diagnostic messages.
     */
    public final String name;

    /**
     * The default, or initial, value of the Parameter. This is never null.
     */
    public final Function<ParameterMap, R> defaultValue;

    /**
     * Constructs a ParameterDescription with the specified name and default
     * value. name and defaultValue are never null.
     * 
     * @param name
     *        the name of this ParameterDescription.
     * @param defaultValue
     *        the default value of this ParameterDescription.
     */
    private ParameterDescription( final String name, final Function<ParameterMap, R> defaultValue )
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public ParameterDescription<T, R> disallowing( final T banned )
    {
        return parameterDisallowing( banned, this );
    }

    /**
     * Parses the specified URLParameter into a value of type T.
     * 
     * @param nameAndValue
     *        the URLParameter to parse.
     * @throws NullPointerException
     *         if nameAndValue is null.
     * @return a value of type T, after parsing.
     */
    public abstract Option<T> fromURLParameter( final URLParameter nameAndValue );

    /**
     * Converts this ParameterDescription plus the value stored for it in the
     * specified ParameterMap into a URL-style parameter, if the value is not
     * the default value.
     * 
     * @param parameterMap
     *        the ParameterMap to get the value from.
     * @throws NullPointerException
     *         if parameterMap is null.
     * @return this ParameterDescription as a URL parameter, or an empty String
     *         if the value of the parameter is the ParameterDescription's
     *         default value.
     */
    public Option<String> toURLParameter( final ParameterMap parameterMap )
    {
        return parameterMap.isDefault( this ) ? Option.getFullOption( "" ) : toURLParameter( parameterMap.get( this ) );
    }

    /**
     * Converts a value into a URLParameter, placing it in an Option if that
     * succeeds, or giving an empty Option if that is an unsupported operation
     * for this ParameterDescription.
     * 
     * @param value
     *        the value to store in the URLParameter.
     * @throws NullPointerException
     *         is value is null.
     * @return a URLParameter representing the specified value for this
     *         ParameterDescription.
     */
    public abstract Option<String> toURLParameter( R value );

    /**
     * Takes in a new value and an original value and produces a new value from
     * merging them. What this actually does depends on the particular
     * ParameterDescription implementation.
     * 
     * @param newValue
     *        the new incoming value.
     * @param original
     *        the previously-stored value.
     * @throws NullPointerException
     *         if newValue or original are null.
     * @return the new value to store.
     */
    abstract R reduce( final ParameterMap map, final T newValue, final R original );

    /**
     * A ParameterDescription for parameters that have a default, or initial,
     * value.
     * 
     * @param <T>
     *        the input and output type of this ParameterDescription.
     */
    public static final class ParameterDescriptionWithDefault<T>
            extends ParameterDescription<T, T>
    {
        /**
         * The conversions between values of type T and String.
         */
        private final StringConversion<T> conversions;

        /**
         * Constructs a ParameterDescriptionWithDefault.
         * 
         * @param name
         *        the name of the parameter.
         * @param defaultValue
         *        the default value of the parameter.
         * @param conversions
         *        the conversions between values of type T and String.
         * @throws NullPointerException
         *         if name, defaultValue or conversions are null.
         */
        private ParameterDescriptionWithDefault( final String name, final Function<ParameterMap, T> defaultValue,
                final StringConversion<T> conversions )
        {
            super( name, defaultValue );
            this.conversions = conversions;
        }

        public ParameterDescription<T, T> allowedValues( final T... values )
        {
            return oneOfWithDefault( name, defaultValue, conversions, values );
        }

        @Override
        public Option<T> fromURLParameter( final URLParameter nameAndValue )
        {
            return conversions.fromString( nameAndValue.value );
        }

        public ParameterDescription<T, T> positive( final Num<T> num )
        {
            return parameterWithBounds( num.succ( num.zero() ), num.maxValue(), num, this );
        }

        @Override
        public T reduce( final ParameterMap map, final T newValue, final T original )
        {
            if ( original.equals( defaultValue.apply( map ) ) )
            {
                return newValue;
            }

            throw new IllegalStateException( "The " + name
                    + " parameter has already been set to a value other than its default" );
        }

        @Override
        public Option<String> toURLParameter( final T value )
        {
            return conversions.toString( value ).map( new Function<String, String>()
            {
                @Override
                public String apply( final String t )
                {
                    return name + '=' + new URLEncoder().apply( t );
                }
            } );
        }

        public ParameterDescription<T, T> withBounds( final T lower, final T upper, final Num<T> num )
        {
            return parameterWithBounds( lower, upper, num, this );
        }
    }

    /**
     * A ParameterDescription for parameters that do not have a default, or
     * initial, value.
     * 
     * @param <T>
     *        The input type of this ParameterDescription.
     */
    public static final class ParameterDescriptionWithoutDefault<T>
            extends ParameterDescription<T, Option<T>>
    {
        /**
         * The conversions between values of type T and Strings.
         */
        private final StringConversion<T> conversion;

        /**
         * Constructs a ParameterDescriptionWithoutDefault.
         * 
         * @param name
         *        the name of the parameter.
         * @param conversion
         *        the conversions between values of type T and Strings.
         */
        private ParameterDescriptionWithoutDefault( final String name, final StringConversion<T> conversion )
        {
            super( name, Function.<ParameterMap, Option<T>> constant( Option.<T> getEmptyOption( "The value for the "
                    + name + " parameter has not been set yet" ) ) );
            this.conversion = conversion;
        }

        @Override
        public Option<T> fromURLParameter( final URLParameter nameAndValue )
        {
            return conversion.fromString( nameAndValue.value );
        }

        @Override
        public Option<T> reduce( final ParameterMap map, final T newValue, final Option<T> original )
        {
            if ( original.isEmpty() )
            {
                return Option.getFullOption( newValue );
            }

            throw new IllegalStateException( "The " + name + " parameter has already been set to a value." );
        }

        @Override
        public Option<String> toURLParameter( final Option<T> value )
        {
            return value.bind( new Function<T, Option<String>>()
            {
                @Override
                public Option<String> apply( final T value )
                {
                    return conversion.toString( value ).map( new Function<String, String>()
                    {
                        @Override
                        public String apply( final String valuePart )
                        {
                            return name + '=' + valuePart;
                        }

                    } );
                }
            } );
        }

        public ParameterDescriptionWithDefault<T> withDefault( final T t )
        {
            return parameterWithDefault( name, t, conversion );
        }
    }

    /**
     * A ParameterDescription for parameters that represent one-dimensional
     * sparse arrays indexed by Integers.
     * 
     * @param <T>
     *        the element type of this SparseArrayParameterDescription.
     */
    public static final class SparseArrayParameterDescription<T>
            extends ParameterDescription<List<Pair<Integer, T>>, TreeMap<Integer, T>>
    {
        /**
         * Functions between values of type T and Strings.
         */
        private final StringConversion<T> conversions;

        /**
         * Constructs a SparseArrayParameterDescription.
         * 
         * @param name
         *        the name of the parameter.
         * @param conversions
         *        conversions between values of type T and Strings.
         * @throws NullPointerException
         *         if name or conversions are null.
         */
        private SparseArrayParameterDescription( final String name, final StringConversion<T> conversions )
        {
            super( name, Function.<ParameterMap, TreeMap<Integer, T>> constant( new TreeMap<Integer, T>() ) );
            this.conversions = conversions;
        }

        @Override
        public Option<List<Pair<Integer, T>>> fromURLParameter( final URLParameter keyAndValue )
        {
            final List<String> values = Strings.splitIgnoringQuotedSections( keyAndValue.value, ',' );
            int startIndex =
                    Integer.parseInt( keyAndValue.name.substring( name.length() + 1, keyAndValue.name.length() - 1 ) );

            final List<Pair<Integer, T>> results = new ArrayList<Pair<Integer, T>>();

            for ( final String value : values )
            {
                final int hack = startIndex;
                for ( final T t : conversions.fromString( Strings.removeSurroundingQuotesLeniently( value ) ) )
                {
                    results.add( new Pair<Integer, T>( hack, t ) );
                }

                startIndex++;
            }

            return Option.getFullOption( results );
        }

        @Override
        public TreeMap<Integer, T> reduce( final ParameterMap map, final List<Pair<Integer, T>> newValue,
                final TreeMap<Integer, T> original )
        {
            final TreeMap<Integer, T> copy = new TreeMap<Integer, T>( original );
            for ( final Pair<Integer, T> pair : newValue )
            {
                if ( copy.containsKey( pair.getFirstComponent() ) )
                {
                    throw new IllegalStateException( "Cannot specify " + name + "[" + pair.getFirstComponent()
                            + "] twice." );
                }

                copy.put( pair.getFirstComponent(), pair.getSecondComponent() );
            }

            return copy;
        }

        @Override
        public Option<String> toURLParameter( final TreeMap<Integer, T> map )
        {
            final StringBuilder result = new StringBuilder();

            for ( final Map.Entry<Integer, T> entry : map.entrySet() )
            {
                for ( final String value : conversions.toString( entry.getValue() ) )
                {
                    if ( result.length() != 0 )
                    {
                        result.append( "&" );
                    }

                    result.append( name + '[' + entry.getKey() + ']' + "=" + new URLEncoder().apply( value ) );
                }
            }

            return Option.getFullOption( result.toString() );
        }
    }

    /**
     * A ParameterDescription that disallows a certain value.
     * 
     * @param <T>
     *        the input type of this ParameterDescription.
     * @param <U>
     *        the output type of this ParameterDescription.
     */
    private static final class BannedParameterDescription<T, U>
            extends ParameterDescription<T, U>
    {
        private final T banned;
        private final ParameterDescription<T, U> delegate;

        private BannedParameterDescription( final T banned, final ParameterDescription<T, U> delegate )
        {
            super( delegate.name, delegate.defaultValue );
            this.banned = banned;
            this.delegate = delegate;
        }

        @Override
        public Option<T> fromURLParameter( final URLParameter nameAndValue )
        {
            return delegate.fromURLParameter( nameAndValue );
        }

        @Override
        public U reduce( final ParameterMap map, final T newValue, final U original )
        {
            if ( newValue.equals( banned ) )
            {
                throw new IllegalArgumentException( "The " + delegate.name
                        + " parameter is not allowed to take the supplied value, " + newValue + '.' );
            }
            return delegate.reduce( map, newValue, original );
        }

        @Override
        public Option<String> toURLParameter( final U valuePair )
        {
            return delegate.toURLParameter( valuePair );
        }
    }

    /**
     * A ParameterDescription for a parameter that accepts values in a certain
     * range.
     * 
     * @param <U>
     *        the output type of this ParameterDescription.
     */
    private static final class BoundParameterDescription<T, U>
            extends ParameterDescription<T, U>
    {
        /**
         * The upper bound of acceptable values.
         */
        private final T higherInclusive;

        /**
         * The lower bound of acceptable values.
         */
        private final T lowerInclusive;

        private final Num<T> num;

        /**
         * The ParameterDescription to delegate to, including passing on all
         * accepted values.
         */
        private final ParameterDescription<T, U> delegate;

        /**
         * Constructs a BoundParameterDescription.
         * 
         * @param lowerInclusive
         *        the lower bound of acceptable values.
         * @param higherInclusive
         *        the upper bound of acceptable values.
         * @param delegate
         *        the ParameterDescription to delegate to, including passing on
         *        all accepted values. delegate is never null.
         */
        private BoundParameterDescription( final T lowerInclusive, final T higherInclusive, final Num<T> num,
                final ParameterDescription<T, U> delegate )
        {
            super( delegate.name, delegate.defaultValue );
            this.higherInclusive = higherInclusive;
            this.lowerInclusive = lowerInclusive;
            this.num = num;
            this.delegate = delegate;
        }

        @Override
        public Option<T> fromURLParameter( final URLParameter nameAndValue )
        {
            return delegate.fromURLParameter( nameAndValue );
        }

        @Override
        public U reduce( final ParameterMap map, final T newValue, final U original )
        {
            if ( num.ge( newValue, lowerInclusive ) && num.le( newValue, higherInclusive ) )
            {
                return delegate.reduce( map, newValue, original );
            }

            throw new IllegalArgumentException( "The value " + newValue + " is not within the bounds for the "
                    + delegate.name + " parameter (" + lowerInclusive + " to " + higherInclusive + " inclusive)." );
        }

        @Override
        public Option<String> toURLParameter( final U value )
        {
            return delegate.toURLParameter( value );
        }
    }
}
