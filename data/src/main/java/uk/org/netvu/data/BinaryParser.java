package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

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
            while ( true )
            {
              final int frameTypeInt = input.read();
              final FrameType frameType = FrameType.frameTypeFor(frameTypeInt);

              final int channel = input.read() + 1;
              final int length = new DataInputStream( input ).readInt();
              ByteBuffer data = IO.readIntoByteBuffer( input, length );
              data.position(0);
              frameType.deliverTo( handler, data, channel, length, frameTypeInt );
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
