package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A set of utility methods for operating on Iterable objects. For internal use
 * only.
 */
public final class Iterables
{
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

        for ( final T t : list )
        {
            result.add( conversion.convert( t ) );
        }

        return result;
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
    public static <T> T reduce( final List<T> ts,
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

    public static <T, U> List<Pair<T, U>> zip( final List<T> ts,
            final List<U> us )
    {
        final Iterator<T> tIterator = ts.iterator();
        final Iterator<U> uIterator = us.iterator();

        final List<Pair<T, U>> results = new ArrayList<Pair<T, U>>();

        while ( tIterator.hasNext() && uIterator.hasNext() )
        {
            results.add( Pair.pair( tIterator.next(), uIterator.next() ) );
        }

        return results;
    }

    public static <T> List<Pair<T, Integer>> zipWithIndex( final List<T> ts )
    {
        final List<Pair<T, Integer>> results = new ArrayList<Pair<T, Integer>>();

        int a = 0;

        for ( final T t : ts )
        {
            results.add( Pair.pair( t, a++ ) );
        }

        return results;
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

    public static <T> List<T> filter( final List<T> list,
            final Conversion<T, Boolean> conversion )
    {
        final List<T> results = new ArrayList<T>();
        for ( final T t : list )
        {
            if ( conversion.convert( t ) )
            {
                results.add( t );
            }
        }
        return results;
    }
}
