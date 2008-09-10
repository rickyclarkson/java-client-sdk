package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A set of utility methods for operating on Iterable objects. For internal use
 * only.
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

    /**
     * Tests whether two Iterables are equal in elements.
     * 
     * @param <T>
     *        the type of the Iterables.
     * @param one
     *        the first Iterable.
     * @param two
     *        the second Iterable.
     * @return true if the two Iterables are equal in elements, false otherwise.
     */
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

    /**
     * Creates an Iterable by passing all items from the specified Iterable
     * through the specified Conversion.
     * 
     * @param <T>
     *        the type of the specified Iterable.
     * @param <R>
     *        the type of the returned Iterable.
     * @param iterable
     *        the iterable to map over.
     * @param conversion
     *        the conversion to map through.
     * @return an Iterable containing objects of type R.
     */
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

    /**
     * Creates a List by passing all items from the specified List through the
     * specified Conversion.
     * 
     * @param <T>
     *        the type of the specified List.
     * @param <R>
     *        the type of the returned List.
     * @param list
     *        the List to map over.
     * @param conversion
     *        the conversion to map through.
     * @return a List containing objects of type R.
     */
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

    /**
     * Creates an Iterator by passing all items from the specified Iterator
     * through the specified Conversion.
     * 
     * @param <T>
     *        the type of the specified Iterator.
     * @param <R>
     *        the type of the returned Iterator.
     * @param iterator
     *        the Iterator to map over.
     * @param conversion
     *        the conversion to map through.
     * @return an Iterator containing objects of type R.
     */
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

    /**
     * Constructs a new list containing the elements from the specified list,
     * except for the specified element toRemove. The original list is not
     * modified.
     * 
     * @param <T>
     *        the type of the list.
     * @param original
     *        the list to remove elements from.
     * @param toRemove
     *        the element to remove.
     * @return a new list containing the elements from the specified list,
     *         except for the specified element toRemove.
     */
    public static <T> List<T> remove( final List<T> original, final T toRemove )
    {
        final List<T> newOne = new ArrayList<T>( original );
        newOne.remove( toRemove );
        return newOne;
    }

    /**
     * Applies a Reduction across an Iterable, returning a single value. Given
     * an Iterable containing ints, and a Reduction that adds its two arguments
     * together, the result is a sum of those ints.
     * 
     * @param <T>
     *        the type of the iterable.
     * @param ts
     *        the Iterable to reduce over.
     * @param reduction
     *        the operation to carry out between each pair of elements.
     * @return the value produced by reducing the Iterable with the specified
     *         Reduction.
     */
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

    /**
     * Zips two iterables, giving a single iterable of pairs, where the first of
     * each pair comes from the first specified iterable, and the second of each
     * pair comes from the second specified iterable.
     * 
     * @param <T>
     *        the type of the first Iterable.
     * @param <U>
     *        the type of the second Iterable.
     * @param ts
     *        the first Iterable to zip.
     * @param us
     *        the second Iterable to zip.
     * @return an iterable of pairs of T and U.
     */
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

    /**
     * Gives a list containing all the elements from the specified List, but
     * without the items at the specified indices. The original List is not
     * modified.
     * 
     * @param <T>
     *        the type of the List.
     * @param values
     *        the List to copy values from.
     * @param indices
     *        the indices to remove.
     * @return a List containing all the elements from the specified List, but
     *         without the items at the specified indices.
     */
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

    /**
     * Returns an infinite series of consecutive ints, beginning with the
     * specified int.
     * 
     * @param start
     *        the int to begin counting upwards from.
     * @return an infinite series of consecutive ints.
     */
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

    /**
     * Gives a List containing the elements from the specified Iterable that the
     * specified Conversion returns true for. The original Iterable is not
     * modified.
     * 
     * @param <T>
     *        the type of the Iterable.
     * @param iterable
     *        the Iterable to filter.
     * @param predicate
     *        the predicate to filter with.
     * @return a List containing the elements from the specified Iterable that
     *         the specified Conversion returns true for.
     */
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

    /**
     * Gives an Iterable containing the second element of all the Pairs of an
     * Iterable containing Pairs.
     * 
     * @param <T>
     *        the type of the first element of the Pairs.
     * @param <U>
     *        the type of the second element of the Pairs.
     * @param iterable
     *        the Iterable of interest.
     * @return an Iterable containing the second element of all the Pairs of an
     *         Iterable containing Pairs.
     */
    public static <T, U> Iterable<U> second( final Iterable<Pair<T, U>> iterable )
    {
        return map( iterable, Pair.<T, U> getSecond() );
    }
}
