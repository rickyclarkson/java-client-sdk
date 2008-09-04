package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A set of utility methods for operating on Iterable objects.
 */
public class Iterables
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

    public static <T, R> Iterable<R> map( final Iterable<T> iterable,
            final Conversion<T, R> conversion )
    {
        return new Iterable<R>()
        {

            public Iterator<R> iterator()
            {
                return map( iterable.iterator(), conversion );
            }
        };
    }

    public static <T, R> List<R> map( final List<T> list,
            final Conversion<T, R> conversion )
    {
        final List<R> result = new ArrayList<R>();
        final Iterable<T> iterable = list;
        for ( final R r : map( iterable, conversion ) )
        {
            result.add( r );
        }

        return result;
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

    public static <T> List<T> retainAll( final List<T> original,
            final Collection<T> others )
    {
        final List<T> newOne = new ArrayList<T>( original );
        newOne.retainAll( others );
        return newOne;
    }

    public static <T> List<T> remove( final List<T> original, final T toRemove )
    {
        final List<T> newOne = new ArrayList<T>( original );
        newOne.remove( toRemove );
        return newOne;
    }

    public static <T> T reduceLeft( final Iterable<T> ts,
            final Reduction<T, T> reduction )
    {
        final Iterator<T> iterator = ts.iterator();
        T accumulator = iterator.next();

        while ( iterator.hasNext() )
        {
            accumulator = reduction.reduce( iterator.next(), accumulator );
        }

        return accumulator;
    }

    public static <T, R> List<R> map( final Conversion<T, R> conversion,
            final T... ts )
    {
        return map( Arrays.asList( ts ), conversion );
    }

    public static <T, U> Iterable<Pair<T, U>> zip( final Iterable<T> ts,
            final Iterable<U> us )
    {
        return new Iterable<Pair<T, U>>()
        {
            public Iterator<Pair<T, U>> iterator()
            {
                final Iterator<T> tIterator = ts.iterator();
                final Iterator<U> uIterator = us.iterator();

                return new Iterator<Pair<T, U>>()
                {
                    public boolean hasNext()
                    {
                        return tIterator.hasNext() && uIterator.hasNext();
                    }

                    public Pair<T, U> next()
                    {
                        return Pair.pair( tIterator.next(), uIterator.next() );
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T> List<T> removeIndices( final List<T> values,
            final int... indices )
    {
        final List<T> results = new ArrayList<T>( values );

        int offset = 0;

        for ( final int index : indices )
        {
            results.remove( index - offset++ );
        }

        return results;
    }
}
