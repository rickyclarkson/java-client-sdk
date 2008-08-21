package uk.org.netvu.core.cgi.events;

import uk.org.netvu.core.cgi.common.MaxOnce;
import junit.framework.TestCase;

/**
 * A set of tests for the MaxOnce generic wrapper class.
 */
public class MaxOnceTest extends TestCase
{
    /**
     * A MaxOnce that has not been set to a value should yield its default value
     * when get() is called. isUnset() should return normally.
     */
    public void testMaxOnceUnderflow()
    {
        assertEquals( new MaxOnce<String>( "hello", "ignored" ).get(), "hello" );
        new MaxOnce<String>( "hello", "ignored" ).isUnset();
    }

    /**
     * A MaxOnce that has been set to a value should yield that value when get()
     * is called. isUnset() should throw an IllegalStateException.
     */
    public void testMaxOnceSetOnce()
    {
        MaxOnce<String> once = new MaxOnce<String>( "hello", "ignored" );
        once.set( "goodbye" );
        assertEquals( once.get(), "goodbye" );
        try
        {
            once.isUnset();
            fail();
        }
        catch ( IllegalStateException e )
        {
            assertTrue( e.getMessage().contains( "ignored" ) );
        }
    }

    /**
     * A MaxOnce that has been set to a value once should throw an
     * IllegalStateException when an attempt is made to set it for a second
     * time.
     */
    public void testMaxOnceOverflow()
    {
        MaxOnce<String> once = new MaxOnce<String>( "hello", "ignored" );
        once.set( "goodbye" );
        try
        {
            once.set( "again" );
            fail();
        }
        catch ( IllegalStateException e )
        {
            assertTrue( e.getMessage().contains( "ignored" ) );
        }
    }
}
