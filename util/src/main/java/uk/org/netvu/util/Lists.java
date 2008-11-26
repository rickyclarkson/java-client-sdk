package uk.org.netvu.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * A set of utility methods for operating on Lists.
 */
public final class Lists
{
    /**
     * Given a List and a predicate, constructs a List consisting of only the
     * elements of that List that pass the predicate.
     * 
     * @param <T>
     *        the type of the elements of the List.
     * @param list
     *        the List to filter.
     * @param predicate
     *        the predicate to filter by.
     * @return a List consisting of only the elements of the passed-in List that
     *         pass the predicate.
     */
    public static <T> List<T> filter( final List<T> list, final Function<T, Boolean> predicate )
    {
        CheckParameters.areNotNull( predicate );

        final List<T> results = new ArrayList<T>();
        for ( final T t : list )
        {
            if ( predicate.apply( t ) )
            {
                results.add( t );
            }
        }
        return results;
    }

    /**
     * Creates a List by passing all items from the specified List through the
     * specified Function.
     * 
     * @param <T>
     *        the type of the specified List.
     * @param <R>
     *        the type of the returned List.
     * @param list
     *        the List to map over.
     * @param function
     *        the function to map over the List.
     * @return a List containing objects of type R.
     */
    public static <T, R> List<R> map( final List<T> list, final Function<T, R> function )
    {
        CheckParameters.areNotNull( function );
        final List<R> result = new ArrayList<R>();

        for ( final T t : list )
        {
            result.add( function.apply( t ) );
        }

        return result;
    }

    /**
     * Applies a Reduction across a List, returning a single value. For example,
     * given a List containing ints, and a Reduction that adds its two arguments
     * together, the result is a sum of those ints.
     * 
     * @param <T>
     *        the type of the elements of the List.
     * @param list
     *        the List to reduce.
     * @param reduction
     *        the operation to carry out between each pair of elements.
     * @return the value produced by reducing the List with the specified
     *         Reduction.
     */
    public static <T> T reduce( final List<T> list, final Reduction<T, T> reduction )
    {
        CheckParameters.areNotNull( list, reduction );

        final Iterator<T> iterator = list.iterator();
        T accumulator = iterator.next();

        while ( iterator.hasNext() )
        {
            accumulator = reduction.reduce( iterator.next(), accumulator );
        }

        return accumulator;
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
    public static <T> List<T> remove( final List<T> original, final T... toRemove )
    {
        final List<T> newOne = new ArrayList<T>( original );

        for ( final T t : toRemove )
        {
            newOne.remove( t );
        }

        return newOne;
    }

    /**
     * Gives a list containing all the elements from the specified List, but
     * without the items at the specified indices. The original List is not
     * modified.
     * 
     * @param <T>
     *        the type of the elements of the List.
     * @param values
     *        the List to copy values from.
     * @param indices
     *        the indices to remove.
     * @return a List containing all the elements from the specified List, but
     *         without the items at the specified indices.
     */
    public static <T> List<T> removeByIndices( final List<T> values, final int... indices )
    {
        final TreeSet<Integer> uniqueIndices = new TreeSet<Integer>();
        for ( final int index : indices )
        {
            uniqueIndices.add( index );
        }

        final List<T> results = new ArrayList<T>( values );

        int offset = 0;

        for ( final Integer index : uniqueIndices )
        {
            results.remove( index - offset++ );
        }

        return results;
    }

    /**
     * Zips two Lists together, giving a new List containing Pairs each
     * consisting of one element from each List, in order.
     * 
     * @param <T>
     *        the type of the elements of the first List.
     * @param <U>
     *        the type of the elements of the second List.
     * @param ts
     *        the first List to zip.
     * @param us
     *        the second List to zip.
     * @return a new List containing Paris each consisting of one element from
     *         each List, in order.
     */
    public static <T, U> List<Pair<T, U>> zip( final List<T> ts, final List<U> us )
    {
        final Iterator<T> tIterator = ts.iterator();
        final Iterator<U> uIterator = us.iterator();

        final List<Pair<T, U>> results = new ArrayList<Pair<T, U>>();

        while ( tIterator.hasNext() && uIterator.hasNext() )
        {
            results.add( new Pair<T, U>( tIterator.next(), uIterator.next() ) );
        }

        return results;
    }

    /**
     * This is private to prevent instantiation.
     */
    private Lists()
    {
    }
}