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
abstract class FrameType
{
    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
  public static FrameType jpeg(final boolean truncated)
  {
    return new FrameType()
    {
      @Override
      void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final int ignored)
        throws IOException
      {
        CheckParameters.areNotNull( handler, input );
        handler.jpegFrameArrived( new JFIFPacket( input, channel, truncated ) );
      }
    };
  }

  public static final FrameType MPEG4 = new FrameType()
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final int frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, frameType );
            ImageDataStruct imageHeader = new ImageDataStruct( input );
            ByteBuffer commentData = IO.slice( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE, imageHeader.getStartOffset() );
            ByteBuffer restOfData = IO.from( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
            handler.mpeg4FrameArrived( new MPEG4Packet( restOfData, channel ) );
        }
    };

    /**
     * An MPEG-4 frame read from a minimal stream.  It does the same as MPEG4, but omits the ImageDataStruct and comment field.
     */
  public static final FrameType MPEG4_MINIMAL = new FrameType()
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer input, final int channel, final int length, final int frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, frameType );
            handler.mpeg4FrameArrived( new MPEG4Packet( input, channel ) );
        }
    };
    /**
     * Information (such as comments about the other data).
     */
  public static final FrameType INFO = new FrameType()
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel, final int length, final int frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, frameType );
            handler.infoArrived( new InfoPacket( data, channel, length ) );
        }
    };
    /**
     * Unknown data. This should not be seen in normal circumstances.
     */
  public static final FrameType UNKNOWN = new FrameType()
    {
        @Override
          void deliverTo( final StreamHandler handler, final ByteBuffer data, final int channel, final int length, final int frameType )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, frameType );
            handler.unknownDataArrived( new UnknownPacket( data, channel, length ) );
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
              return FrameType.jpeg(false);
            case 0:
              return FrameType.jpeg(true);
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
    abstract void deliverTo( StreamHandler handler, ByteBuffer data, int channel, int length, int frameType ) throws IOException;
}
