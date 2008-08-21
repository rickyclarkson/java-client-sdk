package uk.org.netvu.core.cgi.vparts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.org.netvu.core.cgi.common.Format;

public class VPartsCGITest
{
    @Test
    public void testRetention()
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

    @Test(expected = IllegalArgumentException.class)
    public void testRepeating()
    {
        new VPartsCGI.Builder().expiry( 5 ).expiry( 5 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfBounds()
    {
        new VPartsCGI.Builder().watermarkStep( 1000 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTime()
    {
        new VPartsCGI.Builder().time( -10 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatHTML()
    {
        new VPartsCGI.Builder().format( Format.HTML ).build();
    }

    @Test
    public void fromString()
    {
        final String url = "/vparts.cgi?format=csv&mode=protect&time=958038820&range=120&pathstyle=long";
        assertTrue( VPartsCGI.fromString( url ).toString().equals( url ) );
    }
}
