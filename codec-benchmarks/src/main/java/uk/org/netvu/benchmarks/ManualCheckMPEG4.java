package uk.org.netvu.benchmarks;

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.org.netvu.jpeg.JPEGDecoder;
import uk.org.netvu.jpeg.JPEGDecoderFromArray;
import uk.org.netvu.util.Pair;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.adffmpeg.ADFFMPEG4Decoder;
import java.io.FileInputStream;
import uk.org.netvu.data.*;
/**
 * A simple graphical program to help visually check that all the decoders are
 * decoding all the images correctly.
 */
public class ManualCheckMPEG4
{
    /**
     * To prevent instantiation.
     */
    private ManualCheckMPEG4()
    {
    }

    /**
     * Displays a JFrame containing a JScrollPane containing a JPanel containing
     * all the test images decoded by each of the decoders in a grid.
     * 
     * @param args
     *        ignored.
     * @throws NullPointerException if args is null.
     */
    public static void main( final String[] args )
    {      
        CheckParameters.areNotNull( args );

        new JFrame()
        {
            {
                setSize( 800, 600 );
                add( new JScrollPane( new JPanel( new GridLayout( 10, 10 )
                {
                    {                        
                        try
                        {
                            ParserFactory.parserFor(StreamType.BINARY).parse( new FileInputStream("/home/ricky/java-client-sdk/trunk/codec-benchmarks/192-168-106-206-mp4"), new Object(), new StreamHandler()
                                {
                                    @Override
                                    public void audioDataArrived(Packet packet)
                                    {
                                        throw null;
                                    }

                                    @Override
                                    public void infoArrived(Packet packet)
                                    {
                                        throw null;
                                    }

                                    @Override
                                    public void jpegFrameArrived(Packet packet)
                                    {
                                        throw null;
                                    }

                                    @Override
                                    public void unknownDataArrived(Packet packet)
                                    {
                                        throw null;
                                    }

                                    boolean first = true;
                                    @Override
                                    public void mpeg4FrameArrived(Packet packet)
                                    {
                                        System.out.println("mpeg 4 frame arrived");
                                        if (first)
                                            add( new JLabel( new ImageIcon( ADFFMPEG4Decoder.getInstance().decodeMPEG4(packet.getData()))));
                                        first = false;
                                    }
                                });
                        }
                        catch ( final IOException e )
                        {
                            throw new RuntimeException( e );
                        }
                    }
                } ) ) );
            }
        }.setVisible( true );
    }
}
