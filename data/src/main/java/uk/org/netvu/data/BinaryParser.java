package uk.org.netvu.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import uk.org.netvu.util.CheckParameters;

/**
 * A parser for the 'binary' stream format.
 */
final class BinaryParser implements Parser
{
    /**
     * Parses an InputStream as a 'binary' stream, delivering data obtained from the InputStream to the passed-in StreamHandler.
     * @param input the InputStream to read data from.
     * @param handler the StreamHandler to deliver parsed data to.
     * @throws IOException if any I/O errors occur, other than the end of the stream being reached, in which case the method returns.
     * @throws NullPointerException if input or handler are null.
     */
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull(input, handler);

        try
        {
            while ( true )
            {
                BinaryStreamMetadata metadata = new BinaryStreamMetadata( input );
                metadata.getFrameType().deliverTo( handler, input, metadata );
            }
        }
        catch (EOFException e)
        {
            return;
        }
    }
}