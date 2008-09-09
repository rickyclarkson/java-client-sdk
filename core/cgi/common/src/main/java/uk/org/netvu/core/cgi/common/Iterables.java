package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
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
     * @param <T>
     *        The type of the iterables to prepend to each other.
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
     * 
     * @param iterable
     *        the Iterable to count the elements of.
     * @return the number of elements in the iterable.
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

    public static Iterable<Integer> from( final int start )
    {
        return new Iterable<Integer>()
        {

            public Iterator<Integer> iterator()
            {
                return new Iterator<Integer>()
                {
                    int x = start;

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }

                    public Integer next()
                    {
                        return x++;
                    }

                    public boolean hasNext()
                    {
                        return true;
                    }
                };
            }
        };
    }

    public static <T> List<T> filter( final Iterable<T> iterable,
            final Conversion<T, Boolean> predicate )
    {
        final List<T> results = new ArrayList<T>();
        for ( final T t : iterable )
        {
            if ( predicate.convert( t ) )
            {
                results.add( t );
            }
        }

        return results;
    }

    public static <T, U> Iterable<U> second( final Iterable<Pair<T, U>> iterable )
    {
        return map( iterable, Pair.<T, U> getSecond() );
    }
}
