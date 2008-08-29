package uk.org.netvu.core.cgi.decoder;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

public class DecoderCGITest
{
    @Test
    public void retention() throws UnsupportedEncodingException
    {
        final DecoderCGI cgi = new DecoderCGI().command( 1, "foo" ).command( 2,
                "bar" ).outputTitles( "one", "two", "three" ).layout( 1,
                Layout.FOUR_WAY ).layout( 2, Layout.NINE_WAY );

        assertTrue( cgi.getCommands().size() == 2 );
        assertTrue( cgi.getCommands().get( 2 ).equals( "bar" ) );
        assertTrue( cgi.getOutputTitles()[2].equals( "three" ) );
        assertTrue( cgi.getLayouts().get( 2 ) == Layout.NINE_WAY );

        System.out.println( URLDecoder.decode( cgi.toURLParameters(), "UTF-8" ) );
    }
}
