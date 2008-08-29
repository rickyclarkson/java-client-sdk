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

import uk.org.netvu.core.cgi.common.Generator;
import uk.org.netvu.core.cgi.common.Generators;
import uk.org.netvu.core.cgi.common.Iterables;

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
     * Tests that a one-shot iterable calls its function once when you create
     * the iterator.
     */
    @Test
    public void testOneShotWithNoCalls()
    {
        final int[] count = { 0 };
        Iterables.oneShotIterableFromSideEffectingFunction(
                new Generator<String>()
                {
                    public String invoke()
                    {
                        count[0]++;
                        return "test";
                    }
                } ).iterator();
        assertTrue( count[0] == 1 );
    }

    /**
     * Tests that a one-shot iterable calls its function twice when you request
     * the first element.
     */
    @Test
    public void testOneShotWithOneCall()
    {
        final int[] count = { 0 };
        Iterables.oneShotIterableFromSideEffectingFunction(
                new Generator<String>()
                {

                    public String invoke()
                    {
                        count[0]++;
                        return "test";
                    }
                } ).iterator().next();

        assertTrue( count[0] == 2 );
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
     * Tests that a one-shot iterable doesn't support removal.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testOneShotIteratorRemove()
    {
        Iterables.oneShotIterableFromSideEffectingFunction(
                new Generator<Integer>()
                {

                    public Integer invoke()
                    {
                        return 5;
                    }

                } ).iterator().remove();
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
     * Tests that the Iterable parameter follows the contract of Iterable.
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
}
