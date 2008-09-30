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
    public void validFind()
    {
        assertTrue( EventsCGIResult.AlarmType.find( 4 ).value == 4 );
    }

    /**
     * Tests that a combination of two bits causes an IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void mixedFind()
    {
        EventsCGIResult.AlarmType.find( 3 );
    }

    /**
     * Tests that negative arguments to find cause an IllegalArgumentException..
     */
    @Test( expected = IllegalArgumentException.class )
    public void negativeFind()
    {
        EventsCGIResult.AlarmType.find( -1 );
    }

    /**
     * Tests that larger powers of 2 than there are constants for cause an
     * IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void largeFind()
    {
        EventsCGIResult.AlarmType.find( 256 );
    }
}
