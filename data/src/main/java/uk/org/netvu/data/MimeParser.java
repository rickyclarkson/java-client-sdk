package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.MatchResult;

import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Validation;

/**
 * A parser for MIME streams containing JFIF images.
 */
class MimeParser implements Parser
{
    /**
     * Parses a comment block read from a JFIF image, to retrieve the channel.
     * 
     * @param comments
     *        the comment block to parse.
     * @return the channel number read from the comment block.
     * @throws NullPointerException
     *         if comments is null.
     */
    private static int getChannelFromCommentBlock( final String comments )
    {
        CheckParameters.areNotNull( comments );
        final int numberStart = comments.indexOf( "Number:" );
        final int numberEnd = comments.indexOf( "\r\n", numberStart );
        return Integer.parseInt( comments.substring( numberStart + "Number: ".length(), numberEnd ) );
    }

    /**
     * Reads one part of a multi-part mime stream, returning the content-type
     * and data as a RawPacket.
     * 
     * @param input
     *        the InputStream to read data from.
     * @return a RawPacket holding the content-type and data.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input is null.
     */
    private static RawPacket readRawPacket( final InputStream input ) throws IOException
    {
        CheckParameters.areNotNull( input );
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
     * {@inheritDoc}
     */
    public void parse( final InputStream input, final Object sourceIdentifier, final StreamHandler handler )
            throws IOException
    {
        final Short[] horizontalResolution = { null };
        final Short[] verticalResolution = { null };

        while ( true )
        {
            try
            {
                CheckParameters.areNotNull( input, handler );
                final RawPacket packet = readRawPacket( input );

                final String IMAGE_ADMP4 = "image/admp4";

                if ( packet.contentType.startsWith( IMAGE_ADMP4 ) )
                {
                    final String res = packet.contentType.substring( IMAGE_ADMP4.length() );

                    if ( res.length() != 0 )
                    {
                        final Scanner scanner = new Scanner( res );
                        scanner.findInLine( "; xres=(\\d+); yres=(\\d+)" );
                        final MatchResult result = scanner.match();
                        horizontalResolution[0] = (short) Integer.parseInt( result.group( 1 ) );
                        verticalResolution[0] = (short) Integer.parseInt( result.group( 2 ) );
                    }

                    final RawPacket next = readRawPacket( input );
                    if ( next.contentType.equals( "text/plain" ) )
                    {
                        final String comment = IO.bytesToString( next.data.array() );
                        final int channel = getChannelFromCommentBlock( comment );

                        handler.mpeg4FrameArrived( new Packet( channel, sourceIdentifier )
                        {
                            @Override
                            public ByteBuffer getData()
                            {
                                return IO.duplicate( packet.data );
                            }

                            @Override
                            public ByteBuffer getOnDiskFormat()
                            {
                                final boolean isIFrame = Validation.isIFrame( IO.duplicate( packet.data ) );

                                final ImageDataStruct imageDataStruct =
                                        ImageDataStruct.construct( packet.data, comment,
                                                isIFrame ? VideoFormat.MPEG4_I_FRAME : VideoFormat.MPEG4_P_FRAME,
                                                verticalResolution[0], horizontalResolution[0] );

                                return imageDataStruct.getByteBuffer();
                            }
                        } );
                    }
                    else
                    {
                        throw new IllegalStateException( "Content-type " + packet.contentType
                                + " not expected; text/plain expected at this point." );
                    }
                }
                else if ( packet.contentType.startsWith( "audio/adpcm" ) )
                {
                    final AudioDataStruct audioDataStruct = AudioDataStruct.construct( packet.data.limit() );
                    final String modePart = packet.contentType.substring( "audio/adpcm; rate=".length() );
                    audioDataStruct.setMode( Integer.parseInt( modePart ) );
                    audioDataStruct.setChannel( 0 );
                    audioDataStruct.setStartOffset( AudioDataStruct.AUDIO_DATA_STRUCT_SIZE );
                    audioDataStruct.setSize( packet.data.limit() );
                    audioDataStruct.setSeconds( 0 ); // because the timestamps
                    // are unreliable.
                    audioDataStruct.setMilliseconds( 0 ); // because the
                    // timestamps are
                    // unreliable.

                    handler.audioDataArrived( Packet.constructPacket( audioDataStruct.getChannel(), sourceIdentifier,
                            packet.data, packet.data ) );
                }
                else
                {
                    final ByteBuffer jpeg = packet.data;
                    final String comments = JFIFHeader.getComments( jpeg );

                    final int channel = getChannelFromCommentBlock( comments );
                    handler.jpegFrameArrived( new Packet( channel, sourceIdentifier )
                    {
                        @Override
                        public ByteBuffer getData()
                        {
                            return IO.duplicate( packet.data );
                        }

                        @Override
                        public ByteBuffer getOnDiskFormat()
                        {
                            final int commentPosition =
                                    IO.searchFor( packet.data, new byte[] { (byte) 0xFF, (byte) 0xFE } );
                            final ByteBuffer data = packet.data;
                            data.position( commentPosition + 2 );
                            final int commentLength = data.getShort();
                            final String comment =
                                    IO.bytesToString( IO.readIntoByteArray( packet.data, commentLength ) );
                            final int ffc0 = IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xC0 } );
                            final VideoFormat videoFormat =
                                    packet.data.get( ffc0 + 11 ) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
                            final short targetPixels = packet.data.getShort( ffc0 + 5 );
                            final short targetLines = packet.data.getShort( ffc0 + 7 );
                            return ImageDataStruct.construct( packet.data, comment, videoFormat, targetLines,
                                    targetPixels ).getByteBuffer();
                        }
                    } );
                }
            }
            catch ( final EOFException e )
            {
                return;
            }
        }
    }

    /**
     * A data structure holding the content-type and the data for a single part
     * of a multi-part mime stream.
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
         * 
         * @param contentType
         *        the content type of this RawPacket.
         * @param data
         *        the data of this RawPacket.
         * @throws NullPointerException
         *         if either of the parameters are null.
         */
        RawPacket( final String contentType, final ByteBuffer data )
        {
            CheckParameters.areNotNull( contentType, data );
            this.contentType = contentType;
            this.data = data;
        }
    }
}
