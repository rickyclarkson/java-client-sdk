package uk.org.netvu.jpeg;

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A simple graphical program to help visually check that all the decoders are
 * decoding all the images correctly.
 */
public class ManualCheck
{
    /**
     * Displays a JFrame containing a JScrollPane containing a JPanel containing
     * all the test images decoded by each of the decoders in a grid.
     * 
     * @param args
     *        ignored.
     */
    public static void main( final String[] args )
    {
        new JFrame()
        {
            {
                setSize( 800, 600 );
                add( new JScrollPane( new JPanel( new GridLayout( SubBenchmark.decoders.length,
                        Benchmark.sampleFiles.length ) )
                {
                    {
                        try
                        {
                            for ( final JPEGDecoder decoder : SubBenchmark.decoders )
                            {
                                for ( final SampleFile sampleFile : Benchmark.sampleFiles )
                                {
                                    add( new JLabel( new ImageIcon( decoder.decodeByteArray( SubBenchmark
                                        .byteArrayFor( sampleFile.filename ) ) ) ) );
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
