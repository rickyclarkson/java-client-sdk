package uk.org.netvu.core.cgi.events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A demonstration program that runs ad_events.cgi on a server and reports the
 * results as they arrive.
 */
class ServerDemo
{
    /**
     * The main method to start the program with.
     * 
     * @param args
     *        ignored.
     * @throws IOException
     */
    public static void main( final String[] args ) throws IOException
    {
        final EventsCGI cgi = new EventsCGI.Builder().build();
        final URL url = new URL( "http://remguard_mews.adnv.adh" + cgi );

        final InputStream in = url.openStream();

        try
        {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader( in, "UTF-8" ) );
            try
            {
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    System.out.println( EventsCGIResult.fromString( line ).getAlarm() );
                }
            }
            finally
            {
                reader.close();
            }
        }
        finally
        {
            in.close();
        }
    }
}
