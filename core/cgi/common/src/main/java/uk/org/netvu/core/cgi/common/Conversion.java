package uk.org.netvu.core.cgi.common;

import java.math.BigInteger;

/**
 * A conversion from objects of type T to those of type R. For internal use
 * only.
 * 
 * @param <T>
 *        the type of object to convert from.
 * @param <R>
 *        the type of object to convert to.
 */
public abstract class Conversion<T, R>
{
    /**
     * Constructs a Conversion.  This constructor has no effect.
     */
    public Conversion()
    {
    }

    /**
     * A conversion from Strings to Booleans, where "true" gets mapped to the Boolean value TRUE, "false"
     * gets mapped to FALSE, and anything else gets mapped to an empty Option.
     */
    public static Conversion<String, Option<Boolean>> stringToBoolean()
    {
        return new Conversion<String, Option<Boolean>>()
        {
            @Override
            public Option<Boolean> convert( final String t )
            {
                if (t.equals( "true" ))
                {
                    return Option.some( true );
                }
                else if (t.equals( "false" ))
                {
                    return Option.some( false );
                }
            
                return Option.none( t + " is neither true nor false" );
            }
        };
    }

    /**
     * A conversion from Strings to Integers, using Integer.parseInt(String).
     */
    public static Conversion<String, Option<Integer>> stringToInt()
    {
        return new Conversion<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> convert( final String t )
            {
                Checks.notNull( t );

                try
                {
                    return Option.some( Integer.parseInt( t ) );
                }
                catch ( final NumberFormatException exception )
                {
                    return Option.none( t + " is not a valid Integer" );
                }
            }
        };
    }

    /**
     * A conversion from Strings containing hexadecimal to ints.
     */
    public static final Conversion<String, Option<Integer>> hexStringToInt()
    {
        return new Conversion<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> convert( final String t )
            {
                Checks.notNull( t );

                try
                {
                    return Option.some( (int) Long.parseLong( t, 16 ) );
                }
                catch ( final NumberFormatException e )
                {
                    return Option.none( t + " is not a valid long" );
                }
            }
        };
    }

    /**
     * A conversion from Strings containing hexadecimal to longs.
     */
    public static final Conversion<String, Option<Long>> hexStringToLong()
    {
        return new Conversion<String, Option<Long>>()
        {
            @Override
            public Option<Long> convert( final String t )
            {
                try
                {
                    return Option.some( new BigInteger( t, 16 ).longValue() );
                }
                catch ( final NumberFormatException e )
                {
                    return Option.none( t + " is not a valid long, in hexadecimal" );
                }
            }
        };
    }

    /**
     * A conversion from Strings to longs, using Long.parseLong(String).
     */
    public static final Conversion<String, Option<Long>> stringToLong()
    {
        return new Conversion<String, Option<Long>>()
        {
            @Override
            public Option<Long> convert( final String t )
            {
                Checks.notNull( t );

                try
                {
                    return Option.some(Long.parseLong( t ));
                }
                catch (NumberFormatException e)
                {
                    return Option.none(t+" is not a valid long");
                }
            }
        };
    }

    /**
     * A conversion from longs to Strings containing hexadecimal.
     */
    public static final Conversion<Long, String> longToHexString()
    {
        return new Conversion<Long, String>()
        {
            @Override
            public String convert( final Long value )
            {
                return Long.toHexString( value );
            }
        };
    }

    /**
     * A conversion from ints to Strings containing hexadecimal.
     */
    public static final Conversion<Integer, String> intToHexString()
    {
        return new Conversion<Integer, String>()
        {
            @Override
            public String convert( final Integer value )
            {
                return Integer.toHexString( value );
            }
        };
    }

    /**
     * A Conversion that returns true if the value it receives is the same as
     * the specified parameter.
     * 
     * @param <T>
     *        the type of the values that this Conversion can receive.
     * @param other
     *        the parameter to test values again.
     * @return a Conversion that returns true if the value it receives is the
     *         same as the specified parameter.
     */
    public static <T> Conversion<T, Boolean> equal( final T other )
    {
        Checks.notNull( other );

        return new Conversion<T, Boolean>()
        {
            @Override
            public Boolean convert( final T t )
            {
                Checks.notNull( t );
                return other.equals( t );
            }
        };
    }

    /**
     * A Conversion that, given a boolean, returns the ifTrue parameter if the
     * boolean is true, and returns the ifFalse parameter otherwise.
     * 
     * @param <T>
     *        the type of value to return.
     * @param ifTrue
     *        the value to return if the boolean is true.
     * @param ifFalse
     *        the value to return if the boolean is false.
     * @return a Conversion that, given a boolean, returns the ifTrue parameter
     *         if the boolean is true, and returns the ifFalse parameter
     *         otherwise.
     */
    public static <T> Conversion<Boolean, T> fromBoolean( final T ifTrue,
            final T ifFalse )
    {
        Checks.notNull( ifTrue, ifFalse );

        return new Conversion<Boolean, T>()
        {
            @Override
            public T convert( final Boolean b )
            {
                return b ? ifTrue : ifFalse;
            }
        };
    }

    /**
     * The identity conversion - given an object of type T it results in the
     * same object of type T.
     * 
     * @param <T>
     *        the input and output type of this conversion.
     * @return the identity conversion for type T.
     */
    public static <T> Conversion<T, T> identity()
    {
        return new Conversion<T, T>()
        {
            @Override
            public T convert( final T t )
            {
                Checks.notNull( t );
                return t;
            }
        };
    }

    /**
     * A conversion that uses Object's toString() to convert objects of type T
     * to Strings.
     * 
     * @param <T>
     *        the input type.
     * @return a conversion that uses Object's toString() to convert objects of
     *         type T into Strings.
     */
    public static <T> Conversion<T, String> objectToString()
    {
        return new Conversion<T, String>()
        {
            @Override
            public String convert( final T t )
            {
                return t.toString();
            }
        };
    }

    /**
     * Composes two conversions together such that an incoming object of type T
     * gets converted by this Conversion to an object of type R, then gets
     * passed into the conversion supplied as a parameter, to finally produce an
     * object of type V.
     * 
     * @param <V>
     *        the type that the composed conversions convert objects of type T
     *        to.
     * @param conversion
     *        the second conversion to run.
     * @return a composed conversion.
     */
    public final <V> Conversion<T, V> andThen( final Conversion<R, V> conversion )
    {
        return new Conversion<T, V>()
        {
            @Override
            public V convert( final T t )
            {
                return conversion.convert( Conversion.this.convert( t ) );
            }
        };
    }

    /**
     * Converts an object of type T into an object of type R.
     * 
     * @param t
     *        the object to convert.
     * @return the converted object.
     */
    public abstract R convert( T t );
}
