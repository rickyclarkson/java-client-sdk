package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import uk.org.netvu.util.CheckParameters;

abstract class FrameParser
{
    private static final FrameParser MPEG4 = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel,
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
    private static final FrameParser MPEG4_MINIMAL = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel,
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
    private static final FrameParser INFO = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer data, final int channel,
                final Short ignored, final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.infoArrived( new InfoPacket( data, channel ) );
        }
    };
    /**
     * Unknown data. This should not be seen in normal circumstances.
     */
    private static final FrameParser UNKNOWN = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer data, final int channel,
                final Short ignored, final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.unknownDataArrived( new UnknownPacket( data, channel ) );
        }
    };

  private static final FrameParser TRUNCATED_JFIF = new FrameParser()
    {
      public void parse( final StreamHandler handler, final ByteBuffer input, final int channel, final Short ignored2, final Short ignored3) throws IOException
      {
        CheckParameters.areNotNull(handler, input);
        
        handler.jpegFrameArrived(new Packet(channel)
          {
            public ByteBuffer getData()
            {
              return JFIFHeader.jpegToJfif(input);
            }

            public ByteBuffer getOnWireFormat()
            {
              return IO.duplicate(input);
            }
          });
      }
    };

    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
    private static FrameParser JFIF = new FrameParser()
      {
        @Override
        void parse( final StreamHandler handler, final ByteBuffer input, final int channel,
                    final Short ignored2, final Short ignored3 ) throws IOException
        {
          CheckParameters.areNotNull( handler, input );
          handler.jpegFrameArrived( new Packet( channel )
            {
              public ByteBuffer getData()
              {
                return IO.duplicate(input);
              }
              
              public ByteBuffer getOnWireFormat()
              {
                final int commentPosition = IO.searchFor(input, JFIFHeader.byteArrayLiteral( new int[]{ 0xFF, 0xFE } ));
                input.position(commentPosition + 2);
                final int commentLength = input.getShort();
                String comment = IO.bytesToString(IO.readIntoByteArray(input, commentLength));
                final VideoFormat videoFormat = input.get(IO.searchFor(input, JFIFHeader.byteArrayLiteral(new int[]{0xFF, 0xC0})) + 11) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
                final short targetPixels = input.getShort(IO.searchFor(input, new byte[]{(byte)0xFF, (byte)0xC0}) + 5);
                final short targetLines = input.getShort(IO.searchFor(input, new byte[]{(byte)0xFF, (byte)0xC0})+7);
                final ImageDataStruct imageDataStruct = ImageDataStruct.createImageDataStruct(input, comment, videoFormat, targetLines, targetPixels);
                return imageDataStruct.getByteBuffer();
              }
            });         
        }
      };

  private static final Map<Integer, FrameParser> frameParsers = new HashMap<Integer, FrameParser>()
  {
    {
      // This is an instance initialiser.
      put(0, TRUNCATED_JFIF);
      put(1, JFIF);
      put(2, MPEG4);
      put(3, MPEG4);
      put(4, ADPCM);
      put(6, MPEG4_MINIMAL);
      put(9, INFO);
    }
  };

    /**
     * Gives the type of frame, according to the numbers used in the minimal and
     * binary stream formats.
     * 
     * @param value
     *        the numeric value to find a matching FrameParser for.
     * @return the FrameParser that corresponds with value, or UNKNOWN if none
     *         exists.
     */
    static FrameParser frameParserFor( final int frameType )
    {
      return frameParsers.containsKey(frameType) ? frameParsers.get(frameType) : UNKNOWN;
    }

  private static final FrameParser ADPCM = new FrameParser()
    {
      public void parse( final StreamHandler handler, final ByteBuffer data, final int channel,
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
    abstract void parse( StreamHandler handler, ByteBuffer data, int channel, Short xres, Short yres ) throws IOException;
}
