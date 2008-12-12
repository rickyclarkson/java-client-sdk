package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

abstract class FrameParser
{
    public static final FrameParser MPEG4 = new FrameParser()
    {
        @Override
        void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel,
                final Short ignored, final Short ignored2 ) throws IOException
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
     * An MPEG-4 frame read from a minimal stream.  An MPEG-4 frame from a minimal stream is the same as from a binary stream, but without an ImageDataStruct and comment block.
     */
    public static final FrameParser MPEG4_MINIMAL = new FrameParser()
    {
        @Override
        void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel,
                final Short xres, final Short yres ) throws IOException
        {
            CheckParameters.areNotNull( handler, input);
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
                    // TODO implement choosing between frame types. The frame
                    // type is in the stream header.

                    return ImageDataStruct.createImageDataStruct( input, "", VideoFormat.MPEG4_P_FRAME, xres, yres ).getByteBuffer();
                }
            } );
        }
    };

    /**
     * Information (such as comments about the other data).
     */
    public static final FrameParser INFO = new FrameParser()
    {
        @Override
        void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel,
                final Short ignored, final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.infoArrived( new InfoPacket( data, channel ) );
        }
    };
    /**
     * Unknown data. This should not be seen in normal circumstances.
     */
    public static final FrameParser UNKNOWN = new FrameParser()
    {
        @Override
        void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel,
                final Short ignored, final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.unknownDataArrived( new UnknownPacket( data, channel ) );
        }
    };

    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
    public static FrameParser jpeg( final boolean truncated )
    {
        return new FrameParser()
        {
            @Override
            void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel,
                    final Short ignored2, final Short ignored3 ) throws IOException
            {
                CheckParameters.areNotNull( handler, input );
                handler.jpegFrameArrived( new JFIFPacket( input, channel, truncated ) );
            }
        };
    }

    /**
     * Gives the type of frame, according to the numbers used in the minimal and
     * binary stream formats.
     * 
     * @param value
     *        the numeric value to find a matching FrameParser for.
     * @return the FrameParser that corresponds with value, or UNKNOWN if none
     *         exists.
     */
    static FrameParser frameTypeFor( final int value )
    {
        switch ( value )
        {
            case 1:
                return FrameParser.jpeg( false );
            case 0:
                return FrameParser.jpeg( true );
            case 2:
            case 3:
                return FrameParser.MPEG4;
            case 4:
                return new FrameParser()
                {
                    @Override
                    public void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel,
                            final Short ignored, final Short ignored2 )
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
            case 6:
                return FrameParser.MPEG4_MINIMAL;
            case 9:
                return FrameParser.INFO;
            default:
                return FrameParser.UNKNOWN;
        }
    }

    /**
     * Delivers a frame of this type of data to the passed-in StreamHandler.
     * 
     * @param handler
     *        the StreamHandler to deliver data to.
     * @param data
     *        the InputStream to read data from.
     * @param metadata
     *        information about the packet of data.
     * @throws IOException
     *         if there are any I/O errors.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    abstract void deliverTo( StreamHandler handler, ByteBuffer data, int channel, Short xres, Short yres ) throws IOException;
}
