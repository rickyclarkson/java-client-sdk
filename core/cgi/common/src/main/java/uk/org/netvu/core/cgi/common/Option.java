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

        @Override
        public <U> Option<U> map( final Conversion<T, U> conversion )
        {
            return new Some<U>( conversion.convert( t ) );
        }

        @Override
        public void then( final Action<T> action )
        {
            action.invoke( t );
        }

        @Override
        public <U> Option<U> bind( final Conversion<T, Option<U>> conversion )
        {
            return conversion.convert( t );
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
            throw new IllegalStateException(
                    "This Option has no element, get() cannot be called on it." );
        }

        @Override
        public <U> Option<U> map( final Conversion<T, U> conversion )
        {
            return new None<U>();
        }

        @Override
        public void then( final Action<T> action )
        {
        }

        @Override
        public <U> Option<U> bind( final Conversion<T, Option<U>> conversion )
        {
            return new Option.None<U>();
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

    /**
     * Maps the specified Conversion over this Option. If the Option is a Some,
     * then a new Some will be produced containing the value produced by running
     * the Conversion on the value held by this Option. If the Option is a None,
     * then None will be returned.
     * 
     * @param <U>
     *        the type to convert to.
     * @param conversion
     *        the Conversion to map over this Option.
     * @return an Option containing the mapped value, or None.
     */
    public abstract <U> Option<U> map( Conversion<T, U> conversion );

    /**
     * A Conversion that always yields a None.
     * 
     * @param <T>
     *        the type of the ignored value to convert.
     * @param <U>
     *        the type of the None to produce.
     * @return a Conversion that always yields a None.
     */
    public static <T, U> Conversion<T, Option<U>> noneRef()
    {
        return new Conversion<T, Option<U>>()
        {
            @Override
            public Option<U> convert( final T t )
            {
                return new Option.None<U>();
            }
        };
    }

    /**
     * If this Option is a Some, the specified Action is invoked with the held
     * value. Otherwise, nothing happens.
     * 
     * @param action
     *        the Action to invoke.
     */
    abstract void then( Action<T> action );

    /**
     * A Conversion that yields a Some containing the value given to it.
     * 
     * @param <T>
     *        the type of the values that this Conversion takes.
     * @return a Conversion that yields a Some containing the value given to it.
     */
    public static <T> Conversion<T, Option<T>> some()
    {
        return new Conversion<T, Option<T>>()
        {
            @Override
            public Option<T> convert( final T t )
            {
                return new Option.Some<T>( t );
            }

        };
    }

    /**
     * A Conversion that takes in a T, converts it to a U, and produces a Some
     * containing that U.
     * 
     * @param <T>
     *        the type of the value to take in.
     * @param <U>
     *        the type of the Some to produce.
     * @param conversion
     *        the Conversion to convert the T into a U.
     * @return a Conversion that takes in a T, converts it to a U, and produces
     *         a Some containing that U.
     */
    public static <T, U> Conversion<T, Option<U>> someRef(
            final Conversion<T, U> conversion )
    {
        return new Conversion<T, Option<U>>()
        {
            @Override
            public Option<U> convert( final T t )
            {
                return new Option.Some<U>( conversion.convert( t ) );
            }
        };
    }

    @Override
    public String toString()
    {
        throw new UnsupportedOperationException(
                "toString() not supported, to catch any accidental toString() calls early" );
    }

    /**
     * Applies the specified Conversion to a value held by this Option.
     * Specifically, if this Option is a None, the Conversion is not invoked - a
     * None is returned. If this Option is a Some, the Conversion is invoked,
     * and its result is returned.
     * 
     * @param <U>
     *        the type of the Option to convert this Option to.
     * @param conversion
     *        the Conversion to apply to a value held by this Option.
     * @return an Option holding the result of binding the specified Conversion
     *         to this Option.
     */
    abstract <U> Option<U> bind( Conversion<T, Option<U>> conversion );
}
