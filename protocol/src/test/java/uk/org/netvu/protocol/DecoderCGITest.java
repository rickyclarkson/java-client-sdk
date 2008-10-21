package uk.org.netvu.protocol;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

/**
 * Tests for DecoderCGI.
 */
public class DecoderCGITest
{
    /**
     * Tests that a DecoderCGI with output titles is properly converted to URL
     * parameters.
     */
    @Test
    public void fromCGI()
    {
        assertTrue( new DecoderCGI().outputTitles( "foo", "bar", "baz" ).command( 2, "blah" ).layout( 1,
                Layout.FOUR_WAY ).toURLParameters().equals(
                "decoder.var?layouts[1]=1&output_titles=\"foo\",\"bar\",\"baz\"&commands[2]=%22blah%22" ) );
    }

    /**
     * Tests that example 1 from the Decoder Control Specification parses
     * correctly. Note that the layout was changed from "Four Way" to 1, as the
     * spec appears to be out of date with the server implementations.
     */
    @Test
    public void fromExampleURL1()
    {
        final String string =
                "decoder.frm?connections[16]=\"slaveip=192.168.1.10%2Ccam=1\"," + "\"slaveip=192.168.1.10%2Ccam=2\","
                        + "\"slaveip=192.168.1.10%2Ccam=3\"," + "\"slaveip=192.168.1.10%2Ccam=4\"" + "&layouts[1]=1"
                        + "&commands[1]=\"display_pic.cgi?\"";

        final DecoderCGI cgi = DecoderCGI.fromString( string );
        assertTrue( cgi.getConnections().size() == 4 );
        for ( int a = 0; a < 4; a++ )
        {
            assertTrue( cgi.getConnections().get( a + 16 ).getSlaveIP().equals( "192.168.1.10" ) );
            assertTrue( cgi.getConnections().get( a + 16 ).getCam() == a + 1 );
        }

        assertTrue( cgi.getLayouts().size() == 1 );
        assertTrue( cgi.getLayouts().get( 1 ) == Layout.FOUR_WAY );
        assertTrue( cgi.getCommands().size() == 1 );
        assertTrue( cgi.getCommands().get( 1 ).equals( "display_pic.cgi?" ) );
    }

    /**
     * Tests that example 2 from the Decoder Control Specification parses
     * correctly.
     */
    @Test
    public void fromExampleURL2()
    {
        final String string = "decoder.frm?connections[18]=\"slaveip=192.168.1.10%2Ccam=7\"";

        final DecoderCGI cgi = DecoderCGI.fromString( string );
        assertTrue( cgi.getConnections().size() == 1 );
        assertTrue( cgi.getConnections().get( 18 ).getSlaveIP().equals( "192.168.1.10" ) );
        assertTrue( cgi.getConnections().get( 18 ).getCam() == 7 );
    }

    /**
     * Tests that example 3 from the Decoder Control Specification parses
     * correctly.
     */
    @Test
    public void fromExampleURL3()
    {
        final String string =
                "decoder.var?commands[1]=\"replay_pic.cgi?id=123456&time=12:34:00:28:11:03&control=PLAY\"";

        final DecoderCGI cgi = DecoderCGI.fromString( string );
        assertTrue( cgi.getCommands().size() == 1 );
        assertTrue( cgi.getCommands().get( 1 ).equals( "replay_pic.cgi?id=123456&time=12:34:00:28:11:03&control=PLAY" ) );
    }

    /**
     * Tests that example 1 from the Decoder Control Specification parses
     * correctly. Note that the layout was changed from "Single" to 0, as the
     * spec appears to be out of date with the server implementations.
     */
    @Test
    public void fromExampleURL4()
    {
        final String string =
                "decoder.var?connections[64]=\"slaveip=192.168.1.10%2Cseq=F%2Cdwell=10\""
                        + "&commands[4]=\"display_pic.cgi?\"&layouts[4]=0";

        final DecoderCGI cgi = DecoderCGI.fromString( string );
        assertTrue( cgi.getConnections().size() == 1 );
        assertTrue( cgi.getConnections().get( 64 ).getSlaveIP().equals( "192.168.1.10" ) );
        assertTrue( cgi.getConnections().get( 64 ).getSeq() == 15 );
        assertTrue( cgi.getConnections().get( 64 ).getDwell() == 10 );

        assertTrue( cgi.getCommands().size() == 1 );
        assertTrue( cgi.getCommands().get( 4 ).equals( "display_pic.cgi?" ) );

        assertTrue( cgi.getLayouts().size() == 1 );
        assertTrue( cgi.getLayouts().get( 4 ) == Layout.SINGLE );
    }

    /**
     * Tests against an example format found in Graham Martin's code.
     * 
     * @throws UnsupportedEncodingException
     */
    @Test
    public void fromUnquotedSlavePart() throws UnsupportedEncodingException
    {
        assertTrue( URLDecoder.decode(
                Connection.urlEncode.apply( DecoderCGI.fromString(
                        "decoder.var?connections[64]=slaveip=192.168.1.10%2Cseq=F%2Cdwell=10"
                                + "&commands[4]=\"display_pic.cgi?\"&layouts[4]=0" ).getConnections().get( 64 ) ),
                "UTF-8" ).equals( "\"slaveip=192.168.1.10,seq=f,dwell=10\"" ) );
    }

    /**
     * Tests that a built DecoderCGI has all the values given to it.
     */
    @Test
    public void retention()
    {
        final DecoderCGI cgi =
                new DecoderCGI().persistence( Persistence.PERSISTENT ).command( 1, "foo" ).command( 2, "bar" ).outputTitles(
                        "one", "two", "three" ).layout( 1, Layout.FOUR_WAY ).layout( 2, Layout.NINE_WAY ).connection(
                        5, new Connection().audio( 4 ).seq( 0xF ).dwell( 40 ) );

        assertTrue( cgi.getCommands().size() == 2 );
        assertTrue( cgi.getCommands().get( 2 ).equals( "bar" ) );
        assertTrue( cgi.getOutputTitles()[2].equals( "three" ) );
        assertTrue( cgi.getLayouts().get( 2 ) == Layout.NINE_WAY );
    }
}
