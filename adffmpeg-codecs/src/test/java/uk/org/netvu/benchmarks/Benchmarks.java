package uk.org.netvu.benchmarks;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import uk.org.netvu.codecs.VideoCodec;
import uk.org.netvu.codecs.VideoDecoder;
import java.nio.ByteBuffer;
import uk.org.netvu.util.CheckParameters;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileInputStream;

final class Benchmarks
{
    static void dump(Process process) throws InterruptedException
    {
        Thread in = dump(process.getInputStream());
        Thread err = dump(process.getErrorStream());
        process.waitFor();
        in.join();
        err.join();
    }

    static final Object sync = new Object();

    static Thread dump(final InputStream inputStream)
    {
        final Thread thread = new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    final InputStreamReader isReader = new InputStreamReader(inputStream, "UTF-8");
                    final BufferedReader reader = new BufferedReader(isReader);
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        synchronized(sync)
                        {
                            System.out.println(line);
                        }
                    }
                    reader.close();
                    isReader.close();
                }
                catch (final IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Executes the specified decoder with the specified input for the specified
     * number of iterations, after warming the decoder up by executing it for at
     * least the specified number of milliseconds, printing the results to
     * System.out as CSV.
     * 
     * @param iterations
     *        the number of iterations to time.
     * @param warmUpMillis
     *        the number of milliseconds to give the decoder to warm up before
     *        timing it.
     * @param decoder
     *        the JPEGDecoder to use.
     * @param input
     *        the input to the decoder.
     * @param info
     *        extra info to include in the output.
     * @throws NullPointerException
     *         if decoder, input or info are null.
     */
    public static <T extends VideoCodec> void time( VideoDecoder<T> decoder, final int iterations, final long warmUpMillis, final ByteBuffer input, final String info )
    {
        CheckParameters.areNotNull( decoder, input, info );
        long start = System.nanoTime();
        while ( System.nanoTime() - start < warmUpMillis * 1000000 )
        {
            decoder.decode( input );
        }
        start = System.nanoTime();
        for ( int i = 0; i < iterations; i++ )
        {
            decoder.decode( input );
        }
        final long time = System.nanoTime() - start;
        System.out.println( info + "," + iterations * 1.0 / ( time / 1000000000.0 ) );
    }

    /**
     * Constructs a ByteBuffer containing the data from the specified file.
     * 
     * @param filename
     *        the name of the file that contains the data to read.
     * @return a ByteBuffer containing the data from the specified file.
     * @throws IOException
     *         if any I/O error occurs.
     * @throws NullPointerException
     *         if filename is null.
     */
    public static ByteBuffer bufferFor( final String filename ) throws IOException
    {
        CheckParameters.areNotNull( filename );
        final ByteBuffer first =
                new FileInputStream( filename ).getChannel().map( FileChannel.MapMode.READ_ONLY, 0,
                        new File( filename ).length() );
        first.position( 0 );
        final ByteBuffer result = ByteBuffer.allocateDirect( first.limit() );
        result.put( first );
        result.position( 0 );
        return result;
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
    static int[] rangeOver( final int length )
    {
        final int[] indices = new int[length];
        for ( int a = 0; a < indices.length; a++ )
        {
            indices[a] = a;
        }

        return indices;
    }

    /**
     * The numbers of iterations to benchmark with.
     */
    static final int[] iterationAmounts = { 100, 1000, 10000 };

    /**
     * The number of milliseconds to iterate for before timing the code. This
     * gives the JVM time to warm up to give more accurate results.
     */
    static final int[] warmUpTimes = { 100, 1000, 10000 };
}