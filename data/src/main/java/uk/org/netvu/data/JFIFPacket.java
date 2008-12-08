package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * A JFIFPacket represents a single JFIF frame read from a stream.  The JFIF data is available as a ByteBuffer via the method getData().
 */
final class JFIFPacket extends Packet
{
  /**
   * The JFIF frame.
   */
  private final ByteBuffer data;
  private final boolean truncated;

  /**
   * Constructs a JFIFPacket with the specified data and metadata.
   * @param data the JFIF frame to store.
   * @param metadata the metadata about the JFIFPacket.
   * @throws NullPointerException if data or metadata are null.
   */
  JFIFPacket(ByteBuffer data, int channel, boolean truncated )
  {
    super(channel);
    this.truncated = truncated;

    CheckParameters.areNotNull(data);
    this.data = data;
  }

  /**
   * Gets the JFIF frame held by the JFIFPacket.
   *
   * @return the JFIF frame held by the JFIFPacket.
   */
  public ByteBuffer getData()
  {
    return truncated ? JFIFHeader.jpegToJfif(data) : data;
  }

  public ByteBuffer getOnWireFormat()
  {
    if (truncated)
      return data;

    int commentPosition = IO.searchFor(data,JFIFHeader.byteArrayLiteral(new int[]{ 0xFF, 0xFE }) );
    data.position(commentPosition + 2);
    int commentLength = data.getShort();
    String comment;

    try
      { comment = new String(IO.readIntoByteArray(data, commentLength), "US-ASCII");
      } catch (UnsupportedEncodingException e) { throw new RuntimeException(e);
    }
    
    ImageDataStruct imageDataStruct = new ImageDataStruct(ByteBuffer.allocate(ImageDataStruct.IMAGE_DATA_STRUCT_SIZE).putInt(0xDECADE11));

    final int modeChosenByReadingGenericVideoHeader = 2;
    imageDataStruct.setMode(modeChosenByReadingGenericVideoHeader);

    imageDataStruct.setCamera(IO.findInt(comment, "Number: "));
    imageDataStruct.setVideoFormat(data.get(IO.searchFor(data, JFIFHeader.byteArrayLiteral(new int[]{ 0xFF, 0xC0}))+11) == 0x22 ? VideoFormat.JPEG_422 : VideoFormat.JPEG_411);
    imageDataStruct.setStartOffset(commentLength);
    imageDataStruct.setSize(data.limit());

    final int maxSizeChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setMaxSize(maxSizeChosenByReadingGenericVideoHeader);

    final int targetSizeChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setTargetSize(targetSizeChosenByReadingGenericVideoHeader);

    final int qFactorChosenByReadingGenericVideoHeader = -1;
    imageDataStruct.setQFactor(qFactorChosenByReadingGenericVideoHeader);

    final int alarmBitmaskHighChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setAlarmBitmaskHigh(alarmBitmaskHighChosenByReadingGenericVideoHeader);

    final int statusChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setStatus(statusChosenByReadingGenericVideoHeader);

    try
    {
      imageDataStruct.setSessionTime((int)(JFIFHeader.getDateFormat().parse(IO.find(comment, "Date: ")).getTime() + JFIFHeader.getTimeFormat().parse(IO.find(comment, "Time: ")).getTime()));
    }
    catch (ParseException e) { throw new RuntimeException(e); }

    imageDataStruct.setMilliseconds(IO.findInt(comment, "MSec: "));

    final String resChosenByReadingGenericVideoHeader = "";
    imageDataStruct.setRes(resChosenByReadingGenericVideoHeader);

    imageDataStruct.setTitle(IO.find(comment, "Name: "));
    imageDataStruct.setAlarm(IO.find(comment, "Alarm-text: "));

    final short srcPixelsChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setSrcPixels(srcPixelsChosenByReadingGenericVideoHeader);

    final short srcLinesChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setSrcLines(srcLinesChosenByReadingGenericVideoHeader);

    imageDataStruct.setTargetPixels(data.getShort(IO.searchFor(data, new byte[]{ (byte)0xFF, (byte)0xC0 }) + 5));
    imageDataStruct.setTargetLines(data.getShort(IO.searchFor(data, new byte[]{ (byte)0xFF, (byte)0xC0 }) + 7));

    final short pixelOffsetChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setPixelOffset(pixelOffsetChosenByReadingGenericVideoHeader);

    final short lineOffsetChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setLineOffset(lineOffsetChosenByReadingGenericVideoHeader);

    imageDataStruct.setLocale(IO.find(comment, "Locale: "));
    imageDataStruct.setUtcOffset(IO.findInt(comment, "UTCoffset: "));

    final int alarmBitmaskChosenByReadingGenericVideoHeader = 0;
    imageDataStruct.setAlarmBitmask(alarmBitmaskChosenByReadingGenericVideoHeader);

    return imageDataStruct.getByteBuffer();
  }
}