package uk.org.netvu.core.cgi.common;

import java.math.BigInteger;

public abstract class Conversion<T, R>
{
    public static final Conversion<String, Boolean> stringToBoolean = new Conversion<String, Boolean>()
    {
        @Override
        public Boolean convert( final String t )
        {
            return Boolean.valueOf( t );
        }
    };
    public static final Conversion<String, Integer> stringToInt = new Conversion<String, Integer>()
    {
        @Override
        public Integer convert( final String t )
        {
            return Integer.parseInt( t );
        }
    };

    public static final Conversion<String, Integer> hexStringToInt = new Conversion<String, Integer>()
    {
        @Override
        public Integer convert( final String t )
        {
            return (int) Long.parseLong( t, 16 );
        }
    };

    public static final Conversion<String, Long> hexStringToLong = new Conversion<String, Long>()
    {
        @Override
        public Long convert( final String t )
        {
            return new BigInteger( t, 16 ).longValue();
        }
    };

    public static final Conversion<String, Long> stringToLong = new Conversion<String, Long>()
    {
        @Override
        public Long convert( final String t )
        {
            return Long.parseLong( t );
        }
    };

    public static final Conversion<Long, String> longToHexString = new Conversion<Long, String>()
    {
        @Override
        public String convert( final Long value )
        {
            return Long.toHexString( value );
        }
    };

    public static Conversion<Integer, String> intToHexString = new Conversion<Integer, String>()
    {
        @Override
        public String convert( final Integer value )
        {
            return Integer.toHexString( value );
        }
    };

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

    public abstract R convert( T t );

    public <V> Conversion<T, V> andThen( final Conversion<R, V> conversion )
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

    public static <T, R> Conversion<T, R> throwUnsupportedOperationException()
    {
        return new Conversion<T, R>()
        {
            @Override
            public R convert( final T t )
            {
                throw new UnsupportedOperationException();
            }
        };
    }

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
