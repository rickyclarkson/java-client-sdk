package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A parser for the 'binary' stream format.
 */
final class BinaryParser implements Parser
{
    /**
     * Parses an InputStream as a 'binary' stream, delivering data obtained from
     * the InputStream to the passed-in StreamHandler.
     * 
     * @param input
     *        the InputStream to read data from.
     * @param handler
     *        the StreamHandler to deliver parsed data to.
     * @throws IOException
     *         if any I/O errors occur, other than the end of the stream being
     *         reached, in which case the method returns.
     * @throws NullPointerException
     *         if input or handler are null.
     */
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull( input, handler );

        try
        {
            final Short[] resolutions = { null, null };

            while ( true )
            {
                final int frameTypeInt = input.read();
                final FrameParser frameParser = FrameParser.frameParserFor( frameTypeInt );

                final int channel = input.read() + 1;
                final int length = new DataInputStream( input ).readInt();
                final ByteBuffer data = IO.readIntoByteBuffer( input, length );
                data.position( 0 );

                // A minimal stream containing MPEG-4 data begins with an info
                // packet containing
                // "IMAGESIZE 0,0:123,456", where 123 and 456 are the
                // resolution of the frames. We parse that out and store it in
                // the resolutions array.
                // An array is used because of Java's 'final' restriction for
                // anonymous classes.
                // The type of it is Short rather than short to catch any
                // accidental misuses. This means that if the info
                // packet is missing, a minimal stream containing MPEG-4 will
                // not be parsed (a
                // NullPointerException will result).

                frameParser.parse( new StreamHandler()
                {
                    public void audioDataArrived( final Packet packet )
                    {
                    }

                    public void infoArrived( final Packet packet )
                    {
                        final String s = IO.bytesToString( packet.getData().array() );

                        final String header = "IMAGESIZE 0,0:";
                        int index = s.indexOf( header );
                        if ( index != -1 )
                        {
                            index += header.length();

                            final String theRest = s.substring( index );
                            final int comma = theRest.indexOf( "," );
                            final int semi = theRest.indexOf( ";" );
                            resolutions[0] = Short.parseShort( theRest.substring( 0, comma ) );
                            resolutions[1] = Short.parseShort( theRest.substring( comma + 1, semi ) );
                        }
                    }

                    public void jpegFrameArrived( final Packet packet )
                    {
                    }

                    public void mpeg4FrameArrived( final Packet packet )
                    {
                    }

                    public void unknownDataArrived( final Packet packet )
                    {
                    }
                }, IO.duplicate( data ), channel, null, null );

                frameParser.parse( handler, IO.duplicate( data ), channel, resolutions[0], resolutions[1] );
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
