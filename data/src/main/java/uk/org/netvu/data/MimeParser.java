package uk.org.netvu.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

class MimeParser implements Parser
{
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {    
        IO.expectLine( input, "" );
        try
        {
            while ( true )
            {
                IO.expectLineMatching(input, "--.*" );
                IO.expectLineMatching(input, "HTTP/1\\.[01] 200 .*" );
                IO.expectLine( input, "Server: ADH-Web" );
                IO.expectLine( input, "Content-type: image/jpeg" );
                IO.expectString( input, "Content-length: " );
                final int length = IO.expectIntFromRestOfLine(input);
                IO.expectLine( input, "" );
                ByteBuffer jpeg = jpegParse( input, length );
                handler.jfif(new JFIFPacket(jpeg, new MimeStreamMetadata(length)));
                IO.expectLine( input, "" );
            }
        }
        catch (EOFException e)
        {
            return;
        }
    }

    private ByteBuffer jpegParse( final InputStream input, final int length ) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocate( length );
        Channels.newChannel( input ).read(buffer);
        buffer.position(0);
        return buffer;
    }
}