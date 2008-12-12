package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.regex.MatchResult;

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
        final Integer[] resolution = { null, null };

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
                        for ( int i = 1; i <= result.groupCount(); i++ )
                        {
                            resolution[i - 1] = Integer.parseInt( result.group( i ) );
                        }
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
                                if ( resolution[0] == null )
                                {
                                    new Exception().printStackTrace();
                                }
                                final ImageDataStruct imageDataStruct =
                                        ImageDataStruct.createImageDataStruct( packet.data, comment,
                                                VideoFormat.MPEG4_P_FRAME, resolution[0].shortValue(),
                                                resolution[1].shortValue() );
                                // TODO detect what kind of MPEG4 frame it is.

                                return imageDataStruct.getByteBuffer();
                            }
                        } );
                    }
                    else
                    {
                        throw null;
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
                    handler.jpegFrameArrived( new JFIFPacket( jpeg, channel, false ) );
                    // IO.expectLine(input, "");
                }
            }
            catch ( final EOFException e )
            {
                return;
            }
            // IO.expectLine( input, "" );
        }
    }

    private int getChannelFromCommentBlock( final String comments )
    {
        final int numberStart = comments.indexOf( "Number:" );
        final int numberEnd = comments.indexOf( "\r\n", numberStart );
        return Integer.parseInt( comments.substring( numberStart + "Number: ".length(), numberEnd ) );
    }

    private RawPacket readRawPacket( final InputStream input ) throws IOException
    {
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

    private static class RawPacket
    {
        final String contentType;
        final ByteBuffer data;

        RawPacket( final String contentType, final ByteBuffer data )
        {
            this.contentType = contentType;
            this.data = data;
        }
    }
}
