package uk.org.netvu.core.cgi.decoder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DecoderCGITest
{
    @Test
    public void retention()
    {
        final DecoderCGI cgi = new DecoderCGI().persistence(
                Persistence.PERSISTENT ).command( 1, "foo" ).command( 2, "bar" ).outputTitles(
                "one", "two", "three" ).layout( 1, Layout.FOUR_WAY ).layout( 2,
                Layout.NINE_WAY ).connection( 5,
                new Connection().audio( 4 ).seq( 0xF ).dwell( 40 ) );

        assertTrue( cgi.getCommands().size() == 2 );
        assertTrue( cgi.getCommands().get( 2 ).equals( "bar" ) );
        assertTrue( cgi.getOutputTitles()[2].equals( "three" ) );
        assertTrue( cgi.getLayouts().get( 2 ) == Layout.NINE_WAY );
    }
}
