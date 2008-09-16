package uk.org.netvu.core.cgi.demo;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A demonstration program that allows the user to interact with the decoder and
 * see what values the relevant variables have.
 */
public class DecoderDemo
{
    /**
     * The starting point of the program.
     * 
     * @param args
     *        ignored.
     */
    public static void main( final String[] args )
    {
        final JFrame frame = new JFrame();
        final JTextArea variables = new JTextArea( 50, 50 );
        frame.add( new JScrollPane( variables ), BorderLayout.EAST );
        frame.add( new Panel( variables, frame ) );
        frame.pack();
        frame.setExtendedState( Frame.MAXIMIZED_BOTH );
        frame.setVisible( true );
    }
}
