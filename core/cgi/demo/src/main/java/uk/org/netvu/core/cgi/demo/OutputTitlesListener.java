/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.org.netvu.core.cgi.decoder.DecoderCGI;

final class OutputTitlesListener implements ActionListener
{
    /**
     * 
     */
    private final Panel panel;

    /**
     * @param outputTitlesButton
     */
    OutputTitlesListener( final Panel panel )
    {
        this.panel = panel;
    }

    public void actionPerformed( final ActionEvent e )
    {
        panel.run( new DecoderCGI().outputTitles(
                panel.getString( "First title" ), panel.getString( "second" ),
                panel.getString( "last one" ) ).toURLParameters() );
    }
}
