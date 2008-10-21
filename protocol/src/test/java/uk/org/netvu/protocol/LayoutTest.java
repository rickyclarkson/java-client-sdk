package uk.org.netvu.protocol;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Layout.
 */
public class LayoutTest
{
    /**
     * Tests that parsing a Layout from its representation in the DecoderCGI URL
     * works as expected.
     */
    @Test
    public void fromURL()
    {
        for ( final DecoderCGI.Layout layout : DecoderCGI.Layout.values() )
        {
            assertTrue( DecoderCGI.Layout.fromURL.apply( String.valueOf( layout.value ) ) == layout );
        }
    }

    /**
     * Tests that invalid values throw an IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void invalidURL()
    {
        DecoderCGI.Layout.fromURL.apply( "10" );
    }
}
