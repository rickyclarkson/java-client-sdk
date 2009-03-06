package uk.org.netvu.benchmarks;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.org.netvu.adffmpeg.ADFFMPEGDecoders;
import uk.org.netvu.codecs.VideoCodec;
import uk.org.netvu.codecs.VideoDecoder;
import uk.org.netvu.util.CheckParameters;

public class MPEG4SubBenchmark
{
    private MPEG4SubBenchmark()
    {
    }

    public static void main( final String[] args ) throws IOException
    {
        CheckParameters.areNotNull( (Object) args );
        CheckParameters.areNotNull( (Object[]) args );

        final VideoDecoder<VideoCodec.MPEG4> decoder = ADFFMPEGDecoders.getMPEG4Decoder();
        final String filename = "192-168-106-206-mp4";
        final int iterations = Benchmark.iterationAmounts[Integer.parseInt( args[0] )];
        final int warmUpTime = Benchmark.warmUpTimes[Integer.parseInt( args[1] )];
        final String info = filename + "," + warmUpTime + "," + iterations;
        time( decoder, iterations, warmUpTime, SubBenchmark.bufferFor( filename ), info );
    }

    public static void time( final VideoDecoder<VideoCodec.MPEG4> decoder, final int iterations,
            final long warmUpMillis, final ByteBuffer input, final String info )
    {
        CheckParameters.areNotNull( input, info );
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
}
