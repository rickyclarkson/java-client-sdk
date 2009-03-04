package uk.org.netvu.benchmarks;

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.org.netvu.adffmpeg.Buffers;
import uk.org.netvu.jpeg.JPEGDecoder;
import uk.org.netvu.util.CheckParameters;

/**
 * A simple graphical program to help visually check that all the decoders are
 * decoding all the images correctly.
 */
public class ManualCheck
{
    /**
     * To prevent instantiation.
     */
    private ManualCheck()
    {
    }

    /**
     * Displays a JFrame containing a JScrollPane containing a JPanel containing
     * all the test images decoded by each of the decoders in a grid.
     * 
     * @param args
     *        ignored.
     * @throws NullPointerException
     *         if args is null.
     */
    public static void main( final String[] args )
    {
        CheckParameters.areNotNull( (Object) args );
        CheckParameters.areNotNull( (Object[]) args );

        new JFrame()
        {
            {
                setSize( 800, 600 );
                add( new JScrollPane( new JPanel( new GridLayout( SubBenchmark.decoders.size(),
                        Benchmark.sampleFiles.length ) )
                {
                    {
                        try
                        {
                            for ( final JPEGDecoder decoder : SubBenchmark.decoders )
                            {
                                for ( final SampleFile sampleFile : Benchmark.sampleFiles )
                                {
                                    add( new JLabel( new ImageIcon( decoder.decodeJPEG( Buffers
                                        .bufferFor( sampleFile.filename ) ) ) ) );
                                }
                            }
                        }
                        catch ( final IOException e )
                        {
                            throw new RuntimeException( e );
                        }
                    }
                } ) );
            }
        }.setVisible( true );
    }
}
