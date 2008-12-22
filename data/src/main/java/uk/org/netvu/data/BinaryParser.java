package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A parser for the 'binary' stream format.
 */
final class BinaryParser implements Parser
{
    /**
     * The frame type for truncated JFIF images, as in the binary stream format.
     */
    private static final int TRUNCATED_JFIF = 0;

    /**
     * The frame type for JFIF images, as in the binary stream format.
     */
    private static final int JFIF = 1;

    /**
     * The frame type for MPEG-4 p-frames, as in the binary stream format.
     */
    private static final int MPEG4_P_FRAME = 2;

    /**
     * The frame type for MPEG-4 i-frames, as in the binary stream format.
     */
    private static final int MPEG4_I_FRAME = 3;

    /**
     * The frame type for ADPCM (audio) data, as in the binary stream format.
     */
    private static final int ADPCM = 4;

    /**
     * The frame type for MPEG-4 frames, as in the minimal stream format.
     */
    private static final int MPEG4_MINIMAL = 6;

    /**
     * The frame type for information blocks, as in the binary stream format.
     */
    private static final int INFO = 9;

    private static ImageDataStruct parseJfifOrJpeg( final ByteBuffer jfifOrJpegData )
    {
        final ByteBuffer data = IO.duplicate( jfifOrJpegData );
        try
        {
            IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xD8 } );
            final int ffc0 = IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xC0 } );
            final int commentPosition = IO.searchFor( data, JFIFHeader.byteArrayLiteral( new int[] { 0xFF, 0xFE } ) );
            data.position( commentPosition + 2 );
            final int commentLength = data.getShort();
            final String comment = IO.bytesToString( IO.readIntoByteArray( data, commentLength ) );
            final VideoFormat videoFormat =
                    data.get( ffc0 + 11 ) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
            final short targetPixels = data.getShort( ffc0 + 5 );
            final short targetLines = data.getShort( ffc0 + 7 );
            return ImageDataStruct.construct( data, comment, videoFormat, targetLines, targetPixels );
        }
        catch ( final BufferUnderflowException e )
        {
            final int decade = IO.searchFor( data, new byte[] { (byte) 0xDE, (byte) 0xCA, (byte) 0xDE } );
            return parseJfifOrJpeg( JFIFHeader.jpegToJfif( IO.from( data, decade ) ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parse( final InputStream input, final Object sourceIdentifier, final StreamHandler handler )
            throws IOException
    {
        CheckParameters.areNotNull( input, handler );

        try
        {
            Short horizontalResolution = null;
            Short verticalResolution = null;

            while ( true )
            {
                final int frameTypeInt = input.read();
                final int channel = input.read() + 1;
                final int length = new DataInputStream( input ).readInt();
                final ByteBuffer data = IO.readIntoByteBuffer( input, length );
                data.position( 0 );

                // A minimal stream containing MPEG-4 data begins with an info
                // packet containing
                // "IMAGESIZE 0,0:123,456", where 123 and 456 are the
                // resolution of the frames. We parse that out and store it in
                // horizontalResolution and verticalResolution.
                // The type of them is Short rather than short to catch any
                // accidental misuses. This means that if the info
                // packet is missing, a minimal stream containing MPEG-4 will
                // not be parsed (a
                // NullPointerException will result).

                Packet packet;

                switch ( frameTypeInt )
                {
                    case TRUNCATED_JFIF:
                        packet = new Packet( channel, sourceIdentifier )
                        {
                            @Override
                            public ByteBuffer getData()
                            {
                                return JFIFHeader.jpegToJfif( data );
                            }

                            @Override
                            public ByteBuffer getOnDiskFormat()
                            {
                                return IO.duplicate( data );
                            }
                        };
                        break;
                    case JFIF:
                        packet = new Packet( channel, sourceIdentifier )
                        {
                            @Override
                            public ByteBuffer getData()
                            {
                                try
                                {
                                    IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xD8 } );
                                    return IO.duplicate( data );
                                }
                                catch ( final BufferUnderflowException e )
                                {
                                    final ByteBuffer buffer = parseJfifOrJpeg( data ).getData();
                                    buffer.position( 0 );
                                    return buffer;
                                }
                            }

                            @Override
                            public ByteBuffer getOnDiskFormat()
                            {
                                return parseJfifOrJpeg( data ).getByteBuffer();
                            }
                        };
                        break;

                    case MPEG4_P_FRAME:
                    case MPEG4_I_FRAME:
                        final ImageDataStruct imageHeader = ImageDataStruct.construct( data );
                        final ByteBuffer restOfData =
                                IO.from( data, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
                        packet = Packet.constructPacket( channel, sourceIdentifier, restOfData, data );
                        break;

                    case ADPCM:
                        packet =
                                Packet.constructPacket( channel, sourceIdentifier, IO.from( data,
                                        AudioDataStruct.AUDIO_DATA_STRUCT_SIZE + 6 ), data );
                        break;

                    case MPEG4_MINIMAL:
                        if ( horizontalResolution == null || verticalResolution == null )
                        {
                            throw new IllegalStateException( "The stream does not contain an IMAGESIZE comment, "
                                    + "so the MPEG-4 frames cannot be correctly parsed." );
                        }
                        final short constantHorizontal = horizontalResolution;
                        final short constantVertical = verticalResolution;

                        packet = new Packet( channel, sourceIdentifier )
                        {
                            @Override
                            public ByteBuffer getData()
                            {
                                return IO.from( data, 6 );
                            }

                            @Override
                            public ByteBuffer getOnDiskFormat()
                            {
                                final boolean iFrame = MimeParser.isIFrame( IO.duplicate( data ) );
                                final VideoFormat videoFormat =
                                        iFrame ? VideoFormat.MPEG4_I_FRAME : VideoFormat.MPEG4_P_FRAME;
                                return ImageDataStruct.construct( data, "", videoFormat, constantHorizontal,
                                        constantVertical ).getByteBuffer();
                            }
                        };
                        break;

                    default:
                        packet = Packet.constructPacket( channel, sourceIdentifier, data, data );
                }

                if ( frameTypeInt == INFO )
                {
                    final String s = IO.bytesToString( packet.getData().array() );
                    final String header = "IMAGESIZE 0,0:";
                    int index = s.indexOf( header );
                    if ( index != -1 )
                    {
                        index += header.length();

                        final String theRest = s.substring( index );
                        final int comma = theRest.indexOf( "," );
                        final int semi = theRest.indexOf( ";" );
                        horizontalResolution = Short.parseShort( theRest.substring( 0, comma ) );
                        verticalResolution = Short.parseShort( theRest.substring( comma + 1, semi ) );
                        continue;
                    }
                }

                switch ( frameTypeInt )
                {
                    case TRUNCATED_JFIF:
                    case JFIF:
                        handler.jpegFrameArrived( packet );
                        break;

                    case MPEG4_P_FRAME:
                    case MPEG4_I_FRAME:
                    case MPEG4_MINIMAL:
                        handler.mpeg4FrameArrived( packet );
                        break;

                    case ADPCM:
                        handler.audioDataArrived( packet );
                        break;

                    case INFO:
                        handler.infoArrived( packet );
                        break;

                    default:
                        handler.unknownDataArrived( packet );
                }
            }
        }
        catch ( final EOFException e )
        {
            return;
        }
    }
}
