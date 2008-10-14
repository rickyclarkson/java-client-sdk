package uk.org.netvu.core.cgi.common;

import java.math.BigInteger;

/**
 * For internal use only!
 * A conversion from objects of type T to those of type R.
 * 
 * @param <T>
 *        the type of object to convert from.
 * @param <R>
 *        the type of object to convert to.
 */
public abstract class Conversion<T, R>
{
    /**
     * A Conversion that returns true if the value it receives is the same as
     * the specified parameter.
     * 
     * @param <T>
     *        the type of the values that this Conversion can receive.
     * @param other
     *        the parameter to test values against.
     * @return a Conversion that returns true if the value it receives is the
     *         same as the specified parameter.
     */
    public static <T> Conversion<T, Boolean> equal( final T other )
    {
        CheckParameters.areNotNull( other );

        return new Conversion<T, Boolean>()
        {
            @Override
            public Boolean convert( final T t )
            {
                CheckParameters.areNotNull( t );
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
        CheckParameters.areNotNull( ifTrue, ifFalse );

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
     * A conversion from Strings containing hexadecimal to ints.
     *
     * @return a conversion from Strings containing hexadecimal to ints.
     */
    public static Conversion<String, Option<Integer>> getHexStringToIntConversion()
    {
        return new Conversion<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> convert( final String t )
            {
                CheckParameters.areNotNull( t );

                try
                {
                    long result = Long.parseLong( t, 16 );

                    if ( ( result & 0xFFFFFFFFL ) == result )
                    {
                        return Option.getFullOption( (int)result );
                    }

                    return Option.getEmptyOption( t + " is outside the bounds of an int, in hexadecimal" );
                }
                catch ( final NumberFormatException e )
                {
                    return Option.getEmptyOption( t + " is not a valid int, in hexadecimal" );
                }
            }
        };
    }

    /**
     * A conversion from Strings containing hexadecimal to longs.
     *
     * @return a conversion from Strings containing hexadecimal to longs.
     */
    public static Conversion<String, Option<Long>> getHexStringToLongConversion()
    {
        return new Conversion<String, Option<Long>>()
        {
            @Override
            public Option<Long> convert( final String string )
            {
                if ( string.startsWith("-"))
                {
                    return Option.getEmptyOption("getHexStringToLongConversion does not support negative hexadecimal");
                }

                if ( string.length() > 16 )
                {
                    return Option.getEmptyOption(string + " is not a valid long value in hexadecimal");
                }
                    
                try
                {
                    return Option.getFullOption( new BigInteger( string, 16 ).longValue() );
                }
                catch ( final NumberFormatException e )
                {
                    return Option.getEmptyOption( string
                            + " is not a valid long, in hexadecimal" );
                }
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
    public static <T> Conversion<T, T> getIdentityConversion()
    {
        return new Conversion<T, T>()
        {
            @Override
            public T convert( final T t )
            {
                CheckParameters.areNotNull( t );
                return t;
            }
        };
    }

    /**
     * A Conversion from an Integer to a String containing a hexadecimal
     * representation of the Integer.
     * 
     * @return a Conversion from an Integer to a String containing hexadecimal.
     */
    public static Conversion<Integer, String> getIntToHexStringConversion()
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
     * A conversion from longs to Strings containing hexadecimal.
     *
     * @return a conversion from longs to Strings containing hexadecimal.
     */
    public static Conversion<Long, String> getLongToHexStringConversion()
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
     * A conversion that uses Object's toString() to convert objects of type T
     * to Strings.
     * 
     * @param <T>
     *        the input type.
     * @return a conversion that uses Object's toString() to convert objects of
     *         type T into Strings.
     */
    public static <T> Conversion<T, String> getObjectToStringConversion()
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
     * A case-insensitive conversion from Strings to Booleans, where "true" gets mapped to the
     * Boolean value TRUE, "false" gets mapped to FALSE, and anything else gets
     * mapped to an empty Option.
     * @return a case-insensitive conversion from Strings to Booleans.
     */
    public static Conversion<String, Option<Boolean>> getStringToBooleanConversion()
    {
        return new Conversion<String, Option<Boolean>>()
        {
            @Override
            public Option<Boolean> convert( final String t )
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
     * @return a conversion from Strings to Integers, using Integer.parseInt(String).
     */
    public static Conversion<String, Option<Integer>> getStringToIntConversion()
    {
        return new Conversion<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> convert( final String t )
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
    public static Conversion<String, Option<Long>> getStringToLongConversion()
    {
        return new Conversion<String, Option<Long>>()
        {
            @Override
            public Option<Long> convert( final String t )
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
     * Constructs a Conversion. This constructor has no effect.
     */
    protected Conversion()
    {
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
    public <V> Conversion<T, V> andThen( final Conversion<R, V> conversion )
    {
        return new Conversion<T, V>()
        {
            @Override
            public V convert( final T t )
            {
                R intermediate = Conversion.this.convert( t );
                return conversion.convert( intermediate );
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
