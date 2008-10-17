package uk.org.netvu.core.cgi.displaypic;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public final class Parser
{
    public static void parse( final URLConnection connection,
            final Handler handler ) throws IOException
    {
        new Parser( handler, connection.getInputStream() ).parse( connection );
    }
    private final Handler handler;

    private final InputStream stream;

    private Parser( final Handler handler, final InputStream stream )
    {
        this.handler = handler;
        this.stream = stream;
    }

    private byte[] actuallyRead( final int length ) throws IOException
    {
        final byte[] bytes = new byte[length];
        new DataInputStream( stream ).readFully( bytes );
        return bytes;
    }

    private int expectIntFromRestOfLine() throws IOException
    {
        return Integer.parseInt( readLine() );
    }

    private void expectLine( final String expected ) throws IOException
    {
        final String line = readLine();

        if ( !expected.equals( line ) )
        {
            throw new IOException( expected + " expected, " + line + " found" );
        }
    }

    private void expectLineMatching( final String regex ) throws IOException
    {
        final String line = readLine();

        if ( !line.matches( regex ) )
        {
            throw new IOException( regex + " expected, " + line + " found" );
        }
    }

    private void expectString( final String string ) throws IOException
    {
        if ( string.equals( "" ) )
        {
            return;
        }

        if ( stream.read() != string.charAt( 0 ) )
        {
            throw null;
        }

        if ( string.length() > 1 )
        {
            expectString( string.substring( 1 ) );
        }
    }

    private void jpegParse( final int length ) throws IOException
    {
        final byte[] bytes = actuallyRead( length );
        handler.handleJPEG( bytes );
    }

    private void multiPartMimeParse() throws IOException
    {
        expectLine( "" );
        while ( true )
        {
            try
            {
                readAPart( stream );
            }
            catch ( final EOFException e )
            {
                return;
            }
        }
    }

    private void parse( final URLConnection connection ) throws IOException
    {
        if ( connection.getContentType().equals( "image/jpeg" ) )
        {
            jpegParse( connection.getContentLength() );
        }
        else
        {
            multiPartMimeParse();
        }
    }

    private void readAPart( final InputStream stream ) throws IOException
    {
        expectLineMatching( "--.*" );
        expectLineMatching( "HTTP/1\\.[01] 200 .*" );
        expectLine( "Server: ADH-Web" );
        expectLine( "Content-type: image/jpeg" );
        expectString( "Content-length: " );
        final int length = expectIntFromRestOfLine();
        expectLine( "" );
        jpegParse( length );
        expectLine( "" );
    }

    private String readLine() throws IOException
    {
        final int b = stream.read();
        if ( b == '\n' )
        {
            return "";
        }
        if ( b == '\r' )
        {
            return readLine();
        }
        if ( b == -1 )
        {
            throw null;
        }

        return String.valueOf( (char) b ) + readLine();
    }
}
