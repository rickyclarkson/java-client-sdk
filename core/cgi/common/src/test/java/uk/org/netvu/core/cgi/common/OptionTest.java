package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Option.
 */
public class OptionTest
{
    /**
     * Tests that Option.getOrElse works as specified.
     */
    @Test
    public void getOrElse()
    {
        assertTrue( Option.some( 5 ).getOrElse( 0 ) == 5 );
        assertTrue( Option.<Integer> none().getOrElse( 0 ) == 0 );
    }

    /**
     * Tests that Option.isNone works as specified.
     */
    @Test
    public void isNone()
    {
        assertFalse( Option.some( 5 ).isNone() );
        assertTrue( Option.none().isNone() );
    }

    /**
     * Tests that Option.get works for an Option.Some.
     */
    @Test
    public void get()
    {
        assertTrue( Option.some( 5 ).get() == 5 );
    }

    /**
     * Tests that Option.get throws an IllegalStateException for an Option.None.
     */
    @Test(expected = IllegalStateException.class)
    public void noGet()
    {
        Option.none().get();
    }
}
