package uk.org.netvu.core.cgi.common;

/**
 * For internal use only! An object containing two objects.
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

    /**
     * Constructs a Pair with the two specified objects.
     * 
     * @param t
     *        the first object.
     * @param u
     *        the second object.
     * @return a Pair containing the two specified objects.
     */
    public Pair( final T t, final U u )
    {
        CheckParameters.areNotNull( t, u );

        this.t = t;
        this.u = u;
    }

    /**
     * Compares this object and another for equality.
     * 
     * @return true if the other object is a Pair with equivalent values, false
     *         otherwise.
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public boolean equals( final Object other )
    {
        return other instanceof Pair
                && ( (Pair) other ).getFirstComponent().equals(
                        getFirstComponent() )
                && ( (Pair) other ).getSecondComponent().equals(
                        getSecondComponent() );
    }

    /**
     * Gives the first object in this Pair.
     * 
     * @return the first object in this Pair.
     */
    public T getFirstComponent()
    {
        return t;
    }

    /**
     * Gives the second object in this Pair.
     * 
     * @return the second object in this Pair.
     */
    public U getSecondComponent()
    {
        return u;
    }

    /**
     * Gives a hashCode computed from the fields, for consistency with equals.
     */
    @Override
    public int hashCode()
    {
        return getFirstComponent().hashCode() + 13651
                * getSecondComponent().hashCode();
    }
}
