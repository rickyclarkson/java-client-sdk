package uk.org.netvu.benchmarks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

import uk.org.netvu.adffmpeg.ADFFMPEGDecoders;
import uk.org.netvu.codecs.ImageIODecoder;
import uk.org.netvu.codecs.VideoDecoder;
import uk.org.netvu.codecs.VideoCodec;
import uk.org.netvu.codecs.ToolkitDecoder;
import uk.org.netvu.util.CheckParameters;

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
    public static final List<VideoDecoder<VideoCodec.JPEG>> decoders =
            Arrays.asList( ADFFMPEGDecoders.getJPEGDecoder(), ToolkitDecoder.createInstance(), ImageIODecoder
                .createInstance() );

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
        CheckParameters.areNotNull( (Object) args );
        CheckParameters.areNotNull( (Object[]) args );

        final VideoDecoder<VideoCodec.JPEG> decoder = decoders.get( Integer.parseInt( args[0] ) );
        final SampleFile sampleFile = Benchmark.sampleFiles[Integer.parseInt( args[1] )];
        final int iterations = Benchmarks.iterationAmounts[Integer.parseInt( args[2] )];
        final int warmUpTime = Benchmarks.warmUpTimes[Integer.parseInt( args[3] )];

        final String info =
                decoder.getClass().getSimpleName() + "," + sampleFile.filename + "," + sampleFile.width + ","
                        + sampleFile.height + "," + +warmUpTime + "," + iterations;

        Benchmarks.time( decoder, iterations, warmUpTime, Benchmarks.bufferFor( sampleFile.filename ), info );
    }
}