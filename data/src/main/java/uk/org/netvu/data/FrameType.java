package uk.org.netvu.data;

import static uk.org.netvu.data.ImageDataStruct.IMAGE_DATA_STRUCT_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * An enumeration of the supported frame types from binary, minimal and mime
 * streams.
 */
enum FrameType
{
    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
    JFIF
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final FrameType frameType )
                throws IOException
        {
          if (frameType != JFIF)
            throw null;

            CheckParameters.areNotNull( handler, input, frameType );
            handler.jfif( new JFIFPacket( input, channel, length, frameType ) );
        }
    },
    JPEG
    {
      @Override
        void deliverTo(final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final FrameType frameType)
        {
          if (input.limit() != length) throw null;
          if (input.position() != 0) throw null;
          if (frameType != JPEG) throw null;

          CheckParameters.areNotNull( handler, input, frameType );
          handler.jfif( new JFIFPacket( input, channel, length, frameType ) );
        }
    },
    MPEG4
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final FrameType frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, frameType );
            ImageDataStruct imageHeader = new ImageDataStruct( input );
            ByteBuffer commentData = IO.slice( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE, imageHeader.getStartOffset() );
            ByteBuffer restOfData = IO.from( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
            handler.mpeg4( new MPEG4Packet( restOfData, channel, length, frameType, imageHeader, commentData ) );
        }
    },
    /**
     * An MPEG-4 frame read from a minimal stream.  It does the same as MPEG4, but omits the ImageDataStruct and comment field.
     */
    MPEG4_MINIMAL
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final FrameType frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, frameType );
            handler.mpeg4( new MPEG4Packet( input, length, channel, frameType, null, null ) );
        }
    },
    /**
     * Information (such as comments about the other data).
     */
    INFO
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel, final int length, final FrameType frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, frameType );
            handler.info( new InfoPacket( data, channel, length, frameType ) );
        }
    },
    /**
     * Unknown data. This should not be seen in normal circumstances.
     */
    UNKNOWN
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel, final int length, final FrameType frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, frameType );
            handler.dataArrived( new UnknownPacket( data, channel, length, frameType ) );
        }
    };

    /**
     * Gives the type of frame, according to the numbers used in the minimal and
     * binary stream formats.
     * 
     * @param value
     *        the numeric value to find a matching FrameType for.
     * @return the FrameType that corresponds with value, or UNKNOWN if none
     *         exists.
     */
    static FrameType frameTypeFor( final int value )
    {
        switch ( value )
        {
            case 1:
                return FrameType.JFIF;
            case 0:
                return FrameType.JPEG;
            case 2:
            case 3:
                return FrameType.MPEG4;
            case 6:
                return FrameType.MPEG4_MINIMAL;
            case 9:
                return FrameType.INFO;
            default:
                return FrameType.UNKNOWN;
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
    abstract void deliverTo( StreamHandler handler, ByteBuffer data, int channel, int length, FrameType frameType ) throws IOException;
}
