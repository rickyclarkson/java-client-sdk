package uk.org.netvu.core.cgi.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.events.EventsCGI;
import uk.org.netvu.core.cgi.events.EventsCGIResult;

/**
 * A demonstration program that runs ad_events.cgi on a server and reports the
 * results as they arrive.
 */
public class EventsCGIDemo
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
        if ( args.length == 0 )
        {
            System.err.println( "Please supply the name of a camera server" );
        }
        else
        {
            final EventsCGI cgi = new EventsCGI.Builder().format( Format.CSV ).build();
            final URL url = new URL( "http://" + args[0] + cgi );

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
}
