package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Generators.
 */
public class GeneratorsTest
{
    /**
     * Tests that the Iterable returned by Generators.nonNegativeInts follows
     * the Iterable contract.
     */
    @Test
    public void nonNegativeInts()
    {
        assertTrue( IterablesTest.testContract( Generators.nonNegativeInts( 0 ) ) );
    }
}
