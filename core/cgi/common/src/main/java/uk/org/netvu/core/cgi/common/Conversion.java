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
     * A conversion from Strings to Booleans, using Boolean.valueOf(String).
     */
    public static final Conversion<String, Boolean> stringToBoolean = new Conversion<String, Boolean>()
    {
        @Override
        public Boolean convert( final String t )
        {
            return Boolean.valueOf( t );
        }
    };

    /**
     * A conversion from Strings to Integers, using Integer.parseInt(String).
     */
    public static final Conversion<String, Option<Integer>> stringToInt = new Conversion<String, Option<Integer>>()
    {
        @Override
        public Option<Integer> convert( final String t )
        {
            try
            {
                return Option.some( Integer.parseInt( t ) );
            }
            catch ( final NumberFormatException exception )
            {
                return Option.none();
            }
        }
    };

    /**
     * A conversion from Strings containing hexadecimal to ints.
     */
    public static final Conversion<String, Integer> hexStringToInt = new Conversion<String, Integer>()
    {
        @Override
        public Integer convert( final String t )
        {
            return (int) Long.parseLong( t, 16 );
        }
    };

    /**
     * A conversion from Strings containing hexadecimal to longs.
     */
    public static final Conversion<String, Long> hexStringToLong = new Conversion<String, Long>()
    {
        @Override
        public Long convert( final String t )
        {
            return new BigInteger( t, 16 ).longValue();
        }
    };

    /**
     * A conversion from Strings to longs, using Long.parseLong(String).
     */
    public static final Conversion<String, Long> stringToLong = new Conversion<String, Long>()
    {
        @Override
        public Long convert( final String t )
        {
            return Long.parseLong( t );
        }
    };

    /**
     * A conversion from longs to Strings containing hexadecimal.
     */
    public static final Conversion<Long, String> longToHexString = new Conversion<Long, String>()
    {
        @Override
        public String convert( final Long value )
        {
            return Long.toHexString( value );
        }
    };

    /**
     * A conversion from ints to Strings containing hexadecimal.
     */
    public static final Conversion<Integer, String> intToHexString = new Conversion<Integer, String>()
    {
        @Override
        public String convert( final Integer value )
        {
            return Integer.toHexString( value );
        }
    };

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
                return t;
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
}
