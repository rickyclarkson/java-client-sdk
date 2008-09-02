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

    @Test
    public void fromURL()
    {
        final String string = "decoder.frm?connections[16]=\"slaveip=192.168.1.10%2Ccam=1\","
                + "\"slaveip=192.168.1.10%2Ccam=2\","
                + "\"slaveip=192.168.1.10%2Ccam=3\","
                + "\"slaveip=192.168.1.10%2Ccam=4\""
                + "&layouts[1]=\"Four%20Way\""
                + "&commands[1]=\"display_pic.cgi?\"";

        final DecoderCGI cgi = DecoderCGI.fromString( string );
        assertTrue( cgi.getConnections().size() == 4 );
        for ( int a = 0; a < 4; a++ )
        {
            assertTrue( cgi.getConnections().get( a + 16 ).getSlaveIP().equals(
                    "192.168.1.10" ) );
            assertTrue( cgi.getConnections().get( a + 16 ).getCam() == a + 1 );
        }

        assertTrue( cgi.getLayouts().size() == 1 );
        assertTrue( cgi.getLayouts().get( 1 ) == Layout.FOUR_WAY );
        assertTrue( cgi.getCommands().size() == 1 );
        assertTrue( cgi.getCommands().get( 1 ).equals( "display_pic.cgi?" ) );
    }
}
