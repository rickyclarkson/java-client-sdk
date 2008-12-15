package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import uk.org.netvu.util.CheckParameters;

/**
 * Parsers for each of the supported data formats.
 */
abstract class FrameParser
{
    /**
     * A FrameParser that can parse MPEG-4 frames that arrive complete with
     * their ImageDataStruct.
     */
    private static final FrameParser MPEG4 = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            final ImageDataStruct imageHeader = new ImageDataStruct( input );
            IO.slice( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE, imageHeader.getStartOffset() );
            final ByteBuffer restOfData =
                    IO.from( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
            handler.mpeg4FrameArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return IO.duplicate( restOfData );
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    return IO.duplicate( input );
                }
            } );
        }
    };

    /**
     * A FrameParser that can read MPEG-4 frames read from a minimal stream. An
     * MPEG-4 frame from a minimal stream is the same as from a binary stream,
     * but without an ImageDataStruct and comment block.
     */
    private static final FrameParser MPEG4_MINIMAL = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel, final Short xres,
                final Short yres ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            handler.mpeg4FrameArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return IO.duplicate( input );
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    final boolean iFrame = MimeParser.isIFrame( IO.duplicate( input ) );
                    final VideoFormat videoFormat = iFrame ? VideoFormat.MPEG4_I_FRAME : VideoFormat.MPEG4_P_FRAME;
                    return ImageDataStruct.createImageDataStruct( input, "", videoFormat, yres, xres ).getByteBuffer();
                }
            } );
        }
    };

    /**
     * A FrameParser that can read INFO blocks, which are ASCII text.
     */
    private static final FrameParser INFO = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.infoArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return IO.duplicate( data );
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    return IO.duplicate( data );
                }
            } );
        }
    };
    /**
     * A FrameParser that can read data of an unknown or unsupported type.
     */
    private static final FrameParser UNKNOWN = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.unknownDataArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return data;
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    return data;
                }
            } );
        }
    };

    /**
     * A FrameParser that can read truncated JFIF frames - JFIF frames without
     * their headers. This parser reconstructs those frames.
     */
    private static final FrameParser TRUNCATED_JFIF = new FrameParser()
    {
        @Override
        public void parse( final StreamHandler handler, final ByteBuffer input, final int channel,
                final Short ignored2, final Short ignored3 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );

            handler.jpegFrameArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return JFIFHeader.jpegToJfif( input );
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    return IO.duplicate( input );
                }
            } );
        }
    };

    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
    private static FrameParser JFIF = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel, final Short ignored2,
                final Short ignored3 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            handler.jpegFrameArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return IO.duplicate( input );
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    final int commentPosition =
                            IO.searchFor( input, JFIFHeader.byteArrayLiteral( new int[] { 0xFF, 0xFE } ) );
                    input.position( commentPosition + 2 );
                    final int commentLength = input.getShort();
                    final String comment = IO.bytesToString( IO.readIntoByteArray( input, commentLength ) );
                    final int ffc0 = IO.searchFor( input, new byte[] { (byte) 0xFF, (byte) 0xC0 } );

                    final VideoFormat videoFormat =
                            input.get( ffc0 + 11 ) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
                    final short targetPixels = input.getShort( ffc0 + 5 );
                    final short targetLines = input.getShort( ffc0 + 7 );

                    final ImageDataStruct imageDataStruct =
                            ImageDataStruct.createImageDataStruct( input, comment, videoFormat, targetLines,
                                    targetPixels );
                    return imageDataStruct.getByteBuffer();
                }
            } );
        }
    };

    /**
     * Maps the ints found in the stream formats to FrameParsers to be used for
     * parsing the data with.
     */
    private static final Map<Integer, FrameParser> frameParsers = new HashMap<Integer, FrameParser>()
    {
        {
            // This is an instance initialiser.
            put( 0, TRUNCATED_JFIF );
            put( 1, JFIF );
            put( 2, MPEG4 );
            put( 3, MPEG4 );
            put( 4, ADPCM );
            put( 6, MPEG4_MINIMAL );
            put( 9, INFO );
        }
    };

    /**
     * A FrameParser that can parse ADPCM data.
     */
    private static final FrameParser ADPCM = new FrameParser()
    {
        @Override
        public void parse( final StreamHandler handler, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 )
        {
            CheckParameters.areNotNull( handler, data );
            handler.audioDataArrived( new Packet( channel )
            {
                @Override
                public ByteBuffer getData()
                {
                    return data;
                }

                @Override
                public ByteBuffer getOnWireFormat()
                {
                    return data;
                }
            } );
        }
    };

    /**
     * Gives the type of frame, according to the numbers used in the minimal and
     * binary stream formats.
     * 
     * @param frameType
     *        the numeric value to find a matching FrameParser for.
     * @return the FrameParser that corresponds with value, or UNKNOWN if none
     *         exists.
     */
    static FrameParser frameParserFor( final int frameType )
    {
        return frameParsers.containsKey( frameType ) ? frameParsers.get( frameType ) : UNKNOWN;
    }

    /**
     * Delivers a frame of this type of data to the passed-in StreamHandler.
     * 
     * @param handler
     *        the StreamHandler to deliver data to.
     * @param data
     *        the InputStream to read data from.
     * @param channel
     *        the channel, or camera number, that the data arrived on.
     * @param xres
     *        the horizontal resolution, if known - only used for MPEG-4 frames
     *        read from a minimal stream. It is null if not known.
     * @param yres
     *        the vertical resolution, if known - only used for MPEG-4 frames
     *        read from a minimal stream. It is null if not known.
     * @throws IOException
     *         if there are any I/O errors.
     * @throws NullPointerException
     *         if handler or data are null.
     */
    abstract void parse( StreamHandler handler, ByteBuffer data, int channel, Short xres, Short yres )
            throws IOException;
}
