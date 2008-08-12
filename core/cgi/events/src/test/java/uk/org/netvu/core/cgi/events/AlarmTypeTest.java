package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for AlarmType.find.
 */
public class AlarmTypeTest
{
    /**
     * Tests that a valid input to AlarmType.find completes successfully.
     */
    @Test
    public void testValidFind()
    {
        assertTrue( AlarmType.find( 4 ).value == 4 );
    }

    /**
     * Tests that a combination of two bits fails with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMixedFind()
    {
        AlarmType.find( 3 );
    }

    /**
     * Tests that negative arguments to find fail with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeFind()
    {
        AlarmType.find( -1 );
    }

    /**
     * Tests that larger powers of 2 than there are constants for fail with an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLargeFind()
    {
        AlarmType.find( 256 );
    }
}
