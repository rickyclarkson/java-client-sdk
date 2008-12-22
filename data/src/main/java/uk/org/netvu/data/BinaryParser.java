package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.util.HashMap;

import uk.org.netvu.util.CheckParameters;

/**
 * A parser for the 'binary' stream format.
 */
final class BinaryParser implements Parser
{
  interface Function<T, U> { U apply(T t); }

  private ImageDataStruct parseJfifOrJpeg(ByteBuffer jfifOrJpegData)
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
   * @{inheritDoc}
   */
  public void parse( final InputStream input, final Object sourceIdentifier, final StreamHandler handler ) throws IOException
    {
        CheckParameters.areNotNull( input, handler );

        try
        {
            final Short[] horizontalResolution = { null };
            final Short[] verticalResolution = { null };

            while ( true )
            {
                final int frameTypeInt = input.read();

                /**
                 * Parsers for each of the supported data formats.
                 */
                abstract class FrameParser
                {
                  public final Effect<Packet> deliver;
                  public FrameParser(Effect<Packet> deliver)
                  {
                    this.deliver = deliver;
                  }

                  abstract Packet constructPacket( Object sourceIdentifier, ByteBuffer data, int channel );
                }

                  /**
                   * A FrameParser that can parse MPEG-4 frames that arrive complete with
                   * their ImageDataStruct.
                   */
                final FrameParser MPEG4 = new FrameParser( handler.mpeg4FrameArrived )
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer input, final int channel )
                      {
                        CheckParameters.areNotNull( handler, input );
                        final ImageDataStruct imageHeader = ImageDataStruct.construct( input );
                        IO.slice( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE, imageHeader.getStartOffset() );
                        final ByteBuffer restOfData =
                          IO.from( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + imageHeader.getStartOffset() );
                        return Packet.constructPacket( channel, sourceIdentifier, restOfData, input );
                      }
                    };
                  
                  /**
                   * A FrameParser that can read MPEG-4 frames read from a minimal stream. An
                   * MPEG-4 frame from a minimal stream is the same as from a binary stream,
                   * but without an ImageDataStruct and comment block.
                   */
                final FrameParser MPEG4_MINIMAL = new FrameParser( handler.mpeg4FrameArrived )
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer input, final int channel )
                      {
                        CheckParameters.areNotNull( handler, input );
                        if ( horizontalResolution[0] == null || verticalResolution[0] == null )
                          {
                            throw new IllegalStateException( "The stream does not contain an IMAGESIZE comment, "
                                                             + "so the MPEG-4 frames cannot be correctly parsed." );
                          }
                        
                        return new Packet( channel, sourceIdentifier )
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
                              return ImageDataStruct.construct( input, "", videoFormat, verticalResolution[0], horizontalResolution[0] ).getByteBuffer();
                            }
                        };
                      }
                    };
                  
                  /**
                   * A FrameParser that can read INFO blocks, which are ASCII text.
                   */
                  final FrameParser INFO = new FrameParser( handler.infoArrived )
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer data, final int channel )
                      {
                        return Packet.constructPacket( channel, sourceIdentifier, data, data );
                      }
                    };
                  /**
                   * A FrameParser that can read data of an unknown or unsupported type.
                   */
                  final FrameParser UNKNOWN = new FrameParser(handler.unknownDataArrived)
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer data, final int channel )
                      {
                        CheckParameters.areNotNull( handler, data );
                        return Packet.constructPacket( channel, sourceIdentifier, data, data );
                      }

                    };
                  
                  /**
                   * A FrameParser that can read truncated JFIF frames - JFIF frames without
                   * their headers. This parser reconstructs those frames.
                   */
                  final FrameParser TRUNCATED_JFIF = new FrameParser(handler.jpegFrameArrived)
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer input, final int channel )
                      {
                        return new Packet( channel, sourceIdentifier )
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
                          };
                      }                      
                    };
                  
                  /**
                   * A complete JFIF, compatible with most display and image manipulation
                   * programs.
                   */
                  final FrameParser JFIF = new FrameParser(handler.jpegFrameArrived)
                    {
                      @Override
                      Packet constructPacket( final Object sourceIdentifier, final ByteBuffer input, final int channel )
                      {
                        return new Packet( channel, sourceIdentifier )
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
                          };
                      }
                    };

                  /**
                   * A FrameParser that can parse ADPCM data.
                   */
                  final FrameParser ADPCM = new FrameParser( handler.audioDataArrived )
                    {
                      @Override
                      public Packet constructPacket( final Object sourceIdentifier, final ByteBuffer data, final int channel )
                      {
                        return Packet.constructPacket( channel, sourceIdentifier, IO.from(data, AudioDataStruct.AUDIO_DATA_STRUCT_SIZE + 6), data );
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
                  final Function<Integer, FrameParser> frameParserFor = new Function<Integer, FrameParser>() { public FrameParser apply(Integer frameType) { 
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
                    } };                                 
                

                  final FrameParser frameParser = frameParserFor.apply( frameTypeInt );

                final int channel = input.read() + 1;
                final int length = new DataInputStream( input ).readInt();
                final ByteBuffer data = IO.readIntoByteBuffer( input, length );
                data.position( 0 );

                // A minimal stream containing MPEG-4 data begins with an info
                // packet containing
                // "IMAGESIZE 0,0:123,456", where 123 and 456 are the
                // resolution of the frames. We parse that out and store it in
                // the horizontalResolution and verticalResolution arrays.
                // Arrays are used because of Java's 'final' restriction for
                // anonymous classes.
                // The type of it is Short rather than short to catch any
                // accidental misuses. This means that if the info
                // packet is missing, a minimal stream containing MPEG-4 will
                // not be parsed (a
                // NullPointerException will result).

                Packet packet = frameParser.constructPacket(sourceIdentifier, IO.duplicate(data), channel);

                if (frameTypeInt == 9)
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
                        horizontalResolution[0] = Short.parseShort( theRest.substring( 0, comma ) );
                        verticalResolution[0] = Short.parseShort( theRest.substring( comma + 1, semi ) );
                      }
                    else
                      {
                        handler.infoArrived(packet);
                      }
                  }

                frameParser.deliver.apply( frameParser.constructPacket( sourceIdentifier, IO.duplicate( data ), channel ) );
            }
        }
        catch ( final EOFException e )
          {
            return;
          }
    }
}
