package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * Tests for the Iterables utility class.
 */
public class IterablesTest
{
    /**
     * Tests that the Iterable parameter follows the contract of Iterable.
     * 
     * @param <T>
     *        The type parameter of the Iterable.
     * @param iterable
     *        the Iterable.
     * @return true if the Iterable follows the contracts of Iterable, false
     *         otherwise.
     */
    public static <T> boolean testContract( final Iterable<T> iterable )
    {
        if ( iterable.iterator() == null
                || iterable.iterator() == iterable.iterator() )
        {
            return false;
        }

        final Iterator<T> iterator = iterable.iterator();

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            if ( iterator.hasNext() != iterator.hasNext() )
            {
                return false;
            }

            if ( iterator.hasNext() )
            {
                iterator.next();
            }
            else
            {
                try
                {
                    iterator.next();
                    return false;
                }
                catch ( final NoSuchElementException exception )
                {

                }
            }
        }

        return true;
    }

    /**
     * Tests that Iterables.remove works as specified.
     */
    @Test
    public void remove()
    {
        assertTrue( Iterables.remove( Arrays.asList( 1, 2, 3 ), 3 ).equals(
                Arrays.asList( 1, 2 ) ) );
    }

    /**
     * Tests that Iterables.reduceLeft works as expected.
     */
    @Test
    public void reduceLeft()
    {
        assertTrue( Iterables.reduce( Arrays.asList( 1, 2, 3 ),
                new Reduction<Integer, Integer>()
                {
                    @Override
                    public Integer reduce( final Integer newValue,
                            final Integer original )
                    {
                        return original + newValue;
                    }
                } ) == 6 );
    }

    /**
     * Tests that Iterables.zip works as specified.
     */
    @Test
    public void zip()
    {
        final List<Pair<Integer, Integer>> zipped = Iterables.zip(
                Arrays.asList( 1, 2, 3 ), Arrays.asList( 4, 5, 6 ) );

        assertTrue( Iterables.reduce( Iterables.map( zipped,
                new Conversion<Pair<Integer, Integer>, Integer>()
                {
                    @Override
                    public Integer convert( final Pair<Integer, Integer> t )
                    {
                        return t.first() * t.second();
                    }
                } ), new Reduction<Integer, Integer>()
        {
            @Override
            public Integer reduce( final Integer newValue,
                    final Integer original )
            {
                return original + newValue;
            }
        } ) == 1 * 4 + 2 * 5 + 3 * 6 );
    }

    /**
     * Tests that Iterables.removeIndices works as expected.
     */
    @Test
    public void removeIndices()
    {
        assertTrue( Iterables.removeIndices( Arrays.asList( 0, 1, 2, 3, 4 ), 1,
                3 ).equals( Arrays.asList( 0, 2, 4 ) ) );
    }
}
