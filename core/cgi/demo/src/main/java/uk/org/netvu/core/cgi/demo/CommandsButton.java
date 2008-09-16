/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import javax.swing.JButton;

final class CommandsButton
        extends JButton
{
    CommandsButton( final Panel panel, final String text )
    {
        super( text );
        addActionListener( new CommandsListener( panel ) );
    }
}
