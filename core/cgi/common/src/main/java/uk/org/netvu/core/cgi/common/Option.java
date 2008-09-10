package uk.org.netvu.core.cgi.common;

/**
 * An object that holds 0 or 1 elements. For internal use only.
 * 
 * @param <T>
 *        the type of the object.
 */
public abstract class Option<T>
{
    private Option()
    {
    }

    /**
     * Gets the stored element, or returns the specified reserve if there is
     * none.
     * 
     * @param reserve
     *        the reserve value.
     * @return the stored element, or the specified reserve if there is none.
     */
    public abstract T getOrElse( T reserve );

    /**
     * An implementation of Option that has 1 element.
     * 
     * @param <T>
     *        the type of the element.
     */
    public static final class Some<T>
            extends Option<T>
    {
        private final T t;

        /**
         * @param t
         *        the value that this Some holds.
         */
        public Some( final T t )
        {
            this.t = t;
        }

        @Override
        public T getOrElse( final T reserve )
        {
            return t;
        }

        @Override
        public boolean isNone()
        {
            return false;
        }

        @Override
        public T get()
        {
            return t;
        }
    }

    /**
     * An implementation of Option that has no element.
     * 
     * @param <T>
     *        the type of this Option.
     */
    public static final class None<T>
            extends Option<T>
    {
        @Override
        public T getOrElse( final T reserve )
        {
            return reserve;
        }

        @Override
        public boolean isNone()
        {
            return true;
        }

        @Override
        public T get()
        {
            throw new IllegalStateException();
        }
    }

    /**
     * Identifies whether the Option has no element or not.
     * 
     * @return true if the Option has no element, false otherwise.
     */
    public abstract boolean isNone();

    /**
     * Gets the element held by this Option, or throws an IllegalStateException
     * if there is none.
     * 
     * @return the element held by this Option.
     */
    public abstract T get();
}
