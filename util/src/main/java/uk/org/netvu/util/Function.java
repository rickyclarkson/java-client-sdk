package uk.org.netvu.util;

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
            public Boolean apply( T t )
            {
                CheckParameters.areNotNull( t );
                return other.equals( t );
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
     * Gives a Function that ignores its argument and instead always returns the
     * specified value.
     * 
     * @param <T>
     *        the argument type of the returned Function.
     * @param <R>
     *        the return type of the returned Function.
     * @param r
     *        the value that the returned Function returns.
     * @return a Function that ignores its argument and instead always returns
     *         the specified value.
     */
    public static <T, R> Function<T, R> constant( final R r )
    {
        return new Function<T, R>()
        {
            @Override
            public R apply( T ignored )
            {
                return r;
            }
        };
    }
}
