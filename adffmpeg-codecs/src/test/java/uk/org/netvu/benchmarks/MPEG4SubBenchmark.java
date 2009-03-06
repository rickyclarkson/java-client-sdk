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
        final int iterations = Benchmarks.iterationAmounts[Integer.parseInt( args[0] )];
        final int warmUpTime = Benchmarks.warmUpTimes[Integer.parseInt( args[1] )];
        final String info = filename + "," + warmUpTime + "," + iterations;
        Benchmarks.time( decoder, iterations, warmUpTime, Benchmarks.bufferFor( filename ), info );
    }
}
