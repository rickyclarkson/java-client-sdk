/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import javax.swing.JButton;

final class OutputTitlesButton
        extends JButton
{
    OutputTitlesButton( final Panel panel, final String text )
    {
        super( text );
        addActionListener( new OutputTitlesListener( panel ) );
    }
}
