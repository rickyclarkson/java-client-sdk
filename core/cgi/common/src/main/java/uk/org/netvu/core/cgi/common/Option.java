package uk.org.netvu.core.cgi.common;

/**
 * An object that holds 0 or 1 elements. For internal use only.
 * 
 * @param <T>
 *        the type of the object.
 */
public abstract class Option<T>
{
    /**
     * Creates an Option holding no elements.
     * 
     * @param <T>
     *        the type of the Option.
     * @param reason
     *        the reason that the Option has no elements.
     * @return an Option holding no elements.
     */
    public static <T> Option<T> none( final String reason )
    {
        return new Option<T>()
        {
            @Override
            public <U> Option<U> bind( final Conversion<T, Option<U>> conversion )
            {
                return Option.none( reason );
            }

            @Override
            public T get()
            {
                throw new IllegalStateException(
                        "This Option has no element, get() cannot be called on it.  The reason it has no element: "
                                + reason );
            }

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
            public <U> Option<U> map( final Conversion<T, U> conversion )
            {
                return Option.none( reason );
            }

            @Override
            public String reason()
            {
                return reason;
            }

            @Override
            public void then( final Action<T> action )
            {
            }

            @Override
            <U> U fold( final U ifNone, final Conversion<T, U> ifSome )
            {
                return ifNone;
            }
        };
    }

    /**
     * A Conversion that always yields a None.
     * 
     * @param <T>
     *        the type of the ignored value to convert.
     * @param <U>
     *        the type of the None to produce.
     * @param reason
     *        the reason that the Option has no element.
     * @return a Conversion that always yields a None.
     */
    public static <T, U> Conversion<T, Option<U>> noneRef( final String reason )
    {
        return new Conversion<T, Option<U>>()
        {
            @Override
            public Option<U> convert( final T t )
            {
                return none( reason );
            }
        };
    }

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
                return some( t );
            }

        };
    }

    /**
     * Creates an Option holding one element.
     * 
     * @param <T>
     *        the type of the element.
     * @param t
     *        the element.
     * @return an Option holding one element.
     */
    public static <T> Option<T> some( final T t )
    {
        return new Option<T>()
        {
            @Override
            public <U> Option<U> bind( final Conversion<T, Option<U>> conversion )
            {
                return conversion.convert( t );
            }

            @Override
            public T get()
            {
                return t;
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
            public <U> Option<U> map( final Conversion<T, U> conversion )
            {
                return Option.some( conversion.convert( t ) );
            }

            @Override
            public String reason()
            {
                throw null;
            }

            @Override
            public void then( final Action<T> action )
            {
                action.invoke( t );
            }

            @Override
            <U> U fold( final U ifNone, final Conversion<T, U> ifSome )
            {
                return ifSome.convert( t );
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
                return some( conversion.convert( t ) );
            }
        };
    }

    private Option()
    {
    }

    /**
     * Throws an UnsupportedOperationException to catch any accidental
     * badly-typed equals comparisons early.
     */
    @Override
    public final boolean equals( final Object o )
    {
        throw new UnsupportedOperationException(
                "equals(Object) not supported, to catch any accidental badly-typed equals comparisons early" );
    }

    /**
     * Gets the element held by this Option, or throws an IllegalStateException
     * if there is none.
     * 
     * @return the element held by this Option.
     */
    public abstract T get();

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
     * Throws an UnsupportedOperationException to be consistent with equals.
     */
    @Override
    public final int hashCode()
    {
        throw new UnsupportedOperationException(
                "hashCode not supported, to match equals." );
    }

    /**
     * Identifies whether the Option has no element or not.
     * 
     * @return true if the Option has no element, false otherwise.
     */
    public abstract boolean isNone();

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
     * The reason that the Option has no element.
     * 
     * @throws NullPointerException
     *         if the Option does have an element.
     * @return the reason that the Option has no element.
     */
    public abstract String reason() throws NullPointerException;

    /**
     * Throws an UnsupportedOperationException to catch any accidental
     * toString() calls early.
     */
    @Override
    public final String toString()
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

    /**
     * Folds an Option, producing one value.
     * 
     * @param <U>
     *        the type of the value to return.
     * @param ifNone
     *        the U to return if the Option is a None.
     * @param ifSome
     *        the Conversion to apply if the Option is a Some.
     * @return a folded version of this Option according to the specified
     *         parameters.
     */
    abstract <U> U fold( U ifNone, Conversion<T, U> ifSome );

    /**
     * If this Option is a Some, the specified Action is invoked with the held
     * value. Otherwise, nothing happens.
     * 
     * @param action
     *        the Action to invoke.
     */
    abstract void then( Action<T> action );
}
