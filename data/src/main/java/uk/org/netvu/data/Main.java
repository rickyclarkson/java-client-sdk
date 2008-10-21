package uk.org.netvu.data;

import java.net.URL;

public class Main
{
    public static void main( final String[] args ) throws Exception
    {
        final URL url = new URL( "file:testdata/192-168-106-204" );
        url.openConnection();

        /*
         * Parser.parse( connection, new Handler() { public void handleJPEG(
         * final ByteBuffer data ) throws IOException { final FileOutputStream
         * out = new FileOutputStream( System.getProperty("user.dir") +
         * "/deleteme" + index[0]++ + ".jpg" ); out.write( data ); out.close();
         * } } );
         */

    }
}
