package uk.org.netvu.core.cgi.vparts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.org.netvu.core.cgi.common.Format;

/**
 * Unit tests for VPartsCGI.
 */
public class VPartsCGITest
{
    /**
     * Tests that a built VPartsCGI contains the values given to it.
     */
    @Test
    public void retention()
    {
        assertTrue( new VPartsCGI.Builder().expiry( 5 ).build().getExpiry() == 5 );
        assertTrue( new VPartsCGI.Builder().mode( Mode.PROTECT ).build().getMode() == Mode.PROTECT );
        assertTrue( new VPartsCGI.Builder().time( 50 ).build().getTime() == 50 );
        assertTrue( new VPartsCGI.Builder().range( 100 ).build().getRange() == 100 );
        assertTrue( new VPartsCGI.Builder().watermark( true ).build().getWatermark() );
        assertTrue( new VPartsCGI.Builder().watermarkStep( 12 ).build().getWatermarkStep() == 12 );
        assertTrue( new VPartsCGI.Builder().format( Format.JS ).build().getFormat() == Format.JS );
        assertTrue( new VPartsCGI.Builder().listlength( 101 ).build().getListlength() == 101 );
        assertTrue( new VPartsCGI.Builder().pathstyle( DirectoryPathFormat.LONG ).build().getPathstyle() == DirectoryPathFormat.LONG );
    }

    /**
     * Tests that setting the same value twice causes an IllegalStateException.
     */
    @Test( expected = IllegalStateException.class )
    public void repeating()
    {
        new VPartsCGI.Builder().expiry( 5 ).expiry( 5 );
    }

    /**
     * Tests that setting the watermark step to an invalid value causes an
     * IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void outOfBounds()
    {
        new VPartsCGI.Builder().watermarkStep( 1000 );
    }

    /**
     * Tests that setting the time to a negative value causes an
     * IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void negativeTime()
    {
        new VPartsCGI.Builder().time( -10 );
    }

    /**
     * Tests that setting the format to HTML causes an IllegalArgumentException.
     */
    @Test( expected = IllegalArgumentException.class )
    public void formatHTML()
    {
        new VPartsCGI.Builder().format( Format.HTML ).build();
    }

    /**
     * Tests that parsing a URL then generating a URL from the resulting object
     * results in the same URL.
     */
    @Test
    public void fromString()
    {
        final String url = "/vparts.cgi?format=csv&mode=protect&time=958038820&range=120&pathstyle=long";

        assertTrue( VPartsCGI.fromString( url ).toString().equals( url ) );
    }
}
