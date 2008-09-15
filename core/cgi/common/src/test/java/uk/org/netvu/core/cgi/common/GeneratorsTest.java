package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

/**
 * Unit tests for Generators.
 */
public class GeneratorsTest
{
    /**
     * Tests that nonNegativeInts does actually produce non-negative ints.
     */
    @Test
    public void nonNegativeInts()
    {
        assertTrue( Generators.nonNegativeInts( new Random( 0 ) ).next() >= 0 );
    }
}
