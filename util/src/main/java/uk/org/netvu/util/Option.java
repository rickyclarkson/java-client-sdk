package uk.org.netvu.util;

import java.util.Collections;
import java.util.Iterator;

/**
 * An object that holds 0 or 1 elements.
 * 
 * @param <T>
 *        the type of the object.
 */
public abstract class Option<T> implements Iterable<T>
{
    /**
     * Creates an Option holding no elements.
     * 
     * @param <T>
     *        the type of the Option.
     * @param reason
     *        the reason that the Option has no elements.
     * @throws NullPointerException
     *         if reason is null.
     * @return an Option holding no elements.
     */
    public static <T> Option<T> getEmptyOption( final String reason )
    {
        CheckParameters.areNotNull( reason );

        return new Empty<T>( reason );
    }

    /**
     * Creates an Option holding one element.
     * 
     * @param <T>
     *        the type of the element.
     * @param t
     *        the element.
     * @throws NullPointerException
     *         if t is null.
     * @return an Option holding one element.
     */
    public static <T> Option<T> getFullOption( final T t )
    {
        CheckParameters.areNotNull( t );

        return new Full<T>( t );
    }

    /**
     * A Function that always yields an empty Option.
     * 
     * @param <T>
     *        the type of the ignored value to convert.
     * @param <U>
     *        the type of the Option to produce.
     * @param reason
     *        the reason that the Option has no element.
     * @throws NullPointerException
     *         if reason is null.
     * @return a Function that always yields an empty Option.
     */
    public static <T, U> Function<T, Option<U>> getFunctionToEmptyOption( final String reason )
    {
        CheckParameters.areNotNull( reason );

        return new Function<T, Option<U>>()
        {
            @Override
            public Option<U> apply( final T t )
            {
                return Option.getEmptyOption( reason );
            }
        };
    }

    /**
     * A Function that yields an Option containing the value given to it.
     * 
     * @param <T>
     *        the type of the values that this Function takes.
     * @return a Function that yields an Option containing the value given to
     *         it.
     */
    public static <T> Function<T, Option<T>> getFunctionToFullOption()
    {
        return new Function<T, Option<T>>()
        {
            @Override
            public Option<T> apply( final T t )
            {
                return Option.getFullOption( t );
            }
        };
    }

    /**
     * A Function that takes in a T, converts it to a U, and produces an Option
     * containing that U.
     * 
     * @param <T>
     *        the type of the value to take in.
     * @param <U>
     *        the type of the Option to produce.
     * @param function
     *        the Function to convert the T into a U.
     * @throws NullPointerException
     *         if function is null.
     * @return a Function that takes in a T, converts it to a U, and produces an
     *         Option containing that U.
     */
    public static <T, U> Function<T, Option<U>> toPartialFunction( final Function<T, U> function )
    {
        CheckParameters.areNotNull( function );

        return new Function<T, Option<U>>()
        {
            @Override
            public Option<U> apply( final T t )
            {
                return Option.getFullOption( function.apply( t ) );
            }
        };
    }

    /**
     * This is private to prevent subclassing other than by the code in this
     * class.
     */
    private Option()
    {
    }

    /**
     * Applies the specified Function to a value held by this Option.
     * Specifically, if this Option is empty, the Function is not invoked - an
     * empty Option is returned. If this Option is full, the Function is
     * invoked, and its result is returned.
     * 
     * @param <U>
     *        the type of the Option to convert this Option to.
     * @param function
     *        the Function to apply to a value held by this Option.
     * @throws NullPointerException
     *         if conversion is null.
     * @return an Option holding the result of binding the specified Function to
     *         this Option.
     */
    public abstract <U> Option<U> bind( Function<T, Option<U>> function );

    /**
     * Throws an UnsupportedOperationException to catch any accidental
     * badly-typed equals comparisons early.
     * 
     * @throws UnsupportedOperationException
     *         in all cases.
     */
    @Override
    public final boolean equals( final Object o )
    {
        throw new UnsupportedOperationException(
                "equals(Object) not supported, to catch any accidental badly-typed equals comparisons early" );
    }

    /**
     * Folds an Option, producing one value.
     * 
     * @param <U>
     *        the type of the value to return.
     * @param ifEmpty
     *        the U to return if the Option holds no value.
     * @param ifFull
     *        the Function to apply if the Option has a value.
     * @throws NullPointerException
     *         if ifEmpty or ifFull are null.
     * @return a folded version of this Option according to the specified
     *         parameters.
     */
    public abstract <U> U fold( U ifEmpty, Function<T, U> ifFull );

    /**
     * Gets the element held by this Option, or throws an IllegalStateException
     * if there is none.
     * 
     * @return the element held by this Option.
     */
    public abstract T get();

    /**
     * Throws an UnsupportedOperationException to be consistent with equals.
     * 
     * @throws UnsupportedOperationException
     *         in all cases.
     */
    @Override
    public final int hashCode()
    {
        throw new UnsupportedOperationException( "hashCode not supported, to match equals." );
    }

    /**
     * Identifies whether the Option is empty or has an element.
     * 
     * @return true if the Option has no element, false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * Maps the specified Function over this Option. If the Option has an
     * element, then a new Option will be produced containing the value produced
     * by running the Function on the value held by this Option. If the Option
     * is empty, an empty Option will be returned.
     * 
     * @param <U>
     *        the type to convert to.
     * @param function
     *        the Function to map over this Option.
     * @throws NullPointerException
     *         if function is null.
     * @return an Option containing the mapped value, or nothing.
     */
    public abstract <U> Option<U> map( Function<T, U> function );

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
     * 
     * @throws UnsupportedOperationException
     *         in all cases.
     */
    @Override
    public final String toString()
    {
        throw new UnsupportedOperationException(
                "toString() not supported, to catch any accidental toString() calls early" );
    }

    /**
     * An Option that does not hold a value.
     * 
     * @param <T>
     *        the type of this Option.
     */
    private static final class Empty<T> extends Option<T>
    {
        /**
         * The reason that this Option is empty.
         */
        private final String reason;

        /**
         * Constructs an Empty with the specified reason.
         * 
         * @param reason
         *        the reason that this Option is empty.
         */
        private Empty( final String reason )
        {
            this.reason = reason;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Option<U> bind( final Function<T, Option<U>> function )
        {
            CheckParameters.areNotNull( function );

            return Option.getEmptyOption( reason );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> U fold( final U ifEmpty, final Function<T, U> ifFull )
        {
            CheckParameters.areNotNull( ifEmpty, ifFull );

            return ifEmpty;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get()
        {
            throw new IllegalStateException(
                    "This Option has no element, get() cannot be called on it.  The reason it has no element: "
                            + reason );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty()
        {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<T> iterator()
        {
            return Collections.<T> emptyList().iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Option<U> map( final Function<T, U> function )
        {
            CheckParameters.areNotNull( function );

            return Option.getEmptyOption( reason );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String reason()
        {
            return reason;
        }
    }

    /**
     * An Option that holds a value.
     * 
     * @param <T>
     *        the type of this Option.
     */
    private static final class Full<T> extends Option<T>
    {
        /**
         * The value held by this Full.
         */
        private final T t;

        /**
         * Constructs a Full with the specified value.
         * 
         * @param t
         *        the value held by this Full.
         */
        private Full( final T t )
        {
            this.t = t;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Option<U> bind( final Function<T, Option<U>> conversion )
        {
            return conversion.apply( t );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> U fold( final U ifEmpty, final Function<T, U> ifFull )
        {
            CheckParameters.areNotNull( ifEmpty );

            return ifFull.apply( t );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get()
        {
            return t;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty()
        {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<T> iterator()
        {
            return Collections.singletonList( t ).iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Option<U> map( final Function<T, U> conversion )
        {
            return Option.getFullOption( conversion.apply( t ) );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String reason()
        {
            throw new IllegalStateException( "This Option has a value; reason() is only valid on an empty Option" );
        }
    }
}
