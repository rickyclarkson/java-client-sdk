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
    public void validFind()
    {
        assertTrue( EventsCGIResult.Status.find( 4 ).get().value == 4 );
    }

    /**
     * Tests that a combination of two bits gives an Option.None.
     */
    @Test
    public void mixedFind()
    {
        assertTrue( EventsCGIResult.Status.find( 3 ).isNone() );
    }

    /**
     * Tests that negative arguments to find give an Option.None.
     */
    @Test
    public void negativeFind()
    {
        assertTrue( EventsCGIResult.Status.find( -1 ).isNone() );
    }

    /**
     * Tests that larger powers of 2 than there are constants for give an
     * Option.None.
     */
    @Test
    public void largeFind()
    {
        assertTrue( EventsCGIResult.Status.find( 256 ).isNone() );
    }
}
