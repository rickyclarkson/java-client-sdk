/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.org.netvu.core.cgi.decoder.DecoderCGI;
import uk.org.netvu.core.cgi.decoder.Layout;

final class LayoutsListener implements ActionListener
{
    private final Panel panel;

    /**
     * @param layoutsButton
     */
    LayoutsListener( final Panel panel )
    {
        this.panel = panel;
    }

    public void actionPerformed( final ActionEvent e )
    {
        panel.run( new DecoderCGI().layout( panel.getInt( "index" ),
                Layout.find( panel.getInt( "layout" ) ) ).toURLParameters() );
    }
}
