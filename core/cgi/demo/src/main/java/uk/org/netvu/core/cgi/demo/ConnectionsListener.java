/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.org.netvu.core.cgi.decoder.Connection;
import uk.org.netvu.core.cgi.decoder.DecoderCGI;

final class ConnectionsListener implements ActionListener
{
    private final Panel panel;

    /**
     * @param connectionsButton
     */
    ConnectionsListener( final Panel panel )
    {
        this.panel = panel;
    }

    public void actionPerformed( final ActionEvent e )
    {
        panel.run( new DecoderCGI().connection(
                panel.getInt( "index" ),
                new Connection().cam( panel.getInt( "camera number" ) ).slaveIP(
                        panel.getString( "slave IP" ) ) ).toURLParameters() );
    }
}
