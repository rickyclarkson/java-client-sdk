package uk.org.netvu.core.cgi.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Pair;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.decoder.Connection;
import uk.org.netvu.core.cgi.decoder.DecoderCGI;
import uk.org.netvu.core.cgi.decoder.Layout;
import uk.org.netvu.core.cgi.variable.Variable;
import uk.org.netvu.core.cgi.variable.VariableCGI;

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

    private static final class Panel
            extends JPanel
    {
        private final JTextArea variables;
        private final JFrame frame;
        {
            final class ConnectionsButton
                    extends JButton
            {
                ConnectionsButton( final Panel panel, final String text )
                {
                    super( text );
                    final class ConnectionsListener implements ActionListener
                    {
                        public void actionPerformed( final ActionEvent e )
                        {
                            panel.run( new DecoderCGI().connection(
                                    panel.getInt( "index" ),
                                    new Connection().cam(
                                            panel.getInt( "camera number" ) ).slaveIP(
                                            panel.getString( "slave IP" ) ) ).toURLParameters() );
                        }
                    }
                    addActionListener( new ConnectionsListener() );
                }
            }

            add( new ConnectionsButton( this, "connections" ) );
            final class LayoutsButton
                    extends JButton
            {
                LayoutsButton( final Panel panel, final String text )
                {
                    super( text );
                    final class LayoutsListener implements ActionListener
                    {
                        public void actionPerformed( final ActionEvent e )
                        {
                            panel.run( new DecoderCGI().layout(
                                    panel.getInt( "index" ),
                                    Layout.find( panel.getInt( "layout" ) ) ).toURLParameters() );
                        }
                    }

                    addActionListener( new LayoutsListener() );
                }
            }

            add( new LayoutsButton( this, "layouts" ) );
            final class OutputTitlesButton
                    extends JButton
            {
                OutputTitlesButton( final Panel panel, final String text )
                {
                    super( text );
                    final class OutputTitlesListener implements ActionListener
                    {
                        public void actionPerformed( final ActionEvent e )
                        {
                            panel.run( new DecoderCGI().outputTitles(
                                    panel.getString( "First title" ),
                                    panel.getString( "second" ),
                                    panel.getString( "last one" ) ).toURLParameters() );
                        }
                    }

                    addActionListener( new OutputTitlesListener() );
                }
            }
            add( new OutputTitlesButton( this, "output titles" ) );
            add( new JButton( "commands" )
            {
                {
                    addActionListener( new ActionListener()
                    {
                        public void actionPerformed( final ActionEvent e )
                        {
                            Panel.this.run( new DecoderCGI().command(
                                    Panel.this.getInt( " index" ),
                                    Panel.this.getString( "command" ) ).toURLParameters() );
                        }
                    } );
                }
            } );
        }

        Panel( final JTextArea variables, final JFrame frame )
        {
            this.variables = variables;
            this.frame = frame;
        }

        public int getInt( final String name )
        {
            return Integer.parseInt( getString( name ) );
        }

        public String getString( final String name )
        {
            return JOptionPane.showInputDialog( frame, name );
        }

        void run( final String query )
        {
            System.out.println( query );
            try
            {
                new URL( "http://192.168.106.127/" + query ).openStream().close();
            }
            catch ( final FileNotFoundException e )
            {
            }
            catch ( final IOException e )
            {
                throw new RuntimeException( e );
            }

            final class Unquote
                    extends Conversion<String, String>
            {
                @Override
                public String convert( final String quoted )
                {
                    return quoted.substring( 1, quoted.length() - 1 );
                }
            }

            final List<String> unquoted = Lists.map(
                    Strings.splitIgnoringQuotedSections(
                            readOneLine( "http://192.168.106.127/"
                                    + new VariableCGI.Builder().variable(
                                            Variable.CONNECTIONS ).build().toString() ),
                            ',' ), new Unquote() );

            final class Hack2
                    extends Conversion<Pair<String, Integer>, String>
            {
                @Override
                public String convert( final Pair<String, Integer> pair )
                {
                    return pair.getSecondComponent() + " -> " + pair.getFirstComponent();
                }
            }

            final class Hack1
                    extends Conversion<Pair<String, Integer>, Boolean>
            {
                @Override
                public Boolean convert(
                        final Pair<String, Integer> valueAndIndex )
                {
                    return valueAndIndex.getFirstComponent().length() != 0;
                }
            }

            variables.setText( "Connections: \n"
                    + Strings.intersperse( "\n", Lists.map( Lists.filter(
                            zipWithIndex( unquoted ), new Hack1() ),
                            new Hack2() ) )
                    + "'\nLayouts:\n"
                    + readOneLine( "http://192.168.106.127/"
                            + new VariableCGI.Builder().variable(
                                    Variable.LAYOUTS ).build().toString() )
                    + "\nOutput titles: \n"
                    + readOneLine( "http://192.168.106.127/"
                            + new VariableCGI.Builder().variable(
                                    Variable.OUTPUT_TITLES ).build().toString() )
                    + "\nCommands: \n"
                    + readOneLine( "http://192.168.106.127/"
                            + new VariableCGI.Builder().variable(
                                    Variable.COMMANDS ).build().toString() ) );
            variables.setCaretPosition( 0 );
        }

        private <T> List<Pair<T, Integer>> zipWithIndex(List<T> list)
        {
            List<Pair<T, Integer>> result = new ArrayList<Pair<T, Integer>>();

            int a=0;

            for (T t: list)
            {
                list.add(new Pair<T, Integer>(t, a++));
            }

            return list;
        }

        private String readOneLine( final String url )
        {
            InputStream stream;
            try
            {
                stream = new URL( url ).openStream();
            }
            catch ( final IOException e )
            {
                throw new RuntimeException( e );
            }
            try
            {
                return new BufferedReader( new InputStreamReader( stream,
                        "UTF-8" ) ).readLine();
            }
            catch ( final IOException e )
            {
                throw new RuntimeException( e );
            }
            finally
            {
                try
                {
                    stream.close();
                }
                catch ( final IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
