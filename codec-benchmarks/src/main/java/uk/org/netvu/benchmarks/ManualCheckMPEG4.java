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

import uk.org.netvu.adffmpeg.ADFFMPEG4Decoder;
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
        CheckParameters.areNotNull( args );

        new JFrame()
        {
            {
                setSize( 800, 600 );
                add( new JScrollPane( new JPanel( new GridLayout( 1, 1 ) )
                {
                    {
                        try
                        {
                            // ParserFactory.parserFor( StreamType.BINARY
                            // ).parse(
                            // new URL( "http://localhost:2356/"
                            // ).openConnection().getInputStream(),
                            //
                            // // new FileInputStream(
                            // // "../data/testdata/192-168-106-207-minimal-mp4"
                            // // ), new Object(),
                            // new StreamHandler()
                            // {
                            // @Override
                            // public void audioDataArrived( Packet packet )
                            // {
                            // throw null;
                            // }
                            //
                            // @Override
                            // public void infoArrived( Packet packet )
                            // {
                            // throw null;
                            // }
                            //
                            // @Override
                            // public void jpegFrameArrived( Packet packet )
                            // {
                            // throw null;
                            // }
                            //
                            // @Override
                            // public void unknownDataArrived( Packet packet )
                            // {
                            // throw null;
                            // }
                            //
                            // boolean first = true;
                            //
                            // @Override
                            // public void mpeg4FrameArrived( Packet packet )
                            // {
                            //
                            // System.out.println( "mpeg 4 frame arrived" );
                            // ByteBuffer buffer = packet.getData();
                            // for ( int a = 0; a < buffer.limit(); a++ )
                            // {
                            // String hex = Integer.toHexString( buffer.get() &
                            // 0xFF );
                            // if ( hex.length() == 1 )
                            // {
                            // hex = "0" + hex;
                            // }
                            // System.out.print( hex );
                            // if ( a % 4 == 0 )
                            // {
                            // System.out.print( " " );
                            // }
                            // if ( a % 64 == 0 )
                            // {
                            // System.out.println();
                            // }
                            // }
                            //
                            // // if (first)
                            // {
                            // add( new JLabel( new ImageIcon(
                            // ADFFMPEG4Decoder.getInstance()
                            // .decodeMPEG4( packet.getData() ) ) ) );
                            // }
                            // first = false;
                            // }
                            // } );

                            final MPEGDecoder decoder = ADFFMPEG4Decoder.getInstance();

                            for ( int a = 0; a < 10; a++ )
                            {
                                System.out.println( a );
                                final File file = new File( "/home/ricky/next" + a );
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

                    private Object FileInputStream( final String string )
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                } ) );

                pack();
            }
        }.setVisible( true );
    }
}
