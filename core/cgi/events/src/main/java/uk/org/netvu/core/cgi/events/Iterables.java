package uk.org.netvu.core.cgi.events;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A set of utility methods for operating on Iterable objects.
 */
class Iterables
{
    private Iterables()
    {
    }

    /**
     * Prepends one iterable to the next in constant time and memory.
     * 
     * @param one
     *        the first iterable.
     * @param two
     *        the second iterable.
     * @return a new Iterable who elements are those of 'one' followed by those
     *         of 'two'.
     */
    public static <T> Iterable<T> prepend( final Iterable<? extends T> one,
            final Iterable<? extends T> two )
    {
        return new Iterable<T>()
        {
            public Iterator<T> iterator()
            {
                final Iterator<? extends T> oneIt = one.iterator();
                final Iterator<? extends T> twoIt = two.iterator();

                return new Iterator<T>()
                {
                    boolean first = true;

                    public boolean hasNext()
                    {
                        if ( first )
                        {
                            if ( oneIt.hasNext() )
                            {
                                return true;
                            }
                            first = false;
                        }
                        return twoIt.hasNext();
                    }

                    public T next()
                    {
                        if ( hasNext() )
                        {
                            if ( first )
                            {
                                return oneIt.next();
                            }
                            return twoIt.next();
                        }
                        throw new NoSuchElementException();
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Counts the number of elements in an Iterable in linear time and constant
     * memory.
     */
    public static int size( final Iterable<?> iterable )
    {
        int count = 0;

        final Iterator<?> iterator = iterable.iterator();

        while ( iterator.hasNext() )
        {
            count++;
            iterator.next();
        }

        return count;
    }

    public static <T> boolean sequenceEqual( final Iterable<T> one,
            final Iterable<T> two )
    {
        final Iterator<T> oneIt = one.iterator();
        final Iterator<T> twoIt = two.iterator();

        while ( oneIt.hasNext() )
        {
            if ( !twoIt.hasNext() )
            {
                return false;
            }

            final T oneNext = oneIt.next();
            final T twoNext = twoIt.next();

            if ( !oneNext.equals( twoNext ) )
            {
                return false;
            }
        }

        return !twoIt.hasNext();
    }

    public static <T> Iterable<T> oneShotIterableFromSideEffectingFunction(
            final Generator<T> generator )
    {
        return new Iterable<T>()
        {
            public Iterator<T> iterator()
            {
                return new Iterator<T>()
                {
                    T current = generator.invoke();

                    public boolean hasNext()
                    {
                        return current != null;
                    }

                    public T next()
                    {
                        final T toReturn = current;
                        current = generator.invoke();
                        return toReturn;
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T, R> Iterator<R> map( final Iterator<T> iterator,
            final Conversion<T, R> conversion )
    {
        return new Iterator<R>()
        {
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            public R next()
            {
                return conversion.convert( iterator.next() );
            }

            public void remove()
            {
                iterator.remove();
            }
        };
    }
}
