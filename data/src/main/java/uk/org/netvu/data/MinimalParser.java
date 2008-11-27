package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import uk.org.netvu.util.CheckParameters;

/**
 * A parser for the 'minimal' stream format.
 */
public class MinimalParser implements Parser
{
    /**
     * Parses a minimal stream from the specified InputStream, delivering data
     * as it arrives to the specified StreamHandler.
     * 
     * @param input
     *        the InputStream to read from.
     * @param handler
     *        the StreamHandler to deliver data to.
     * @throws IOException
     *         if an error occurs.
     * @throws NullPointerException
     *         if either of the parameters are null.
     */
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull( input, handler );
        try
        {
            while ( true )
            {
                PacketMetadata metadata = PacketMetadata.fromBinaryOrMinimalStream(input);

                metadata.getFrameType().deliverTo(handler, input, metadata);
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
