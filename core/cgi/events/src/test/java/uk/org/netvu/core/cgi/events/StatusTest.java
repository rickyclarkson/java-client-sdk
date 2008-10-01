package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for Status.find.
 */
public class StatusTest
{
    /**
     * Tests that larger powers of 2 than there are constants for cause an
     * IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void largeFind()
    {
        EventsCGIResult.Status.find( 256 );
    }

    /**
     * Tests that a combination of two bits throws an IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void mixedFind()
    {
        EventsCGIResult.Status.find( 3 );
    }

    /**
     * Tests that negative arguments to find give an IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void negativeFind()
    {
        EventsCGIResult.Status.find( -1 );
    }

    /**
     * Tests that a valid input to AlarmType.find completes successfully.
     */
    @Test
    public void validFind()
    {
        assertTrue( EventsCGIResult.Status.find( 4 ).value == 4 );
    }
}
