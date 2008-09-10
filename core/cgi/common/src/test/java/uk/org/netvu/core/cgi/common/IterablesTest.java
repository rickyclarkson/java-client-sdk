package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Test;

/**
 * Tests for the Iterables utility class.
 */
public class IterablesTest
{
    /**
     * Tests that prepending one Iterable to another yields an Iterable of equal
     * size to the sum of the sizes of the input Iterables.
     */
    @Test
    public void testPrepend()
    {
        final Random random = new Random();

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final List<Integer> one = Generators.intLists( random ).next();
            final List<Integer> two = Generators.intLists( random ).next();

            assertTrue( one.size() + two.size() == Iterables.size( Iterables.prepend(
                    one, two ) ) );
        }
    }

    /**
     * Tests that sequenceEqual follows its contract.
     */
    @Test
    public void testSequenceEqual()
    {
        assertTrue( Iterables.sequenceEqual( Collections.emptyList(),
                Collections.emptyList() ) );
        assertTrue( Iterables.sequenceEqual( Collections.singleton( "test" ),
                Collections.singleton( "test" ) ) );
        assertTrue( Iterables.sequenceEqual( Arrays.asList( 1, 2, 3, 4 ),
                Arrays.asList( 1, 2, 3, 4 ) ) );
        assertFalse( Iterables.sequenceEqual( Arrays.asList( 1, 2, 3, 4 ),
                Arrays.asList( 1, 2, 3, 5 ) ) );
        assertFalse( Iterables.sequenceEqual( Arrays.asList( 1, 2, 3 ),
                Arrays.asList( 1, 2, 3, 4 ) ) );
        assertFalse( Iterables.sequenceEqual( Arrays.asList( 1, 2, 3, 4 ),
                Arrays.asList( 1, 2, 3 ) ) );
    }

    /**
     * Tests that the Iterable returned by prepend follows the contract of
     * Iterable.
     */
    @Test
    public void testContractOfPrepend()
    {
        final Iterator<List<Integer>> gen = Generators.intLists( new Random() );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertTrue( testContract( Iterables.prepend( gen.next(), gen.next() ) ) );
            try
            {
                Iterables.prepend( gen.next(), gen.next() ).iterator().remove();
                fail();
            }
            catch ( final UnsupportedOperationException e )
            {

            }
        }
    }

    /**
     * Tests that the Iterable returned by Iterables.map follows the Iterable
     * contract.
     */
    @Test
    public void mapContract()
    {
        assertTrue( testContract( Iterables.map( Arrays.asList( 1, 2, 3 ),
                new Conversion<Integer, Integer>()
                {
                    @Override
                    public Integer convert( final Integer i )
                    {
                        return i * 2;
                    }
                } ) ) );
    }

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
        assertTrue( Iterables.reduceLeft( Arrays.asList( 1, 2, 3 ),
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
        final Iterable<Pair<Integer, Integer>> zipped = Iterables.zip(
                Arrays.asList( 1, 2, 3 ), Arrays.asList( 4, 5, 6 ) );

        assertTrue( Iterables.reduceLeft( Iterables.map( zipped,
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

        assertTrue( testContract( zipped ) );

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
