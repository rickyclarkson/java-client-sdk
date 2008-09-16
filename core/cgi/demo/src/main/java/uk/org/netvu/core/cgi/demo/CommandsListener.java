/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.org.netvu.core.cgi.decoder.DecoderCGI;

final class CommandsListener implements ActionListener
{
    private final Panel panel;

    CommandsListener( final Panel panel )
    {
        this.panel = panel;
    }

    public void actionPerformed( final ActionEvent e )
    {
        panel.run( new DecoderCGI().command( panel.getInt( "index" ),
                panel.getString( "command" ) ).toURLParameters() );
    }
}
