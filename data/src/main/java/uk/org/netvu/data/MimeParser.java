package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A parse for MIME streams containing JFIF images.
 */
class MimeParser implements Parser
{
    /**
     * Parses a MIME stream from the specified InputStream, delivering data as
     * it arrives to the specified StreamHandler.
     * 
     * @param input
     *        the InputStream where the MIME data is to be read from.
     * @param handler
     *        the StreamHandler to deliver data to.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if either parameter are null.
     */
    public void parse( final InputStream input, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull( input, handler );
        IO.expectLine( input, "" );
        try
        {
            while ( true )
            {
                IO.expectLineMatching( input, "--.*" );
                IO.expectLineMatching( input, "HTTP/1\\.[01] 200 .*" );
                IO.expectLine( input, "Server: ADH-Web" );
                IO.expectLine( input, "Content-type: image/jpeg" );
                IO.expectString( input, "Content-length: " );
                final int length = IO.expectIntFromRestOfLine( input );
                IO.expectLine( input, "" );
                final ByteBuffer jpeg = IO.readIntoByteBuffer( input, length );

                final String comments = JFIFHeader.getComments( jpeg );
                final int numberStart = comments.indexOf( "Number:" );
                final int numberEnd = comments.indexOf( "\r\n", numberStart );
                final int channel =
                        Integer.parseInt( comments.substring( numberStart + "Number: ".length(), numberEnd ) );

                final PacketMetadata metadata = new PacketMetadata( length, channel, FrameType.JFIF );
                handler.jfif( Packet.jfifPacket( jpeg, metadata ) );
                IO.expectLine( input, "" );
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
