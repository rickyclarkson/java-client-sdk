package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;

import uk.org.netvu.util.CheckParameters;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

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
              final FrameType frameType = FrameType.frameTypeFor(frameTypeInt);


              final int channel = input.read() + 1;
              final int length = new DataInputStream( input ).readInt();
              ByteBuffer data = IO.readIntoByteBuffer( input, length );
              data.position(0);

              // A minimal stream begins with an info packet containing "IMAGESIZE 0,0:123,456", where 123 and 456 are the
              // resolution of the frames.  We parse that out and store it in the resolutions array.
              // An array is used because of Java's 'final' restriction for anonymous classes.

              frameType.deliverTo( new StreamHandler()
                {
                  public void unknownDataArrived(Packet packet) {}
                  public void jpegFrameArrived(Packet packet){}
                  public void mpeg4FrameArrived(Packet packet) {}
                  public void infoArrived(Packet packet)
                  {                   
                    String s;
                    try
                    {
                      s = new String(packet.getData().array(), "US-ASCII");
                    }
                    catch (UnsupportedEncodingException e)
                      {
                        throw new RuntimeException(e);
                      }

                    final String header = "IMAGESIZE 0,0:";
                    int index = s.indexOf(header);
                    if (index != -1)
                      {
                        index += header.length();

                        String theRest = s.substring(index);
                        int comma = theRest.indexOf(",");
                        int semi = theRest.indexOf(";");
                        resolutions[0] = Short.parseShort(theRest.substring(0, comma));
                        resolutions[1] = Short.parseShort(theRest.substring(comma + 1, semi));
                      }
                  }
                }, IO.duplicate(data), channel, length, frameTypeInt, null, null );

                frameType.deliverTo( handler, IO.duplicate(data), channel, length, frameTypeInt, resolutions[0], resolutions[1] );
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
