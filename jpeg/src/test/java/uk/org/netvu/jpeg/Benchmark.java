package uk.org.netvu.jpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Benchmark
{
    public static int[] rangeOver( final int length )
    {
        final int[] indices = new int[length];
        for ( int a = 0; a < indices.length; a++ )
        {
            indices[a] = a;
        }

        return indices;
    }

    /* Must be kept in sync with SubBenchmark.decoders */
    public static final String[] decoderNames =
            { "JPEGDecoders.adffmpegDecoder", "JPEGDecoders.toolkitDecoder", "JPEGDecoders.imageIODecoder" };

    public static SampleFile[] sampleFiles =
            { new SampleFile( 352, 256, "192-168-106-206-352x256.jpg" ),
                new SampleFile( 320, 240, "mews-camvu-1-320x240.jpg" ),
                new SampleFile( 1600, 1200, "mews-camvu-2-1600x1200.jpg" ),
                new SampleFile( 320, 256, "192-168-106-207-320x256.jpg" ),
                new SampleFile( 352, 256, "dvip3s-ad-dev-adh-352x256.jpeg" ) };

    public static final int[] iterationAmounts = { 100, 1000, 10000 };
    public static final int[] warmUpTimes = { 100, 1000, 10000 };
    public static final String[] inputTypes = { "byte[]", "ByteBuffer" };

    public static void main( final String[] args ) throws IOException, InterruptedException
    {
        if ( args.length == 0 )
        {
            System.out.println( "decoder,filename,width,height,inputType,warmUpTime,iterations,iterations per second" );
            for ( final int iterations : rangeOver( iterationAmounts.length ) )
            {
                for ( final int warmUpTime : rangeOver( warmUpTimes.length ) )
                {
                    for ( final int decoder : rangeOver( decoderNames.length ) )
                    {
                        for ( final int resolution : rangeOver( sampleFiles.length ) )
                        {
                            for ( final int inputType : rangeOver( inputTypes.length ) )
                            {
                                final Process process =
                                        Runtime.getRuntime().exec(
                                                new String[] { "java", "-classpath",
                                                    System.getProperty( "java.class.path" ),
                                                    "uk.org.netvu.jpeg.SubBenchmark", String.valueOf( decoder ),
                                                    String.valueOf( resolution ), String.valueOf( iterations ),
                                                    String.valueOf( warmUpTime ), String.valueOf( inputType ) } );
                                // try moving waitFor
                                // try one thread per stream.

                                final Thread inputThread = new Thread( new Runnable()
                                {
                                    public void run()
                                    {
                                        try
                                        {
                                            final BufferedReader inputStreamReader =
                                                    new BufferedReader( new InputStreamReader( process
                                                        .getInputStream(), "UTF-8" ) );
                                            String line;
                                            while ( ( line = inputStreamReader.readLine() ) != null )
                                            {
                                                synchronized ( args )
                                                {
                                                    System.out.println( line );
                                                }
                                            }
                                            inputStreamReader.close();
                                        }
                                        catch ( final Exception e )
                                        {
                                            throw new RuntimeException( e );
                                        }
                                    }
                                } );
                                inputThread.start();
                                final Thread errorThread = new Thread( new Runnable()
                                {
                                    public void run()
                                    {
                                        try
                                        {
                                            final BufferedReader errorStreamReader =
                                                    new BufferedReader( new InputStreamReader( process
                                                        .getErrorStream(), "UTF-8" ) );
                                            String line;
                                            while ( ( line = errorStreamReader.readLine() ) != null )
                                            {
                                                synchronized ( args )
                                                {
                                                    System.out.println( line );
                                                }
                                            }
                                            errorStreamReader.close();
                                        }
                                        catch ( final Exception e )
                                        {
                                            throw new RuntimeException( e );
                                        }
                                    }
                                } );
                                errorThread.start();

                                process.waitFor();

                                inputThread.join();
                                errorThread.join();
                            }
                        }
                    }
                }
            }
        }
        else
        {
            throw null;
        }
    }
}
