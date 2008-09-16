/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import javax.swing.JButton;

final class LayoutsButton
        extends JButton
{
    LayoutsButton( final Panel panel, final String text )
    {
        super( text );
        addActionListener( new LayoutsListener( panel ) );
    }
}
