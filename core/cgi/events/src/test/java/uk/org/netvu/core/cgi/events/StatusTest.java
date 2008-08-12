package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for Status.find.
 */
public class StatusTest
{
    /**
     * Tests that a valid input to AlarmType.find completes successfully.
     */
    @Test
    public void testValidFind()
    {
        assertTrue( Status.find( 4 ).value == 4 );
    }

    /**
     * Tests that a combination of two bits fails with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMixedFind()
    {
        Status.find( 3 );
    }

    /**
     * Tests that negative arguments to find fail with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeFind()
    {
        Status.find( -1 );
    }

    /**
     * Tests that larger powers of 2 than there are constants for fail with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLargeFind()
    {
        Status.find( 256 );
    }
}
