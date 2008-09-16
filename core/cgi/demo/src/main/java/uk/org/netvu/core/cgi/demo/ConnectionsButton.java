/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import javax.swing.JButton;

final class ConnectionsButton
        extends JButton
{
    ConnectionsButton( final Panel panel, final String text )
    {
        super( text );
        addActionListener( new ConnectionsListener( panel ) );
    }
}
