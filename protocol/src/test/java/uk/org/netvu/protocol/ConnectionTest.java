package uk.org.netvu.protocol;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

/**
 * Unit tests for the Connection class.
 */
public class ConnectionTest
{
    /**
     * Tests that cam and seq are mutually exclusive.
     */
    @Test( expected = IllegalStateException.class )
    public void mutualExclusiveParameters1()
    {
        new Connection().cam( 2 ).seq( 4 );
    }

    /**
     * Tests that cam and dwell are mutually exclusive.
     */
    @Test( expected = IllegalStateException.class )
    public void mutualExclusiveParameters2()
    {
        new Connection().cam( 2 ).dwell( 10 );
    }

    /**
     * Tests that parsing a URL into a Connection gives the correct slave IP
     * address.
     */
    @Test
    public void retention()
    {
        assertTrue( Connection.fromURL.apply( "slaveip=192.168.1.10&cam=1" ).getSlaveIP().equals( "192.168.1.10" ) );
    }

    /**
     * Tests that a Connection constructed with a slaveIP contains that slaveIP.
     */
    @Test
    public void retention2()
    {
        assertTrue( new Connection().slaveIP( "foo" ).getSlaveIP().equals( "foo" ) );
    }

    /**
     * Tests that URL encoding a Connection produces the expected output.
     * 
     * @throws UnsupportedEncodingException
     *         if UTF-8 is not supported - an impossible exception.
     */
    @Test
    public void urlEncode() throws UnsupportedEncodingException
    {
        assertTrue( URLDecoder.decode( Connection.urlEncode.apply( new Connection().cam( 2 ).slaveIP( "foo" ) ),
                "UTF-8" ).equals( "\"slaveip=foo,cam=2\"" ) );
    }
}
