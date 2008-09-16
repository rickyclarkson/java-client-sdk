package uk.org.netvu.core.cgi.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Generator;
import uk.org.netvu.core.cgi.common.Generators;
import uk.org.netvu.core.cgi.events.EventsCGI;
import uk.org.netvu.core.cgi.events.EventsCGIResult;
import uk.org.netvu.core.cgi.events.EventsCGITest;
import uk.org.netvu.core.cgi.events.EventsCGI.Builder;

/**
 * A program that runs many randomly-generated queries against servers, to see
 * whether they behave well.
 */
public class IntegrationTests
{
    /**
     * The entry point for the program.
     * 
     * @param args
     *        ignored
     * @throws IOException
     */
    public static void main( final String[] args ) throws IOException
    {
        final Random random = new Random( 0 );

        final Generator<Builder> builders = EventsCGITest.randomEventsCGIBuilders( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final EventsCGI cgi = builders.next().format( Format.CSV ).build();

            final URL url = new URL( "http://192.168.106.202" + cgi );

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
