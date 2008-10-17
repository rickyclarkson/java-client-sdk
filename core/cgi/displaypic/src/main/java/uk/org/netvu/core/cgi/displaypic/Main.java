package uk.org.netvu.core.cgi.displaypic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Main
{
    public static void main( final String[] args ) throws Exception
    {
        final URL url = new URL( "file:testdata/192-168-106-204" );
        final URLConnection connection = url.openConnection();

        final int[] index = { 0 };

        Parser.parse( connection, new Handler()
        {
            public void handleJPEG( final byte[] data ) throws IOException
            {
                final FileOutputStream out = new FileOutputStream(
                        "/home/ricky/deleteme" + index[0]++ + ".jpg" );
                out.write( data );
                out.close();
            }
        } );

    }
}
