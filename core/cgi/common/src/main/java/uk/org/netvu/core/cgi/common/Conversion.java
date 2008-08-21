package uk.org.netvu.core.cgi.common;

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
}
