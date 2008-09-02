package uk.org.netvu.core.cgi.decoder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConnectionTest
{
    @Test
    public void retention()
    {
        assertTrue( Connection.fromURL.convert( "slaveip=192.168.1.10&cam=1" ).getSlaveIP().equals(
                "192.168.1.10" ) );
    }
}
