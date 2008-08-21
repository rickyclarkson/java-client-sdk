package uk.org.netvu.core.cgi.common;

/**
 * A fluent interface for checks on comparables.
 */
public class Checks<T extends Comparable<T>>
{
    /**
     * Creates a Checks object to run checks against a supplied value.
     * 
     * @param x
     *        the value to be checked
     * @param name
     *        the name to use for x in any exceptions generated.
     * @return a Checks object that can run checks against x.
     */
    public static <T extends Comparable<T>> Checks<T> checks( final T x,
            final String name )
    {
        return new Checks<T>( x, name );
    }

    /**
     * The value to run checks against.
     */
    private final T x;

    /**
     * The name to use for the stored value in any exceptions generated.
     */
    private final String name;

    /**
     * A copy-to-field constructor.
     */
    private Checks( final T x, final String name )
    {
        this.x = x;
        this.name = name;
    }

    /**
     * Checks that the stored value is not less than y.
     * 
     * @param y
     *        the lower limit that the stored value is allowed to be.
     * @param yName
     *        the name to use for y in any exceptions generated.
     * @return the same Checks object.
     * @throws IllegalArgumentException
     *         if the stored value is less than y.
     */
    public Checks<T> notLessThan( final T y, final String yName )
    {
        if ( x.compareTo( y ) < 0 )
        {
            throw new IllegalArgumentException( name + '<' + yName + " i.e. "
                    + x + '<' + y );
        }

        return this;
    }

    /**
     * Checks that the stored value is not greater than y.
     * 
     * @param y
     *        the upper limit that the stored value is allowed to be.
     * @param yName
     *        the name to use for y in any exceptions generated.
     * @return the same Checks object.
     * @throws IllegalArgument
     *         if the stored value is greater than y.
     */
    public Checks<T> notGreaterThan( final T y, final String yName )
    {
        if ( x.compareTo( y ) > 0 )
        {
            throw new IllegalArgumentException( name + '>' + yName + " i.e. "
                    + x + '>' + y );
        }

        return this;
    }

    /**
     * Returns the stored value.
     */
    public T done()
    {
        return x;
    }

    /**
     * Checks that the stored value is not null.
     * 
     * @return the same Checks object.
     * @throws NullPointerException
     *         if the stored value is null.
     */
    public Checks<T> notNull()
    {
        return notNull( x, name, this );
    }

    /**
     * Checks that t is not null.
     * 
     * @param t
     *        the value to check.
     * @param name
     *        the name to use for t in any exceptions generated.
     * @return t
     */
    public static <T> T notNull( final T t, final String name )
    {
        return notNull( t, name, t );
    }

    /**
     * Checks that o is not null.
     * 
     * @param o
     *        the value to check.
     * @param name
     *        the name to use for o in any exceptions generated.
     * @param returning
     *        the value to return from this method, for use in a fluent
     *        interface.
     * @return the value of 'returning'.
     * @throws NullPointerException
     *         if o is null.
     */
    private static <R> R notNull( final Object o, final String name,
            final R returning )
    {
        if ( o == null )
        {
            throw new NullPointerException( name + " == " + null );
        }

        return returning;
    }
}
