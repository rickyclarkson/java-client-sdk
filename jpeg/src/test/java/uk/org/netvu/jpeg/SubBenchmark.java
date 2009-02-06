package uk.org.netvu.jpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import uk.org.netvu.util.Function;

public class SubBenchmark
{
    public static final JPEGDecoder[] decoders =
            { JPEGDecoders.adffmpegDecoder, JPEGDecoders.toolkitDecoder, JPEGDecoders.imageIODecoder };

    public static void main( final String[] args ) throws IOException, InterruptedException
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

    public static byte[] byteArrayFor( final String filename ) throws IOException
    {
        final ByteBuffer buffer = bufferFor( filename );
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return bytes;
    }

    public static <T> void time( final int iterations, final long warmUpMillis, final Function<T, ?> decoder,
            final T input, final String info )
    {
        final long start = System.nanoTime();
        while ( System.nanoTime() - start < warmUpMillis * 1000000 )
        {
            decoder.apply( input );
        }

        humourNetbeans( iterations, decoder, input, info );
    }

    public static <T> void humourNetbeans( final int iterations, final Function<T, ?> decoder, final T input,
            final String info )
    {
        final long start = System.nanoTime();
        for ( int i = 0; i < iterations; i++ )
        {
            decoder.apply( input );
        }

        final long time = System.nanoTime() - start;
        System.out.println( info + "," + iterations * 1.0 / ( time / 1000000000.0 ) );
    }
}
