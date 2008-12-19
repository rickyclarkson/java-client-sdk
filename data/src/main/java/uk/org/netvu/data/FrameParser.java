package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.nio.BufferUnderflowException;

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
        void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer input, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            final ImageDataStruct imageHeader = ImageDataStruct.construct( input );
            IO.slice( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE, imageHeader.getStartOffset() );
            final ByteBuffer restOfData =
                    IO.from( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
            handler.mpeg4FrameArrived( Packet.constructPacket( channel, sourceIdentifier, restOfData, input ) );
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
        void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer input, final int channel, final Short xres,
                final Short yres ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            if ( xres == null || yres == null )
            {
                throw new IllegalStateException( "The stream does not contain an IMAGESIZE comment, "
                        + "so the MPEG-4 frames cannot be correctly parsed." );
            }

            handler.mpeg4FrameArrived( new Packet( channel, sourceIdentifier )
            {
                @Override
                public ByteBuffer getData()
                {
                  return IO.from( input, 6 );
                }

                @Override
                public ByteBuffer getOnDiskFormat()
                {
                    final boolean iFrame = MimeParser.isIFrame( IO.duplicate( input ) );
                    final VideoFormat videoFormat = iFrame ? VideoFormat.MPEG4_I_FRAME : VideoFormat.MPEG4_P_FRAME;
                    return ImageDataStruct.construct( input, "", videoFormat, yres, xres ).getByteBuffer();
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
        void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.infoArrived( Packet.constructPacket( channel, sourceIdentifier, data, data ) );
        }
    };
    /**
     * A FrameParser that can read data of an unknown or unsupported type.
     */
    private static final FrameParser UNKNOWN = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 ) throws IOException
        {
            CheckParameters.areNotNull( handler, data );
            handler.unknownDataArrived( Packet.constructPacket( channel, sourceIdentifier, data, data ) );
        }
    };

    /**
     * A FrameParser that can read truncated JFIF frames - JFIF frames without
     * their headers. This parser reconstructs those frames.
     */
    private static final FrameParser TRUNCATED_JFIF = new FrameParser()
    {
        @Override
        public void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer input, final int channel,
                final Short ignored2, final Short ignored3 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );

            handler.jpegFrameArrived( new Packet( channel, sourceIdentifier )
            {
                @Override
                public ByteBuffer getData()
                {
                    return JFIFHeader.jpegToJfif( input );
                }

                @Override
                public ByteBuffer getOnDiskFormat()
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
    private static final FrameParser JFIF = new FrameParser()
    {
        @Override
        void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer input, final int channel, final Short ignored2,
                final Short ignored3 ) throws IOException
        {
            CheckParameters.areNotNull( handler, input );
            handler.jpegFrameArrived( new Packet( channel, sourceIdentifier )
            {
                @Override
                public ByteBuffer getData()
                {
                  try
                    {
                      IO.searchFor( input, new byte[]{ (byte) 0xFF, (byte) 0xD8 } );
                      return IO.duplicate( input );
                    }
                  catch (BufferUnderflowException e)
                    {
                      ByteBuffer buffer = parseJfifOrJpeg(input).getData();
                      if ((buffer.get() & 0xFF) != 0xFF)
                        throw null;
                      buffer.position(0);
                      return buffer;
                    }
                }

                @Override
                public ByteBuffer getOnDiskFormat()
                {
                  return parseJfifOrJpeg( input ).getByteBuffer();
                }
              });
        }
    };

  private static ImageDataStruct parseJfifOrJpeg(ByteBuffer jfifOrJpegData)
  {
    final ByteBuffer data = IO.duplicate( jfifOrJpegData);
    try
    {
      final int ffd8 = IO.searchFor(data, new byte[]{(byte)0xFF, (byte)0xD8});
      if (ffd8 != 0)
        throw null;

      final int ffc0 = IO.searchFor(data, new byte[]{(byte)0xFF, (byte)0xC0});
      final int commentPosition = IO.searchFor(data, JFIFHeader.byteArrayLiteral(new int[]{0xFF, 0xFE}));
      data.position(commentPosition + 2);
      final int commentLength = data.getShort();
      final String comment = IO.bytesToString(IO.readIntoByteArray(data, commentLength));
      final VideoFormat videoFormat = data.get(ffc0 + 11) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411;
      final short targetPixels = data.getShort(ffc0 +5 );
      final short targetLines = data.getShort(ffc0+7);
      return ImageDataStruct.construct(data, comment, videoFormat, targetLines, targetPixels);
    }
    catch (BufferUnderflowException e)
      {
        final int decade = IO.searchFor(data, new byte[]{(byte)0xDE, (byte)0xCA, (byte)0xDE } );
        return parseJfifOrJpeg(JFIFHeader.jpegToJfif(IO.from(data, decade)));
      }
  }

    /**
     * A FrameParser that can parse ADPCM data.
     */
    private static final FrameParser ADPCM = new FrameParser()
    {
        @Override
        public void parse( final StreamHandler handler, final Object sourceIdentifier, final ByteBuffer data, final int channel, final Short ignored,
                final Short ignored2 )
        {
            CheckParameters.areNotNull( handler, data );
            handler.audioDataArrived( Packet.constructPacket( channel, sourceIdentifier, IO.from(data, AudioDataStruct.AUDIO_DATA_STRUCT_SIZE + 6), data ) );
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
        final HashMap<Integer, FrameParser> frameParsers = new HashMap<Integer, FrameParser>()
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
  abstract void parse( StreamHandler handler, Object sourceIdentifier, ByteBuffer data, int channel, Short xres, Short yres )
            throws IOException;
}
