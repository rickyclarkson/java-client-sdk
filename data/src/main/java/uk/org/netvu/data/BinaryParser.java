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
   * @{inheritDoc}
   */
  public void parse( final InputStream input, final Object sourceIdentifier, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull( input, handler );

        try
        {
            final Short[] horizontalResolution = { null };
            final Short[] verticalResolution = { null };

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
                // the horizontalResolution and verticalResolution arrays.
                // Arrays are used because of Java's 'final' restriction for
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
                      handler.audioDataArrived(packet);
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
                            horizontalResolution[0] = Short.parseShort( theRest.substring( 0, comma ) );
                            verticalResolution[0] = Short.parseShort( theRest.substring( comma + 1, semi ) );
                        }
                        else
                          {
                            handler.infoArrived(packet);
                          }
                    }

                    public void jpegFrameArrived( final Packet packet )
                    {
                      handler.jpegFrameArrived(packet);
                    }

                    public void mpeg4FrameArrived( final Packet packet )
                    {
                      handler.mpeg4FrameArrived(packet);
                    }

                    public void unknownDataArrived( final Packet packet )
                    {
                      handler.unknownDataArrived(packet);
                    }
                  }, sourceIdentifier, IO.duplicate( data ), channel, horizontalResolution[0], verticalResolution[0] );

                /*                frameParser.parse( handler, IO.duplicate( data ), channel, horizontalResolution[0],
                                  verticalResolution[0] );*/
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
