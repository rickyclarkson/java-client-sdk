package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for EqualsBuilder
 */
public class EqualsBuilderTest
{
    /**
     * Tests that an EqualsBuilder with only equal elements is equal().
     */
    @Test
    public void testSuccess()
    {
        assertTrue( new EqualsBuilder().with( 1, 1, "one" ).with( "hello",
                "hello", "hello" ).equal() );
    }

    /**
     * Tests that an EqualsBuilder with 1 failure has some rejections.
     */
    @Test
    public void testSingleFailure()
    {
        assertFalse( new EqualsBuilder().with( 1, 2, "one" ).rejections().length() == 0 );
    }

    /**
     * Tests that an EqualsBuilder with many failures has many rejections.
     */
    @Test
    public void testManyFailures()
    {
        assertTrue( new EqualsBuilder().with( 1, 1, "one" ).with( 1, 2, "1" ).with(
                2, 3, "2" ).rejections().contains( "\n" ) );
    }
}
