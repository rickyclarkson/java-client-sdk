package uk.org.netvu.benchmarks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.org.netvu.util.Function;
import uk.org.netvu.util.CheckParameters;

/**
 * The controller for benchmarks. It runs each benchmark in a child process, and
 * varies the input parameters. Each benchmark runs SubBenchmark, which is a
 * separate class merely to prevent Benchmark from loading the adffmpeg
 * libraries, as on Windows these cannot be used by two processes at the same
 * time.
 */
public class Benchmark
{
    /**
     * Prevents instantiation.
     */
    private Benchmark()
    {
    }

    /**
     * Gives an array containing the integers from 0 until the specified length,
     * exclusively.
     * 
     * @param length
     *        the length of the array to create.
     * @return an array containing the integers from 0 until the specified
     *         length, exclusively.
     */
    private static int[] rangeOver( final int length )
    {
        final int[] indices = new int[length];
        for ( int a = 0; a < indices.length; a++ )
        {
            indices[a] = a;
        }

        return indices;
    }

    /**
     * The number of decoders in use. This must always be equal to
     * SubBenchmark.decoders.length, but cannot be written that way as otherwise
     * the ADFFMPEG library will be loaded in the parent process.
     */
    private static final int numberOfDecoders = 3;

    /**
     * The test data used in the benchmarks.
     */
    static SampleFile[] sampleFiles =
            { new SampleFile( 352, 256, "192-168-106-206-352x256.jpg" ),
                new SampleFile( 320, 240, "mews-camvu-1-320x240.jpg" ),
                new SampleFile( 1600, 1200, "mews-camvu-2-1600x1200.jpg" ),
                new SampleFile( 320, 256, "192-168-106-207-320x256.jpg" ),
                new SampleFile( 352, 256, "dvip3s-ad-dev-adh-352x256.jpeg" ) };

    /**
     * The numbers of iterations to benchmark with.
     */
    public static final int[] iterationAmounts = { 100, 1000, 10000 };

    /**
     * The number of milliseconds to iterate for before timing the code. This
     * gives the JVM time to warm up to give more accurate results.
     */
    public static final int[] warmUpTimes = { 100, 1000, 10000 };

    /**
     * The type that the data should be passed in to the decoder as.
     */
    public static final String[] inputTypes = { "byte[]", "ByteBuffer" };

    /**
     * Runs the benchmarks.
     * 
     * @param args
     *        the arguments to the program - should be empty.
     * @throws IOException
     *         if any I/O fails.
     * @throws InterruptedException
     *         if the parent is interrupted while waiting for the child process.
     * @throws NullPointerException
     *         if args is null.
     */
    public static void main( final String[] args ) throws IOException, InterruptedException
    {
        CheckParameters.areNotNull( args );
        System.out.println( "decoder,filename,width,height,inputType,warmUpTime,iterations,iterations per second" );
        for ( final int iterations : rangeOver( iterationAmounts.length ) )
        {
            for ( final int warmUpTime : rangeOver( warmUpTimes.length ) )
            {
                for ( final int decoder : rangeOver( numberOfDecoders ) )
                {
                    for ( final int resolution : rangeOver( sampleFiles.length ) )
                    {
                        for ( final int inputType : rangeOver( inputTypes.length ) )
                        {
                            final Process process =
                                    Runtime.getRuntime().exec(
                                            new String[] { "java", "-classpath",
                                                System.getProperty( "java.class.path" ),
                                                "uk.org.netvu.codecbenchmarks.SubBenchmark",
                                                String.valueOf( decoder ), String.valueOf( resolution ),
                                                String.valueOf( iterations ), String.valueOf( warmUpTime ),
                                                String.valueOf( inputType ) } );

                            final Function<InputStream, Thread> consume = new Function<InputStream, Thread>()
                            {
                                /**
                                 * {@inheritDoc}
                                 */
                                @Override                                
                                public Thread apply( final InputStream inputStream )
                                {
                                    final Thread thread = new Thread( new Runnable()
                                    {
                                        public void run()
                                        {
                                            try
                                            {
                                                final InputStreamReader isReader =
                                                        new InputStreamReader( inputStream, "UTF-8" );
                                                final BufferedReader reader = new BufferedReader( isReader );
                                                String line;
                                                while ( ( line = reader.readLine() ) != null )
                                                {
                                                    synchronized ( args )
                                                    {
                                                        System.out.println( line );
                                                    }
                                                }
                                                reader.close();
                                                isReader.close();

                                            }
                                            catch ( final IOException e )
                                            {
                                                throw new RuntimeException( e );
                                            }
                                        }
                                    } );
                                    thread.start();
                                    return thread;
                                }
                            };

                            final Thread inputThread = consume.apply( process.getInputStream() );
                            final Thread errorThread = consume.apply( process.getErrorStream() );

                            process.waitFor();

                            inputThread.join();
                            errorThread.join();
                        }
                    }
                }
            }
        }
    }
}
