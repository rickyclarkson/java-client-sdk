package uk.org.netvu.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

public class MinimalParser implements Parser
{
    public void parse( final InputStream input, final StreamHandler handler) throws IOException
    {
        try
        {
            while (true)
            {
                MinimalStreamMetadata metadata = new MinimalStreamMetadata( input );
                metadata.frameType.deliverTo( input, handler, metadata);
            }
        }
        catch (EOFException e)
        {
            return;
        }
    }
}