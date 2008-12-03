package uk.org.netvu.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.org.netvu.util.CheckParameters;

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
        this.buffer = buffer;

        int version = readInt( buffer, VERSION );

        if ( ( version & 0xDECADE00 ) != 0xDECADE00 )
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

    /**
     * Gets the alarmBitmask contained in the image data header.
     * 
     * @return the alarmBitmask contained in the image data header.
     */
    public int getAlarmBitmask()
    {
      return readInt( buffer, ALM_BITMASK );
    }

    /**
     * Gets the alarmBitmaskHigh contained in the image data header.
     * 
     * @return the alarmBitmaskHigh contained in the image data header.
     */
    public int getAlarmBitmaskHigh()
    {
      return readInt( buffer, ALM_BITMASK_HI);
    }

    /**
     * Gets the camera contained in the image data header.
     * 
     * @return the camera contained in the image data header.
     */
    public int getCamera()
    {
      return readInt( buffer, CAM);
    }

    /**
     * Gets the format contained in the image data header.
     * 
     * @return the format contained in the image data header.
     */
    public PictureStruct getFormat()
    {
      return new PictureStruct( buffer, ByteOrder.BIG_ENDIAN, FORMAT);
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

    /**
     * Gets the maxSize contained in the image data header.
     * 
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
      return readInt(buffer, MAX_SIZE);
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

    /**
     * Gets the mode contained in the image data header.
     * 
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
      return readInt(buffer, MODE);
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

    /**
     * Gets the res contained in the image data header.
     * 
     * @return the res contained in the image data header.
     */
    public byte[] getRes()
    {
      return read(buffer, 4, RES);
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

    /**
     * Gets the size contained in the image data header.
     * 
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
      return readInt(buffer, SIZE);
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

    /**
     * Gets the status contained in the image data header.
     * 
     * @return the status contained in the image data header.
     */
    public int getStatus()
    {
      return readInt(buffer, STATUS);
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

    /**
     * Gets the title contained in the image data header.
     * 
     * @return the title contained in the image data header.
     */
    public String getTitle()
    {
      return nullTerminate( read( buffer, TITLE_LENGTH, TITLE) );
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

    /**
     * Gets the version of the image data header.
     * 
     * @return the version of the image data header.
     */
    public int getVersion()
    {
      return readInt(buffer, VERSION);
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
