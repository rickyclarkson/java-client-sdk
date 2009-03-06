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
        for ( final int iterations : Benchmark.rangeOver( Benchmark.iterationAmounts.length ) )
        {
            for ( final int warmUpTime : Benchmark.rangeOver( Benchmark.warmUpTimes.length ) )
            {
                final Process process =
                        Runtime.getRuntime().exec(
                                new String[] { "java", "-classpath", System.getProperty( "java.class.path" ),
                                    "uk.org.netvu.benchmarks.MPEG4SubBenchmark", String.valueOf( iterations ),
                                    String.valueOf( warmUpTime ) } );
                final Function<InputStream, Thread> consume = new Function<InputStream, Thread>()
                {
                    @Override
                    public Thread apply( final InputStream inputStream )
                    {
                        final Thread thread = new Thread( new Runnable()
                        {
                            public void run()
                            {
                                try
                                {
                                    final InputStreamReader isReader = new InputStreamReader( inputStream, "UTF-8" );
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
