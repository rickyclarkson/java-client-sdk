package uk.org.netvu.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.org.netvu.util.CheckParameters;
import java.io.UnsupportedEncodingException;

/**
 * A representation of the 'image data' part of a minimised-JFIF.
 */
public final class ImageDataStruct
{
    /**
     * The size of an int, in bytes.
     */
    private static final int INT = 4;

    /**
     * The size of a timestamp, in bytes.
     */
    private static final int TIME_SIZE = 4;

    /**
     * The length of the title (the title may be smaller than this and
     * null-terminated, but the number of bytes used is constant).
     */
    static final int TITLE_LENGTH = 31;

    /**
     * The length of the name (the name may be smaller than this and
     * null-terminated, but the number of bytes used is constant).
     */
    static final int MAX_NAME_LENGTH = 30;

    /**
     * The position of the VERSION field in the image data.
     */
    private static final int VERSION = 0;

    /**
     * The position of the MODE field in the image data.
     */
    private static final int MODE = VERSION + INT;

    /**
     * The position of the CAM field in the image data.
     */
    private static final int CAM = MODE + INT;

    /**
     * The position of the VID_FORMAT field in the image data.
     */
    private static final int VID_FORMAT = CAM + INT;

    /**
     * The position of the START_OFFSET field in the image data.
     */
    private static final int START_OFFSET = VID_FORMAT + INT;

    /**
     * The position of the SIZE field in the image data.
     */
    private static final int SIZE = START_OFFSET + INT;

    /**
     * The position of the MAX_SIZE field in the image data.
     */
    private static final int MAX_SIZE = SIZE + INT;

    /**
     * The position of the TARGET_SIZE field in the image data.
     */
    private static final int TARGET_SIZE = MAX_SIZE + INT;

    /**
     * The position of the Q_FACTOR field in the image data.
     */
    private static final int Q_FACTOR = TARGET_SIZE + INT;

    /**
     * The position of the ALM_BITMASK_HI field in the image data.
     */
    private static final int ALM_BITMASK_HI = Q_FACTOR + INT;

    /**
     * The position of the STATUS field in the image data.
     */
    private static final int STATUS = ALM_BITMASK_HI + INT;

    /**
     * The position of the SESSION_TIME field in the image data.
     */
    private static final int SESSION_TIME = STATUS + INT;

    /**
     * The position of the MILLISECONDS field in the image data.
     */
    private static final int MILLISECONDS = SESSION_TIME + TIME_SIZE;

    /**
     * The position of the RES field in the image data.
     */
    private static final int RES = MILLISECONDS + INT;

    /**
     * The position of the TITLE field in the image data.
     */
    private static final int TITLE = RES + 4;

    /**
     * The position of the ALARM field in the image data.
     */
    private static final int ALARM = TITLE + TITLE_LENGTH;

    /**
     * The position of the FORMAT field in the image data.
     */
    private static final int FORMAT = ALARM + TITLE_LENGTH;

    /**
     * The position of the LOCALE field in the image data.
     */
    private static final int LOCALE = FORMAT + PictureStruct.SIZE;

    /**
     * The position of the UTC_OFFSET field in the image data.
     */
    private static final int UTC_OFFSET = LOCALE + MAX_NAME_LENGTH;

    /**
     * The position of the ALM_BITMASK field in the image data.
     */
    private static final int ALM_BITMASK = UTC_OFFSET + INT;

    /**
     * The size of an image data block in bytes.
     */
    public static final int IMAGE_DATA_STRUCT_SIZE = ALM_BITMASK + INT;

    /**
     * Reads an ASCII String from the specified byte[], stopping when a 0 byte
     * is found or the end of the array is reached.
     * 
     * @param input
     *        the byte[] to read from.
     * @return an ASCII String.
     * @throws NullPointerException
     *         if input is null.
     */
    private static String nullTerminate( final byte[] input )
    {
        CheckParameters.areNotNull( input );
        try
        {
            return nullTerminate( new String( input, "US-ASCII" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Takes the part of the specified String before its first '\0', or the
     * whole String if none is found.
     * 
     * @param input
     *        the String to read from.
     * @return a String without '\0' characters.
     * @throws NullPointerException
     *         if input is null.
     */
    private static String nullTerminate( final String input )
    {
        CheckParameters.areNotNull( input );
        final int indexOfNull = input.indexOf( '\0' );
        return indexOfNull == -1 ? input : input.substring( 0, indexOfNull );
    }

  private final ByteBuffer buffer;

    /**
     * Constructs an ImageDataStruct with data from the specified ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to read data from.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public ImageDataStruct( final ByteBuffer buffer )
    {
        CheckParameters.areNotNull( buffer );
        this.buffer = buffer.duplicate();
        
        int version = readInt( this.buffer, VERSION );
        
        if (version != 0xDECADE10 && version != 0xDECADE11)
        {
            throw new IllegalArgumentException( "version is " + Integer.toHexString( version ) );
        }
    }

    /**
     * Gets the alarm contained in the image data header.
     * 
     * @return the alarm contained in the image data header.
     */
    public String getAlarm()
    {
      return nullTerminate( read( buffer, TITLE_LENGTH, ALARM) );
    }

  public void setAlarm(String s)
  {
    write( nullPad(s, TITLE_LENGTH), ALARM );
  }

  void write( byte[] bytes, int position)
  {
    buffer.position(position);
    buffer.put(bytes);
  }

  byte[] nullPad(String string, int length)
  {
    try
    {
      byte[] bytes = new byte[length];
      System.arraycopy(string.getBytes("US-ASCII"), 0, bytes, 0, string.length());
      return bytes;
    }
    catch (UnsupportedEncodingException e)
      {
        throw new RuntimeException(e);
      }
  }

    /**
     * Gets the alarmBitmask contained in the image data header.
     * 
     * @return the alarmBitmask contained in the image data header.
     */
    public int getAlarmBitmask()
    {
      return readInt( buffer, ALM_BITMASK );
    }

  public void setAlarmBitmask(int alarmBitmask) { buffer.putInt(ALM_BITMASK, alarmBitmask); }

    /**
     * Gets the alarmBitmaskHigh contained in the image data header.
     * 
     * @return the alarmBitmaskHigh contained in the image data header.
     */
    public int getAlarmBitmaskHigh()
    {
      return readInt( buffer, ALM_BITMASK_HI);
    }

  public void setAlarmBitmaskHigh(int alarmBitmaskHigh) { buffer.putInt(ALM_BITMASK_HI, alarmBitmaskHigh); }

    /**
     * Gets the camera contained in the image data header.
     * 
     * @return the camera contained in the image data header.
     */
    public int getCamera()
    {
      return readInt( buffer, CAM);
    }

  public void setCamera(int camera) { buffer.putInt(CAM, camera); }

  private PictureStruct pic()
  {
    return new PictureStruct();
  }

  public short getLineOffset()
  {
    return pic().getLineOffset();
  }

  public short getPixelOffset()
  {
    return pic().getPixelOffset();
  }

  public short getTargetLines()
  {
    return pic().getTargetLines();
  }

  public short getTargetPixels()
  {
    return pic().getTargetPixels();
  }

  public short getSrcLines()
  {
    return pic().getSrcLines();
  }

  public short getSrcPixels()
  {
    return pic().getSrcPixels();
  }

  public void setSrcPixels(short srcPixels)
  {
    pic().setSrcPixels(srcPixels);
  }

  public void setSrcLines(short srcLines)
  {
    pic().setSrcLines(srcLines);
  }

  public void setTargetPixels(short targetPixels)
  {
    pic().setTargetPixels(targetPixels);
  }

  public void setTargetLines(short targetLines)
  {
    pic().setTargetLines(targetLines);
  }

  public void setPixelOffset(short pixelOffset)
  {
    pic().setPixelOffset(pixelOffset);
  }

  public void setLineOffset(short lineOffset)
  {
    pic().setLineOffset(lineOffset);
  }

  private ByteOrder versionToByteOrder()
  {
    return getVersion() == 0xDECADE10 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }

  
  /**
   * A representation of the 'picture' part of the ImageDataStruct.
   */
  private final class PictureStruct
  {
    /**
     * The size of a short, in bytes.
     */
    private static final int INT16 = 2;
    
    /**
     * The position of the SRC_PIXELS field in the picture struct.
     */
    private static final int SRC_PIXELS = 0;
    
    /**
     * The position of the SRC_LINES field in the picture struct.
     */
    private static final int SRC_LINES = SRC_PIXELS + INT16;
    
    /**
     * The position of the TARGET_PIXELS field in the picture struct.
     */
    private static final int TARGET_PIXELS = SRC_LINES + INT16;
    
    /**
     * The position of the TARGET_LINES field in the picture struct.
     */
    private static final int TARGET_LINES = TARGET_PIXELS + INT16;
    
    /**
     * The position of the PIXEL_OFFSET field in the picture struct.
     */
    private static final int PIXEL_OFFSET = TARGET_LINES + INT16;
    
    /**
     * The position of the LINE_OFFSET field in the picture struct.
     */
    private static final int LINE_OFFSET = PIXEL_OFFSET + INT16;
    
    /**
     * The size of the picture struct in bytes.
     */
    public static final int SIZE = LINE_OFFSET + INT16;
    
    /**
     * Gets the line offset of this PictureStruct. The line offset parameter is
     * unused as of this writing, but it originally meant the y position in the
     * camera's original image that the frame of data began at.
     * 
     * @return the line offset of this PictureStruct.
     */
    public short getLineOffset()
    {
      return readShort( LINE_OFFSET );
    }
    
    public void setLineOffset(short lineOffset)
    {
      writeShort( LINE_OFFSET, lineOffset );
    }
    
    /**
     * Gets the pixel offset of this PictureStruct. The pixel offset parameter
     * is unused as of this writing, but it originally meant the x position in
     * the camera's original image that the frame of data began at.
     * 
     * @return the pixel offset of this PictureStruct.
     */
    public short getPixelOffset()
    {
      return readShort( PIXEL_OFFSET );
    }
    
    public void setPixelOffset(short pixelOffset)
    {
      writeShort( PIXEL_OFFSET, pixelOffset);
    }
    
    /**
     * Gets the source lines of this PictureStruct. The source lines parameter
     * is unused as of this writing, but it originally meant the height of the
     * camera's original image.
     * 
     * @return the source lines of this PictureStruct.
     */
    public short getSrcLines()
    {
      return readShort( SRC_LINES );
    }
    
  public void setSrcLines(short srcLines)
    {
      writeShort( SRC_LINES, srcLines);
    }
    
    /**
     * Gets the source pixels of this PictureStruct. The source pixels parameter
     * is unused as of this writing, but it originally meant the width of the
     * camera's original image.
     * 
     * @return the source pixels of this PictureStruct.
     */
    public short getSrcPixels()
    {
      return readShort( SRC_PIXELS );
    }
    
    public void setSrcPixels(short srcPixels)
    {
      writeShort(SRC_PIXELS, srcPixels);
    }
    
    /**
     * Gets the target lines (the picture height) of this PictureStruct.
     * 
     * @return the target lines of this PictureStruct.
     */
    public short getTargetLines()
    {
      return readShort( TARGET_LINES );
    }
    
    public void setTargetLines(short targetLines)
    {
      writeShort(TARGET_LINES, targetLines);
    }
    
    /**
     * Gets the target pixels (the picture width) of this PictureStruct.
     * 
     * @return the target pixels of this PictureStruct.
     */
    public short getTargetPixels()
    {
      return readShort( TARGET_PIXELS );
    }
    
    public void setTargetPixels(short targetPixels)
    {
      writeShort( TARGET_PIXELS, targetPixels);
    }
    
    /**
     * Reads a little-endian short from the specified position within the
     * ByteBuffer relative to the offset.
     * 
     * @param where
     *        the position within the ByteBuffer relative to the offset to read
     *        from.
     * @return a short read from the ByteBuffer.
     */
    private short readShort( final int where )
    {
      buffer.order( versionToByteOrder() );
      try
      {
        return buffer.getShort( FORMAT + where );
      }
      finally
      {
        buffer.order( ByteOrder.BIG_ENDIAN );
      }
      
    }
    
    private void writeShort(final int position, final short value)
    {
      buffer.order(versionToByteOrder());
      try
      {
        buffer.putShort( FORMAT + position, value );
      }
      finally
      {
        buffer.order(ByteOrder.BIG_ENDIAN);
      }
    }
  }
  
  /**
     * Gets the locale contained in the image data header.
     * 
     * @return the locale contained in the image data header.
     */
    public String getLocale()
    {
      return nullTerminate( read( buffer, MAX_NAME_LENGTH, LOCALE));
    }

  public void setLocale(String locale)
  {
    write( nullPad(locale, MAX_NAME_LENGTH), LOCALE);
  }

    /**
     * Gets the maxSize contained in the image data header.
     * 
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
      return readInt(buffer, MAX_SIZE);
    }

  public void setMaxSize(int maxSize)
  {
    buffer.putInt(MAX_SIZE, maxSize);
  }

    /**
     * Gets the milliseconds contained in the image data header.
     * 
     * @return the milliseconds contained in the image data header.
     */
    public int getMilliseconds()
    {
      return (int)((0xFFFFFFFFL & readInt(buffer, MILLISECONDS)) % 1000L);
    }

  public void setMilliseconds(int milliseconds)
  {
    buffer.putInt(MILLISECONDS, milliseconds);
  }

    /**
     * Gets the mode contained in the image data header.
     * 
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
      return readInt(buffer, MODE);
    }

  public void setMode(int mode)
  {
    buffer.putInt(MODE, mode);
  }

    /**
     * Gets the qFactor contained in the image data header.
     * 
     * @return the qFactor contained in the image data header.
     */
    public int getQFactor()
    {
      return readInt(buffer, Q_FACTOR);
    }

  public void setQFactor(int qFactor)
  {
    buffer.putInt(Q_FACTOR, qFactor);
  }


    /**
     * Gets the res contained in the image data header.
     * 
     * @return the res contained in the image data header.
     */
    public String getRes()
    {
      String res = nullTerminate(read(buffer, 4, RES));
      return res.length() > 4 ? res.substring(0, 4) : res;
    }

  public void setRes(String res)
  {
    write( nullPad(res, 4), RES);
  }

    /**
     * Gets the sessionTime contained in the image data header.
     * 
     * @return the sessionTime contained in the image data header.
     */
    public int getSessionTime()
    {
      return readInt(buffer, SESSION_TIME);
    }

  public void setSessionTime(int sessionTime)
  {
    buffer.putInt(SESSION_TIME, sessionTime);
  }

    /**
     * Gets the size contained in the image data header.
     * 
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
      return readInt(buffer, SIZE);
    }

  public void setSize(int size)
  {
    buffer.putInt(SIZE, size);
  }

    /**
     * Gets the startOffset contained in the image data header.
     * 
     * @return the startOffset contained in the image data header.
     */
    public int getStartOffset()
    {
      return readInt(buffer, START_OFFSET);
    }

  public void setStartOffset(int startOffset)
  {
    buffer.putInt(START_OFFSET, startOffset);
  }

    /**
     * Gets the status contained in the image data header.
     * 
     * @return the status contained in the image data header.
     */
    public int getStatus()
    {
      return readInt(buffer, STATUS);
    }

  public void setStatus(int status)
  {
    buffer.putInt(STATUS, status);
  }

    /**
     * Gets the targetSize contained in the image data header.
     * 
     * @return the targetSize contained in the image data header.
     */
    public int getTargetSize()
    {
      return readInt(buffer, TARGET_SIZE);
    }

  public void setTargetSize(int targetSize)
  {
    buffer.putInt(TARGET_SIZE, targetSize);
  }

    /**
     * Gets the title contained in the image data header.
     * 
     * @return the title contained in the image data header.
     */
    public String getTitle()
    {
      return nullTerminate( read( buffer, TITLE_LENGTH, TITLE) );
    }

  public void setTitle(String title)
  {
    write( nullPad( title, TITLE_LENGTH), TITLE);
  }

    /**
     * Gets the utcOffset contained in the image data header.
     * 
     * @return the utcOffset contained in the image data header.
     */
    public int getUtcOffset()
    {
      return readInt(buffer, UTC_OFFSET);
    }

  public void setUtcOffset(int utcOffset)
  {
    buffer.putInt(UTC_OFFSET, utcOffset);
  }

    /**
     * Gets the version of the image data header.
     * 
     * @return the version of the image data header.
     */
    public int getVersion()
    {
      return readInt(buffer, VERSION);
    }

  public void setVersion(int version)
  {
    buffer.putInt(VERSION, version);
  }

    /**
     * Gets the videoFormat contained in the image data header.
     * 
     * @return the videoFormat contained in the image data header.
     */
    public VideoFormat getVideoFormat()
    {
      return VideoFormat.valueOf(readInt(buffer, VID_FORMAT));
    }

  public void setVideoFormat(VideoFormat format)
  {
    buffer.putInt(VID_FORMAT, format.index);
  }

    /**
     * Reads the specified number of bytes from the specified ByteBuffer, from
     * the specified position.
     * 
     * @param buffer
     *        the ByteBuffer to read data from.
     * @param length
     *        the number of bytes to read.
     * @param where
     *        the position in the ByteBuffer to read from.
     * @return an array of bytes containing the specified number of bytes read
     *         from the specified ByteBuffer, at the specified position.
     * @throws NullPointerException
     *         if buffer is null.
     */
    private byte[] read( final ByteBuffer buffer, final int length, final int where )
    {
        CheckParameters.areNotNull( buffer );
        final byte[] result = new byte[length];
        buffer.position( where );
        buffer.get( result );
        return result;
    }

    /**
     * Reads a big-endian int from the specified position in the specified
     * ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to read an int from.
     * @param where
     *        the position to read from.
     * @return a big-endian int read from the specified position in the
     *         specified ByteBuffer.
     * @throws NullPointerException
     *         if buffer is null.
     */
    private int readInt( final ByteBuffer buffer, final int where )
    {
        CheckParameters.areNotNull( buffer );
        buffer.order( ByteOrder.BIG_ENDIAN );
        return buffer.getInt( where );
    }
}
