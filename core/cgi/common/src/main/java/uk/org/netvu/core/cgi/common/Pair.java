package uk.org.netvu.core.cgi.common;

/**
 * An object containing two objects. For internal use only.
 * 
 * @param <T>
 *        the type of the first object.
 * @param <U>
 *        the type of the second object.
 */
public final class Pair<T, U>
{
    private final T t;
    private final U u;

    private Pair( final T t, final U u )
    {
        this.t = t;
        this.u = u;
    }

    /**
     * Constructs a Pair with the two specified objects.
     * 
     * @param <T>
     *        the type of the first object.
     * @param <U>
     *        the type of the second object.
     * @param t
     *        the first object.
     * @param u
     *        the second object.
     * @return a Pair containing the two specified objects.
     */
    public static <T, U> Pair<T, U> pair( final T t, final U u )
    {
        return new Pair<T, U>( t, u );
    }

    /**
     * Gives the first object in this Pair.
     * 
     * @return the first object in this Pair.
     */
    public T first()
    {
        return t;
    }

    /**
     * Gives the second object in this Pair.
     * 
     * @return the second object in this Pair.
     */
    public U second()
    {
        return u;
    }

    /**
     * A Conversion from a Pair to the second element of the Pair.
     * 
     * @param <T>
     *        the type of the first element of the Pair.
     * @param <U>
     *        the type of the second element of the Pair.
     * @return a Conversion from a Pair to the second element of the Pair.
     */
    public static <T, U> Conversion<Pair<T, U>, U> getSecond()
    {
        return new Conversion<Pair<T, U>, U>()
        {
            @Override
            public U convert( final Pair<T, U> t )
            {
                return t.second();
            }
        };
    }
}
