package uk.org.netvu.benchmarks;

import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.org.netvu.adffmpeg.ADFFMPEGDecoders;
import uk.org.netvu.mpeg.MPEGDecoder;
import uk.org.netvu.util.CheckParameters;

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
                add( new JScrollPane( new JPanel( new GridLayout( 3, 2 ) )
                {
                    {
                        try
                        {
                            final MPEGDecoder decoder = ADFFMPEGDecoders.getMPEG4Decoder();

                            for ( int a = 0; a < 5; a++ )
                            {
                                final File file = new File( "testdata/mpeg4frames/" + a );
                                final InputStream in = new FileInputStream( file );
                                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                                int b;
                                while ( ( b = in.read() ) != -1 )
                                {
                                    out.write( b );
                                }

                                final byte[] bytes = out.toByteArray();
                                final ByteBuffer buffer = ByteBuffer.allocateDirect( bytes.length );
                                buffer.put( bytes );
                                buffer.position( 0 );
                                add( new JLabel( new ImageIcon( decoder.decodeMPEG4( buffer ) ) ) );
                            }
                        }
                        catch ( final IOException e )
                        {
                            throw new RuntimeException( e );
                        }
                    }
                } ) );

                pack();
            }
        }.setVisible( true );
    }
}
