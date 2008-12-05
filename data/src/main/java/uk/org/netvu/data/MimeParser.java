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
  private static class RawPacket
  {
    final String contentType;
    final ByteBuffer data;

    RawPacket(String contentType, ByteBuffer data)
    {
      this.contentType=contentType;
      this.data=data;
    }
  }

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
      while (true)
      {
        try
        {
          CheckParameters.areNotNull( input, handler );
          RawPacket packet = readRawPacket(input);

          if (packet.contentType.startsWith("image/admp4"))
            {
              RawPacket next = readRawPacket(input);
              if (next.contentType.equals("text/plain"))
              {
                String comment = new String(next.data.array(), "US-ASCII");
                int channel = getChannelFromCommentBlock(comment);

                handler.mpeg4FrameArrived( new MPEG4Packet( packet.data, channel ) );
              }
              else
                throw null;
            }
          else
            {
              ByteBuffer jpeg = packet.data;
              final String comments = JFIFHeader.getComments(jpeg);
              
              int channel = getChannelFromCommentBlock( comments);
              handler.jpegFrameArrived( new JFIFPacket( jpeg, channel, false) );
              //IO.expectLine(input, "");
            }    
        }
        catch (EOFException e)
          {
            return;
          }
        //        IO.expectLine( input, "" );
      }
    }

  private int getChannelFromCommentBlock(String comments)
  {
              final int numberStart = comments.indexOf("Number:");
              final int numberEnd = comments.indexOf("\r\n", numberStart);
              return Integer.parseInt(comments.substring(numberStart+"Number: ".length(), numberEnd));
  }

  private RawPacket readRawPacket(InputStream input) throws IOException
  {
    IO.expectLineMatching(input,"");
          IO.expectLineMatching( input, "--.*" );
          IO.expectLineMatching( input, "HTTP/1\\.[01] 200 .*" );
          IO.expectLine( input, "Server: ADH-Web" );
          
          IO.expectString(input, "Content-type: ");
          String contentType = IO.readLine( input );
          IO.expectString( input, "Content-length: " );
          final int length = IO.expectIntFromRestOfLine( input );
          if (length == 1)
                  throw null;
          
          IO.expectLine( input, "" );
          final ByteBuffer data = IO.readIntoByteBuffer( input, length );
          
          return new RawPacket(contentType, data);
    }
}
