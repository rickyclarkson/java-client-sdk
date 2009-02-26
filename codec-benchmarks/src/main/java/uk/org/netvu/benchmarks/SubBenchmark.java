package uk.org.netvu.benchmarks;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.adffmpeg.ADFFMPEGDecoder;
import uk.org.netvu.jpeg.ImageIODecoder;
import uk.org.netvu.jpeg.JPEGDecoder;
import uk.org.netvu.jpeg.ToolkitDecoder;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Pair;
import uk.org.netvu.util.CheckParameters;

import java.util.Arrays;

/**
 * A program that is launched many times by Benchmark, to time the execution of
 * a given number of iterations of a given decoder with a given test image.
 */
public class SubBenchmark
{
    /**
     * To prevent instantiation.
     */
    private SubBenchmark()
    {
    }

    /**
     * The decoders to benchmark.
     */
    public static final List<JPEGDecoder> decoders = Arrays.asList(ADFFMPEGDecoder.getInstance(), ToolkitDecoder.getInstance(), ImageIODecoder.getInstance());

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
     * @throws NullPointerException
     *         if args is null.
     */
    public static void main( final String[] args ) throws IOException
    {
        CheckParameters.areNotNull( args );
        final JPEGDecoder decoder = decoders.get( Integer.parseInt( args[0] ) );
        final SampleFile sampleFile = Benchmark.sampleFiles[Integer.parseInt( args[1] )];
        final int iterations = Benchmark.iterationAmounts[Integer.parseInt( args[2] )];
        final int warmUpTime = Benchmark.warmUpTimes[Integer.parseInt( args[3] )];

        final String info =
                decoder.getClass().getSimpleName() + "," + sampleFile.filename + ","
                        + sampleFile.width + "," + sampleFile.height + "," + + warmUpTime + ","
                        + iterations;

        time( iterations, warmUpTime, decoder, bufferFor( sampleFile.filename ), info );
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
     * @throws NullPointerException if decoder, input or info are null.
     */
    public static void time( final int iterations, final long warmUpMillis, final JPEGDecoder decoder,
            final ByteBuffer input, final String info )
    {
        CheckParameters.areNotNull( decoder, input, info );
        long start = System.nanoTime();
        while ( System.nanoTime() - start < warmUpMillis * 1000000 )
        {
            decoder.decodeJPEG( input );
        }
        start = System.nanoTime();
        for ( int i = 0; i < iterations; i++ )
        {
            decoder.decodeJPEG( input );
        }
        final long time = System.nanoTime() - start;
        System.out.println( info + "," + iterations * 1.0 / ( time / 1000000000.0 ) );
    }

    /**
     * A method that returns JPEGDecoder.decodeJPEG as a Function on the specified JPEGDecoder.
     * This is used to allow the method to be passed as a value into time().
     *
     * @param decoder the JPEGDecoder that the returned Function uses to decode JPEGs.
     * @return a Function that takes in a ByteBuffer containing JPEG data, and returns it as an
     * Image after using the specified JPEGDecoder to decode it.
     * @throws NullPointerException if decoder is null.
     */
    private static Function<ByteBuffer, Image> decodeJPEG( final JPEGDecoder decoder )
    {
        CheckParameters.areNotNull( decoder );
        return new Function<ByteBuffer, Image>()
        {
            @Override
            public Image apply( final ByteBuffer buffer )
            {
                return decoder.decodeJPEG( buffer );
            }
        };
    }
}
