package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.Arrays;

import uk.org.netvu.util.CheckParameters;

/**
 * A parser for MIME streams containing JFIF images.
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
      final Short[] horizontalResolution = { null }, verticalResolution = { null };

        while ( true )
        {
            try
            {
                CheckParameters.areNotNull( input, handler );
                final RawPacket packet = readRawPacket( input );

                if ( packet.contentType.startsWith( "image/admp4" ) )
                {
                    final String res = packet.contentType.substring( "image/admp4".length() );

                    if ( res.length() != 0 )
                    {
                        final Scanner scanner = new Scanner( res );
                        scanner.findInLine( "; xres=(\\d+); yres=(\\d+)" );
                        final MatchResult result = scanner.match();
                        horizontalResolution[0] = (short)Integer.parseInt( result.group( 1 ) );
                        verticalResolution[0] = (short)Integer.parseInt( result.group( 2 ) );
                    }

                    final RawPacket next = readRawPacket( input );
                    if ( next.contentType.equals( "text/plain" ) )
                    {
                      final String comment = IO.bytesToString( next.data.array() );
                        final int channel = getChannelFromCommentBlock( comment );

                        handler.mpeg4FrameArrived( new Packet( channel )
                        {
                            @Override
                            public ByteBuffer getData()
                            {
                                return IO.duplicate( packet.data );
                            }

                            @Override
                            public ByteBuffer getOnWireFormat()
                            {
                              boolean isIFrame = isIFrame( IO.duplicate(packet.data) );

                                final ImageDataStruct imageDataStruct =
                                        ImageDataStruct.createImageDataStruct( packet.data, comment,
                                                                               isIFrame ? VideoFormat.MPEG4_I_FRAME : VideoFormat.MPEG4_P_FRAME, horizontalResolution[0],
                                                verticalResolution[0] );

                                return imageDataStruct.getByteBuffer();
                            }
                        } );
                    }
                    else
                    {
                      throw new IllegalStateException("Content-type "+packet.contentType+" not expected; text/plain expected at this point.");
                    }
                }
                else if ( packet.contentType.startsWith( "audio/adpcm" ) )
                {
                    final AudioDataStruct audioDataStruct =
                            new AudioDataStruct( ByteBuffer.allocate( AudioDataStruct.AUDIO_DATA_STRUCT_SIZE ).putInt(
                                    AudioDataStruct.VERSION ) );

                    audioDataStruct.setMode( Integer.parseInt( packet.contentType.substring( "audio/adpcm; rate=".length() ) ) );
                    audioDataStruct.setChannel( 0 );
                    audioDataStruct.setStartOffset( AudioDataStruct.AUDIO_DATA_STRUCT_SIZE );
                    audioDataStruct.setSize( packet.data.limit() );
                    audioDataStruct.setSeconds( 0 ); // because the timestamps
                    // are unreliable.
                    audioDataStruct.setMilliseconds( 0 ); // because the
                    // timestamps are
                    // unreliable.

                    handler.audioDataArrived( new Packet( audioDataStruct.getChannel() )
                    {
                        @Override
                        public ByteBuffer getData()
                        {
                            return packet.data;
                        }

                        @Override
                        public ByteBuffer getOnWireFormat()
                        {
                            return packet.data;
                        }
                    } );
                }
                else
                {
                    final ByteBuffer jpeg = packet.data;
                    final String comments = JFIFHeader.getComments( jpeg );

                    final int channel = getChannelFromCommentBlock( comments );
                    handler.jpegFrameArrived( new Packet(channel)
                      {
                        public ByteBuffer getData()
                        {
                          return IO.duplicate(packet.data);
                        }

                        public ByteBuffer getOnWireFormat()
                        {
                          final int commentPosition = IO.searchFor( packet.data, JFIFHeader.byteArrayLiteral(new int[]{0xFF, 0xFE}));
                          final ByteBuffer data = packet.data;
                          data.position(commentPosition+2);
                          final int commentLength = data.getShort();
                          String comment = IO.bytesToString( IO.readIntoByteArray(packet.data, commentLength));
                          int ffc0 = IO.searchFor(data, new byte[]{ (byte)0xFF, (byte)0xC0 });
                          final VideoFormat videoFormat = packet.data.get(ffc0 + 11) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
                          final short targetPixels = packet.data.getShort(ffc0 + 5);
                          final short targetLines = packet.data.getShort(ffc0 + 7);
                          return ImageDataStruct.createImageDataStruct(packet.data, comment, videoFormat, targetLines, targetPixels).getByteBuffer();
                        }
                      });
                }
            }
            catch ( final EOFException e )
            {
                return;
            }
        }
    }

  /**
   * Parses a comment block read from a JFIF image, to retrieve the channel.
   * @param comments the comment block to parse.
   * @return the channel number read from the comment block.
   * @throws NullPointerException if comments is null.
   */
    private static int getChannelFromCommentBlock( final String comments )
    {
      CheckParameters.areNotNull(comments);
        final int numberStart = comments.indexOf( "Number:" );
        final int numberEnd = comments.indexOf( "\r\n", numberStart );
        return Integer.parseInt( comments.substring( numberStart + "Number: ".length(), numberEnd ) );
    }

  /**
   * Reads one part of a multi-part mime stream, returning the content-type and data as a RawPacket.
   * @param input the InputStream to read data from.
   * @return a RawPacket holding the content-type and data.
   * @throws IOException if an I/O error occurs.
   * @throws NullPointerException if input is null.
   */
    private static RawPacket readRawPacket( final InputStream input ) throws IOException
    {
      CheckParameters.areNotNull(input);
        IO.expectLineMatching( input, "" );
        IO.expectLineMatching( input, "--.*" );
        IO.expectLineMatching( input, "HTTP/1\\.[01] 200 .*" );
        IO.expectLine( input, "Server: ADH-Web" );

        IO.expectString( input, "Content-type: " );
        final String contentType = IO.readLine( input );
        IO.expectString( input, "Content-length: " );
        final int length = IO.readIntFromRestOfLine( input );

        IO.expectLine( input, "" );
        final ByteBuffer data = IO.readIntoByteBuffer( input, length );

        return new RawPacket( contentType, data );
    }

  /**
   * A data structure holding the content-type and the data for a single part of a multi-part mime stream.
   */
    private static class RawPacket
    {
      /**
       * The content-type of this RawPacket.
       */
        final String contentType;

      /**
       * The data of this RawPacket.
       */
        final ByteBuffer data;

      /**
       * Constructs a RawPacket with the specified content type and data.
       * @param contentType the content type of this RawPacket.
       * @param data the data of this RawPacket.
       * @throws NullPointerException if either of the parameters are null.
       */
        RawPacket( final String contentType, final ByteBuffer data )
        {
          CheckParameters.areNotNull(contentType, data);
            this.contentType = contentType;
            this.data = data;
        }
    }

  /**
   * The beginning header for VOP information in an MPEG-4 frame.
   */
  private static final byte[] MPEG4_VOP_START = { 0x00, 0x00, 0x01, (byte)0xB6 };

  /**
   * Identifies whether the data in the ByteBuffer is an MPEG-4 I-frame, by finding and parsing VOP headers.
   * @param data the ByteBuffer to search.
   * @return true if the data is an I-frame, false otherwise.
   * @throws NullPointerException if data is null.
   */
  static boolean isIFrame(ByteBuffer data)
  {
    CheckParameters.areNotNull(data);
    boolean foundVOP = false;
    byte[] startCode;
    int pos = 0;

    if (data != null)
    {
      startCode = new byte[4];
      while (!foundVOP && ((pos + 4) < data.limit()))
      {
        data.position(pos);
        data.get(startCode);
        if (Arrays.equals(startCode, MPEG4_VOP_START ) )
          {
            foundVOP = true;
          }
        pos++;
      }
      pos += 3;
      if (foundVOP)
      {
        int pictureType = ( data.get(pos) >> 6) & 0xFF;
        if (pictureType == 0)
          return true;
      }
      else
        throw new RuntimeException("Cannot find MPEG-4 VOP start code; cannot tell if I-frame");
    }
    return false;
  }
}
