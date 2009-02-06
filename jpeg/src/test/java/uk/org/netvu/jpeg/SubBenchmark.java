package uk.org.netvu.jpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import uk.org.netvu.util.Function;

/**
 * A program that is launched many times by Benchmark, to time the execution of
 * a given number of iterations of a given decoder with a given test image.
 */
public class SubBenchmark
{
    /**
     * The decoders to benchmark.
     */
    public static final JPEGDecoder[] decoders =
            { JPEGDecoders.adffmpegDecoder, JPEGDecoders.toolkitDecoder, JPEGDecoders.imageIODecoder };

    /**
     * Executes the specified decoder with the specified constraints.
     * 
     * @param args
     *        the arguments to the process. The first argument is the index of
     *        the decoder. The second argument is the index of the test data
     *        file. The third argument is the index of the number of iterations
     *        to perform. The fourth argument is the index of the number of
     *        warmup times to perform. The fifth argument is the index of the
     *        input type to use.
     * @throws IOException
     *         if any I/O error occurred.
     */
    public static void main( final String[] args ) throws IOException
    {
        final JPEGDecoder decoder = decoders[Integer.parseInt( args[0] )];
        final SampleFile sampleFile = Benchmark.sampleFiles[Integer.parseInt( args[1] )];
        final int iterations = Benchmark.iterationAmounts[Integer.parseInt( args[2] )];
        final int warmUpTime = Benchmark.warmUpTimes[Integer.parseInt( args[3] )];
        final String inputType = Benchmark.inputTypes[Integer.parseInt( args[4] )];

        final String info =
                decoder.getClass().getSimpleName() + "," + sampleFile.filename + "," + sampleFile.width + ","
                        + sampleFile.height + "," + inputType + "," + warmUpTime + "," + iterations;

        if ( inputType.equals( "ByteBuffer" ) )
        {
            time( iterations, warmUpTime, decoder.decodeByteBuffer, bufferFor( sampleFile.filename ), info );
        }
        else
        {
            time( iterations, warmUpTime, decoder.decodeByteArray, byteArrayFor( sampleFile.filename ), info );
        }
    }

    /**
     * Constructs a ByteBuffer containing the data from the specified file.
     * 
     * @param filename
     *        the name of the file that contains the data to read.
     * @return a ByteBuffer containing the data from the specified file.
     * @throws IOException
     *         if any I/O error occurs.
     */
    public static ByteBuffer bufferFor( final String filename ) throws IOException
    {
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
     * Constructs an array of bytes containing the data from the specified file.
     * 
     * @param filename
     *        the name of the file that contains the data to read.
     * @return an array of bytes containing the data from the specified file.
     * @throws IOException
     *         if any I/O error occurs.
     */
    public static byte[] byteArrayFor( final String filename ) throws IOException
    {
        final ByteBuffer buffer = bufferFor( filename );
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return bytes;
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
     *        a Function that invokes the JPEGDecoder when applied.
     * @param input
     *        the input to the decoder.
     * @param info
     *        extra info to include in the output.
     */
    public static <T> void time( final int iterations, final long warmUpMillis, final Function<T, ?> decoder,
            final T input, final String info )
    {
        long start = System.nanoTime();
        while ( System.nanoTime() - start < warmUpMillis * 1000000 )
        {
            decoder.apply( input );
        }

        start = System.nanoTime();
        for ( int i = 0; i < iterations; i++ )
        {
            decoder.apply( input );
        }

        final long time = System.nanoTime() - start;
        System.out.println( info + "," + iterations * 1.0 / ( time / 1000000000.0 ) );
    }
}
