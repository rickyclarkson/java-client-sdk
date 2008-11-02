package uk.org.netvu.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

final class BinaryParser implements Parser
{
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {
        try
        {
            while ( true )
            {
                System.out.println("Parsing some data");
                BinaryStreamMetadata metadata = new BinaryStreamMetadata( input );
                System.out.println("metadata.frameType = " + metadata.frameType);
                metadata.frameType.deliverTo( input, handler, metadata );
            }
        }
        catch (EOFException e)
        {
            return;
        }
    }
}