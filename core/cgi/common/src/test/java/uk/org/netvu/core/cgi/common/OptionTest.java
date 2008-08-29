package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptionTest
{
    @Test
    public void getOrElse()
    {
        assertTrue( new Option.Some<Integer>( 5 ).getOrElse( 0 ) == 5 );
        assertTrue( new Option.None<Integer>().getOrElse( 0 ) == 0 );
    }

    @Test
    public void isNone()
    {
        assertFalse( new Option.Some<Integer>( 5 ).isNone() );
        assertTrue( new Option.None<Integer>().isNone() );
    }

    @Test
    public void get()
    {
        assertTrue( new Option.Some<Integer>( 5 ).get() == 5 );
    }

    @Test(expected = IllegalStateException.class)
    public void noGet()
    {
        new Option.None<Integer>().get();
    }
}
