package uk.org.netvu.core.cgi.common;

import java.util.Collections;
import java.util.Iterator;

/**
 * For internal use only.
 * An object that holds 0 or 1 elements.
 * 
 * @param <T>
 *        the type of the object.
 */
public abstract class Option<T> implements Iterable<T>
{
    private static final class Full<T>
            extends Option<T>
    {
        private final T t;

        private Full( final T t )
        {
            this.t = t;
        }

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
        public boolean isEmpty()
        {
            return false;
        }

        public Iterator<T> iterator()
        {
            return Collections.singletonList( t ).iterator();
        }

        @Override
        public <U> Option<U> map( final Conversion<T, U> conversion )
        {
            return Option.getFullOption( conversion.convert( t ) );
        }

        @Override
        public String reason()
        {
            throw new IllegalStateException("This Option has a value; reason() is only valid on an empty Option");
        }

        @Override
        <U> U fold( final U ifEmpty, final Conversion<T, U> ifFull )
        {
            CheckParameters.areNotNull( ifEmpty );

            return ifFull.convert( t );
        }
    }

    private static final class Empty<T>
            extends Option<T>
    {
        private final String reason;

        private Empty( final String reason )
        {
            this.reason = reason;
        }

        @Override
        public <U> Option<U> bind( final Conversion<T, Option<U>> conversion )
        {
            CheckParameters.areNotNull( conversion );

            return Option.getEmptyOption( reason );
        }

        @Override
        public T get()
        {
            throw new IllegalStateException(
                    "This Option has no element, get() cannot be called on it.  The reason it has no element: "
                            + reason );
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        public Iterator<T> iterator()
        {
            return Collections.<T> emptyList().iterator();
        }

        @Override
        public <U> Option<U> map( final Conversion<T, U> conversion )
        {
            CheckParameters.areNotNull( conversion );

            return Option.getEmptyOption( reason );
        }

        @Override
        public String reason()
        {
            return reason;
        }

        @Override
        <U> U fold( final U ifEmpty, final Conversion<T, U> ifFull )
        {
            CheckParameters.areNotNull( ifEmpty, ifFull );

            return ifEmpty;
        }
    }

    /**
     * Creates an Option holding no elements.
     * 
     * @param <T>
     *        the type of the Option.
     * @param reason
     *        the reason that the Option has no elements.
     * @return an Option holding no elements.
     */
    public static <T> Option<T> getEmptyOption( final String reason )
    {
        CheckParameters.areNotNull( reason );

        return new Empty<T>( reason );
    }

    /**
     * A Conversion that always yields an empty Option.
     * 
     * @param <T>
     *        the type of the ignored value to convert.
     * @param <U>
     *        the type of the Option to produce.
     * @param reason
     *        the reason that the Option has no element.
     * @return a Conversion that always yields an empty Option.
     */
    public static <T, U> Conversion<T, Option<U>> getConversionToEmptyOption( final String reason )
    {
        CheckParameters.areNotNull( reason );

        return new Conversion<T, Option<U>>()
        {
            @Override
            public Option<U> convert( final T t )
            {
                return getEmptyOption( reason );
            }
        };
    }

    /**
     * A Conversion that yields an Option containing the value given to it.
     * 
     * @param <T>
     *        the type of the values that this Conversion takes.
     * @return a Conversion that yields an Option containing the value given to it.
     */
    public static <T> Conversion<T, Option<T>> getConversionToFullOption()
    {
        return new Conversion<T, Option<T>>()
        {
            @Override
            public Option<T> convert( final T t )
            {
                return getFullOption( t );
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
    public static <T> Option<T> getFullOption( final T t )
    {
        CheckParameters.areNotNull( t );

        return new Full<T>( t );
    }

    /**
     * A Conversion that takes in a T, converts it to a U, and produces an Option
     * containing that U.
     * 
     * @param <T>
     *        the type of the value to take in.
     * @param <U>
     *        the type of the Option to produce.
     * @param conversion
     *        the Conversion to convert the T into a U.
     * @return a Conversion that takes in a T, converts it to a U, and produces
     *         an Option containing that U.
     */
    public static <T, U> Conversion<T, Option<U>> toPartialConversion(
            final Conversion<T, U> conversion )
    {
        CheckParameters.areNotNull( conversion );

        return new Conversion<T, Option<U>>()
        {
            @Override
            public Option<U> convert( final T t )
            {
                return getFullOption( conversion.convert( t ) );
            }
        };
    }

    private Option()
    {
    }

    /**
     * Throws an UnsupportedOperationException to catch any accidental
     * badly-typed equals comparisons early.
     *
     * @throws an UnsupportedOperationException in all cases.
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
     * Throws an UnsupportedOperationException to be consistent with equals.
     *
     * @throws an UnsupportedOperationException in all cases.
     */
    @Override
    public final int hashCode()
    {
        throw new UnsupportedOperationException(
                "hashCode not supported, to match equals." );
    }

    /**
     * Identifies whether the Option is empty or has an element.
     * 
     * @return true if the Option has no element, false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * Maps the specified Conversion over this Option. If the Option has an element,
     * then a new Option will be produced containing the value produced by running
     * the Conversion on the value held by this Option. If the Option is empty,
     * an empty Option will be returned.
     * 
     * @param <U>
     *        the type to convert to.
     * @param conversion
     *        the Conversion to map over this Option.
     * @return an Option containing the mapped value, or nothing.
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
     *
     * @throws an UnsupportedOperationException in all cases.
     */
    @Override
    public final String toString()
    {
        throw new UnsupportedOperationException(
                "toString() not supported, to catch any accidental toString() calls early" );
    }

    /**
     * Applies the specified Conversion to a value held by this Option.
     * Specifically, if this Option is empty, the Conversion is not invoked - an
     * empty Option is returned. If this Option is full, the Conversion is invoked,
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
     * @param ifEmpty
     *        the U to return if the Option holds no value.
     * @param ifFull
     *        the Conversion to apply if the Option has a value.
     * @return a folded version of this Option according to the specified
     *         parameters.
     */
    abstract <U> U fold( U ifEmpty, Conversion<T, U> ifFull );
}
