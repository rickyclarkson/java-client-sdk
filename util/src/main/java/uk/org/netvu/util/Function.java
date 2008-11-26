package uk.org.netvu.util;

import java.math.BigInteger;

/**
 * A function from objects of type T to those of type R.
 * 
 * @param <T>
 *        the type of object to convert from.
 * @param <R>
 *        the type of object to convert to.
 */
public abstract class Function<T, R>
{
    /**
     * A Function that returns true if the value it receives is the same as the
     * specified parameter.
     * 
     * @param <T>
     *        the type of the values that this Function can receive.
     * @param other
     *        the parameter to test values against.
     * @throws NullPointerException
     *         if other is null.
     * @return a Function that returns true if the value it receives is the same
     *         as the specified parameter.
     */
    public static <T> Function<T, Boolean> equal( final T other )
    {
        CheckParameters.areNotNull( other );

        return new Function<T, Boolean>()
        {
            @Override
            public Boolean apply( final T t )
            {
                CheckParameters.areNotNull( t );
                return other.equals( t );
            }
        };
    }

    /**
     * A Function that, given a Boolean, returns the ifTrue parameter if the
     * Boolean is true, and returns the ifFalse parameter otherwise.
     * 
     * @param <T>
     *        the type of value to return.
     * @param ifTrue
     *        the value to return if the Boolean is true.
     * @param ifFalse
     *        the value to return if the Boolean is false.
     * @throws NullPointerException
     *         if ifTrue or ifFalse are null.
     * @return a Function that, given a Boolean, returns the ifTrue parameter if
     *         the Boolean is true, and returns the ifFalse parameter otherwise.
     */
    public static <T> Function<Boolean, T> fromBoolean( final T ifTrue, final T ifFalse )
    {
        CheckParameters.areNotNull( ifTrue, ifFalse );

        return new Function<Boolean, T>()
        {
            @Override
            public T apply( final Boolean b )
            {
                return b ? ifTrue : ifFalse;
            }
        };
    }

    /**
     * A partial Function from Strings containing hexadecimal to Integers.
     * 
     * @return a Function from Strings containing hexadecimal to Integers.
     */
    public static Function<String, Option<Integer>> getHexStringToIntFunction()
    {
        return new HexStringToIntFunction();
    }

    /**
     * A partial Function from Strings containing hexadecimal to Longs.
     * 
     * @return a partial Function from Strings containing hexadecimal to Longs.
     */
    public static Function<String, Option<Long>> getHexStringToLongFunction()
    {
        return new HexStringToLongFunction();
    }

    /**
     * The identity function - given an object of type T it results in the same
     * object of type T.
     * 
     * @param <T>
     *        the input and output type of this Function.
     * @return the identity conversion for type T.
     */
    public static <T> Function<T, T> getIdentityFunction()
    {
        return new Function<T, T>()
        {
            @Override
            public T apply( final T t )
            {
                CheckParameters.areNotNull( t );
                return t;
            }
        };
    }

    /**
     * A Function from an Integer to a String containing a hexadecimal
     * representation of the Integer.
     * 
     * @return a Function from an Integer to a String containing hexadecimal.
     */
    public static Function<Integer, String> getIntToHexStringFunction()
    {
        return new Function<Integer, String>()
        {
            @Override
            public String apply( final Integer value )
            {
                return Integer.toHexString( value );
            }
        };
    }

    /**
     * A Function from Longs to Strings containing hexadecimal.
     * 
     * @return a Function from Longs to Strings containing hexadecimal.
     */
    public static Function<Long, String> getLongToHexStringFunction()
    {
        return new Function<Long, String>()
        {
            @Override
            public String apply( final Long value )
            {
                return Long.toHexString( value );
            }
        };
    }

    /**
     * A Function that uses Object's toString() to convert objects of type T to
     * Strings.
     * 
     * @param <T>
     *        the input type.
     * @return a conversion that uses Object's toString() to convert objects of
     *         type T into Strings.
     */
    public static <T> Function<T, String> getObjectToStringFunction()
    {
        return new Function<T, String>()
        {
            @Override
            public String apply( final T t )
            {
                return t.toString();
            }
        };
    }

    /**
     * A case-insensitive conversion from Strings to Booleans, where "true" gets
     * mapped to the Boolean value TRUE, "false" gets mapped to FALSE, and
     * anything else gets mapped to an empty Option.
     * 
     * @return a case-insensitive conversion from Strings to Booleans.
     */
    public static Function<String, Option<Boolean>> getStringToBooleanFunction()
    {
        return new Function<String, Option<Boolean>>()
        {
            @Override
            public Option<Boolean> apply( final String t )
            {
                if ( t.equalsIgnoreCase( "true" ) )
                {
                    return Option.getFullOption( true );
                }
                else if ( t.equalsIgnoreCase( "false" ) )
                {
                    return Option.getFullOption( false );
                }

                return Option.getEmptyOption( t + " is neither true nor false" );
            }
        };
    }

    /**
     * A conversion from Strings to Integers, using Integer.parseInt(String).
     * 
     * @return a conversion from Strings to Integers, using
     *         Integer.parseInt(String).
     */
    public static Function<String, Option<Integer>> getStringToIntFunction()
    {
        return new Function<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> apply( final String t )
            {
                CheckParameters.areNotNull( t );

                try
                {
                    return Option.getFullOption( Integer.parseInt( t ) );
                }
                catch ( final NumberFormatException exception )
                {
                    return Option.getEmptyOption( t + " is not a valid Integer" );
                }
            }
        };
    }

    /**
     * A conversion from Strings to longs, using Long.parseLong(String).
     * 
     * @return a conversion from Strings to longs, using Long.parseLong(String).
     */
    public static Function<String, Option<Long>> getStringToLongFunction()
    {
        return new Function<String, Option<Long>>()
        {
            @Override
            public Option<Long> apply( final String t )
            {
                CheckParameters.areNotNull( t );

                try
                {
                    return Option.getFullOption( Long.parseLong( t ) );
                }
                catch ( final NumberFormatException e )
                {
                    return Option.getEmptyOption( t + " is not a valid long" );
                }
            }
        };
    }

    /**
     * Constructs a Function. This constructor has no effect.
     */
    protected Function()
    {
    }

    /**
     * Composes two conversions together such that an incoming object of type T
     * gets converted by this Function to an object of type R, then gets passed
     * into the conversion supplied as a parameter, to finally produce an object
     * of type V.
     * 
     * @param <V>
     *        the type that the composed conversions convert objects of type T
     *        to.
     * @param conversion
     *        the second conversion to run.
     * @throws NullPointerException
     *         if conversion is null.
     * @return a composed conversion.
     */
    public final <V> Function<T, V> andThen( final Function<R, V> conversion )
    {
        CheckParameters.areNotNull( conversion );

        return new Function<T, V>()
        {
            @Override
            public V apply( final T t )
            {
                final R intermediate = Function.this.apply( t );
                return conversion.apply( intermediate );
            }
        };
    }

    /**
     * Converts an object of type T into an object of type R.
     * 
     * @param t
     *        the object to convert.
     * @throws NullPointerException
     *         if t is null.
     * @return the converted object.
     */
    public abstract R apply( T t );

    /**
     * A partial Function between Strings containing non-negative hexadecimal
     * numbers and Integers.
     */
    private static final class HexStringToIntFunction
            extends Function<String, Option<Integer>>
    {
        @Override
        public Option<Integer> apply( final String t )
        {
            CheckParameters.areNotNull( t );

            try
            {
                final long result = Long.parseLong( t, 16 );

                if ( ( result & 0xFFFFFFFFL ) == result )
                {
                    return Option.getFullOption( (int) result );
                }

                return Option.getEmptyOption( t + " is outside the bounds of an int, in hexadecimal" );
            }
            catch ( final NumberFormatException e )
            {
                return Option.getEmptyOption( t + " is not a valid int, in hexadecimal" );
            }
        }
    }

    /**
     * A partial Function between Strings containing non-negative hexadecimal
     * numbers and Longs.
     */
    private static final class HexStringToLongFunction
            extends Function<String, Option<Long>>
    {
        @Override
        public Option<Long> apply( final String string )
        {
            if ( string.startsWith( "-" ) )
            {
                return Option.getEmptyOption( "getHexStringToLongFunction does not support negative hexadecimal" );
            }

            if ( string.length() > 16 )
            {
                return Option.getEmptyOption( string + " is not a valid long value in hexadecimal" );
            }

            try
            {
                return Option.getFullOption( new BigInteger( string, 16 ).longValue() );
            }
            catch ( final NumberFormatException e )
            {
                return Option.getEmptyOption( string + " is not a valid long, in hexadecimal" );
            }
        }
    }
}
