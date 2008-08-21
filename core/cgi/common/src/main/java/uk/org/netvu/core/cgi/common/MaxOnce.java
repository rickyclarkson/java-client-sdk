package uk.org.netvu.core.cgi.common;


/**
 * A mutable container that stores a default value and allows at most one
 * overwrite of it.
 */
public final class MaxOnce<T>
{
    private T t;
    private int timesSet = 0;
    private final String name;

    /**
     * A copy-to-field constructor.
     */
    public MaxOnce( final T t, final String name )
    {
        this.t = t;
        this.name = name;
    }

    /**
     * Sets the value stored by this MaxOnce to a new value, or throws an
     * exception if it has already been set once.
     * 
     * @param t
     *        the value to store.
     */
    public void set( final T t )
    {
        if ( timesSet != 0 )
        {
            throw new IllegalStateException( "Trying to set " + name
                    + " more than once" );
        }

        this.t = t;
        timesSet++;
    }

    /**
     * Returns the most recent value that this MaxOnce has been set to.
     */
    public T get()
    {
        return t;
    }

    /**
     * An assertion that this MaxOnce is unset.
     * 
     * @throws IllegalArgumentException
     *         if this MaxOnce has already been set to something other than its
     *         default value.
     */
    public void isUnset()
    {
        if ( timesSet != 0 )
        {
            throw new IllegalStateException( name
                    + " should be unset, but has been set to a value of " + t );
        }
    }

    public Action<T> setter()
    {
        return new Action<T>()
        {
            public void invoke( final T t )
            {
                set( t );
            }
        };
    }
}
