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
import uk.org.netvu.jpeg.JPEGDecoderFromArray;
import uk.org.netvu.jpeg.ToolkitDecoder;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Pair;

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
    public static final List<Pair<JPEGDecoder, JPEGDecoderFromArray>> decoders =
            new ArrayList<Pair<JPEGDecoder, JPEGDecoderFromArray>>()
            {
                {
                    // this is an anonymous initialiser.
                    add( new Pair<JPEGDecoder, JPEGDecoderFromArray>( ADFFMPEGDecoder.getInstance(), ADFFMPEGDecoder
                        .getInstance() ) );
                    add( new Pair<JPEGDecoder, JPEGDecoderFromArray>( ToolkitDecoder.getInstance(), ToolkitDecoder
                        .getInstance() ) );
                    add( new Pair<JPEGDecoder, JPEGDecoderFromArray>( ImageIODecoder.getInstance(), ImageIODecoder
                        .getInstance() ) );
                }
            };

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
        final Pair<JPEGDecoder, JPEGDecoderFromArray> decoder = decoders.get( Integer.parseInt( args[0] ) );
        final SampleFile sampleFile = Benchmark.sampleFiles[Integer.parseInt( args[1] )];
        final int iterations = Benchmark.iterationAmounts[Integer.parseInt( args[2] )];
        final int warmUpTime = Benchmark.warmUpTimes[Integer.parseInt( args[3] )];
        final String inputType = Benchmark.inputTypes[Integer.parseInt( args[4] )];

        final String info =
                decoder.getFirstComponent().getClass().getSimpleName() + "," + sampleFile.filename + ","
                        + sampleFile.width + "," + sampleFile.height + "," + inputType + "," + warmUpTime + ","
                        + iterations;

        if ( inputType.equals( "ByteBuffer" ) )
        {
            time( iterations, warmUpTime, decodeJPEG( decoder.getFirstComponent() ), bufferFor( sampleFile.filename ),
                    info );
        }
        else
        {
            time( iterations, warmUpTime, decodeJPEGFromArray( decoder.getSecondComponent() ),
                    byteArrayFor( sampleFile.filename ), info );
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

    /**
     * A method that returns JPEGDecoder.decodeJPEG as a Function on the specified JPEGDecoder.
     * This is used to allow the method to be passed as a value into time().
     *
     * @param decoder the JPEGDecoder that the returned Function uses to decode JPEGs.
     * @return a Function that takes in a ByteBuffer containing JPEG data, and returns it as an Image after using the specified JPEGDecoder to decode it.
     */
    private static Function<ByteBuffer, Image> decodeJPEG( final JPEGDecoder decoder )
    {
        return new Function<ByteBuffer, Image>()
        {
            @Override
            public Image apply( final ByteBuffer buffer )
            {
                return decoder.decodeJPEG( buffer );
            }
        };
    }

    /**
     * A method that returns JPEGDecoderFromArray.decodeJPEGFromArray as a Function on the specified JPEGDecoderFromArray.
     * This is used to allow the method to be passed as a value into time().
     *
     * @param decoder the JPEGDecoderFromArray that the returned Function uses to decode JPEGs.
     * @return a Function that takes in an array of bytes containing JPEG data, and returns it as an Image after using the specified JPEGDecoderFromArray to decode it.
     */
    public static Function<byte[], Image> decodeJPEGFromArray( final JPEGDecoderFromArray decoder )
    {
        return new Function<byte[], Image>()
        {
            @Override
            public Image apply( final byte[] array )
            {
                return decoder.decodeJPEGFromArray( array );
            }
        };
    }
}
