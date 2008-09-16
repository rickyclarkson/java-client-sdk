package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Pair.
 */
public class PairTest
{
    /**
     * Tests that constructing a Pair then retrieving first() returns the first
     * value supplied when constructing the Pair.
     */
    @Test
    public void first()
    {
        assertTrue( Pair.pair( 3, 4 ).first() == 3 );
    }

    /**
     * Tests that constructing a Pair then retrieving second() returns the
     * second value supplied when constructing the Pair.
     */
    @Test
    public void second()
    {
        assertTrue( Pair.pair( 3, 4 ).second() == 4 );
    }
}
