package uk.org.netvu.benchmarks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;

public class MPEG4Benchmark
{
    private MPEG4Benchmark()
    {
    }


    public static void main( final String[] args ) throws IOException, InterruptedException
    {        
        CheckParameters.areNotNull( (Object) args );
        CheckParameters.areNotNull( (Object[]) args );

        System.out.println( "filename,warmUpTime,iterations,iterations per second" );
        for ( final int iterations : Benchmarks.rangeOver( Benchmarks.iterationAmounts.length ) )
        {
            for ( final int warmUpTime : Benchmarks.rangeOver( Benchmarks.warmUpTimes.length ) )
            {
                Benchmarks.dump(Runtime.getRuntime().exec(new String[] { "java", "-classpath",
                                                                         System.getProperty( "java.class.path" ),
                                                                         "uk.org.netvu.benchmarks.MPEG4SubBenchmark",
                                                                         String.valueOf( iterations ),
                                                                         String.valueOf( warmUpTime ) } ));
            }
        }
    }
}
