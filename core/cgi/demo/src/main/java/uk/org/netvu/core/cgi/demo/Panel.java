/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.variable.Variable;
import uk.org.netvu.core.cgi.variable.VariableCGI;

final class Panel
        extends JPanel
{
    private final JTextArea variables;
    private final JFrame frame;
    {
        add( new ConnectionsButton( this, "connections" ) );
        add( new LayoutsButton( this, "layouts" ) );
        add( new OutputTitlesButton( this, "output titles" ) );
        add( new CommandsButton( this, "commands" ) );
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
        final List<String> unquoted = Lists.map(
                Strings.splitIgnoringQuotedSections(
                        readOneLine( "http://192.168.106.127/"
                                + new VariableCGI.Builder().variable(
                                        Variable.CONNECTIONS ).build().toString() ),
                        ',' ), new Unquote() );
        variables.setText( "Connections: \n"
                + Strings.intersperse( "\n", Lists.map( Lists.filter(
                        Lists.zipWithIndex( unquoted ), new Hack1() ),
                        new Hack2() ) )
                + "'\nLayouts:\n"
                + readOneLine( "http://192.168.106.127/"
                        + new VariableCGI.Builder().variable( Variable.LAYOUTS ).build().toString() )
                + "\nOutput titles: \n"
                + readOneLine( "http://192.168.106.127/"
                        + new VariableCGI.Builder().variable(
                                Variable.OUTPUT_TITLES ).build().toString() )
                + "\nCommands: \n"
                + readOneLine( "http://192.168.106.127/"
                        + new VariableCGI.Builder().variable( Variable.COMMANDS ).build().toString() ) );
        variables.setCaretPosition( 0 );
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
            return new BufferedReader( new InputStreamReader( stream, "UTF-8" ) ).readLine();
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
