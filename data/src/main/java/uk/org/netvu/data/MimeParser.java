package uk.org.netvu.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import uk.org.netvu.util.CheckParameters;

/**
 * A parse for MIME streams containing JFIF images.
 */
class MimeParser implements Parser
{
    /**
     * Parses a MIME stream from the specified InputStream, delivering data as it arrives to the specified StreamHandler.
     * @param input the InputStream where the MIME data is to be read from.
     * @param handler the StreamHandler to deliver data to.
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if either parameter are null.
     */
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {    
        CheckParameters.areNotNull(input, handler);
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
                ByteBuffer jpeg = IO.readIntoByteBuffer( input, length );
                handler.jfif(jpeg);
                IO.expectLine( input, "" );
            }
        }
        catch (EOFException e)
        {
            return;
        }
    }
}