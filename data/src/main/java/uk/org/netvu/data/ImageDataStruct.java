package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.UnsupportedEncodingException;
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
     * The length of the title (the title may be smaller than this and null-terminated, but the number of bytes used is constant).
     */
    private static final int TITLE_LENGTH = 31;

    /**
     * The length of the name (the name may be smaller than this and null-terminated, but the number of bytes used is constant).
     */
    private static final int MAX_NAME_LENGTH = 30;

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
     * Constructs an ImageDataStruct with data from the specified ByteBuffer.
     * @param buffer the ByteBuffer to read data from.
     * @throws NullPointerException if buffer is null.
     */
    public ImageDataStruct(ByteBuffer buffer)
    {
        CheckParameters.areNotNull(buffer);

        version = readInt(buffer, VERSION);

        if ((version & 0xDECADE00) != 0xDECADE00)
        {
            throw new IllegalArgumentException("version is "+Integer.toHexString(version));
        }

        mode = readInt(buffer, MODE);
        camera = readInt(buffer, CAM);
        videoFormat = VideoFormat.valueOf(readInt(buffer, VID_FORMAT));
        startOffset = readInt(buffer, START_OFFSET);
        size = readInt(buffer, SIZE);
        maxSize = readInt(buffer, MAX_SIZE);
        targetSize = readInt(buffer, TARGET_SIZE);
        qFactor = readInt(buffer, Q_FACTOR);
        alarmBitmaskHigh = readInt(buffer, ALM_BITMASK_HI);
        status = readInt(buffer, STATUS);
        sessionTime = readInt(buffer, SESSION_TIME);
        milliseconds = (int)((0xFFFFFFFFL & readInt(buffer, MILLISECONDS)) % 1000L);
        res = read(buffer, 4, RES);
        title = nullTerminate(read(buffer, TITLE_LENGTH, TITLE));
        alarm = nullTerminate(read(buffer, TITLE_LENGTH, ALARM));
        format = new PictureStruct(buffer, FORMAT);
        locale = nullTerminate(read(buffer, MAX_NAME_LENGTH, LOCALE));
        utcOffset = readInt(buffer, UTC_OFFSET);
        alarmBitmask = readInt(buffer, ALM_BITMASK);
    }

    /**
     * Reads a big-endian int from the specified position in the specified ByteBuffer.
     * @param buffer the ByteBuffer to read an int from.
     * @param where the position to read from.
     * @throws NullPointerException if buffer is null.
     */     
    private int readInt(ByteBuffer buffer, int where)
    {
        CheckParameters.areNotNull(buffer);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt(where);
    }

    /**
     * Reads the specified number of bytes from the specified ByteBuffer, from the specified position.
     * @param buffer the ByteBuffer to read data from.
     * @param length the number of bytes to read.
     * @param where the position in the ByteBuffer to read from.
     * @throws NullPointerException if buffer is null.
     */
    public byte[] read(ByteBuffer buffer, int length, int where)
    {
        CheckParameters.areNotNull(buffer);
        byte[] result = new byte[length];
        buffer.position(where);
        buffer.get(result);
        return result;
    }

    /**
     * The version of the image data header.
     */
    private final int version;

    /**
     * Gets the version of the image data header.
     *
     * @return the version of the image data header.
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * The mode contained in the image data header.
     */
    private final int mode;

    /**
     * Gets the mode contained in the image data header.
     *
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * The camera contained in the image data header.
     */
    private final int camera;

    /**
     * Gets the camera contained in the image data header.
     *
     * @return the camera contained in the image data header.
     */
    public int getCamera()
    {
        return camera;
    }

    /**
     * The videoFormat contained in the image data header.
     */
    private final VideoFormat videoFormat;

    /**
     * Gets the videoFormat contained in the image data header.
     *
     * @return the videoFormat contained in the image data header.
     */
    public VideoFormat getVideoFormat()
    {
        return videoFormat;
    }

    /**
     * The startOffset contained in the image data header.
     */
    private final int startOffset;

    /**
     * Gets the startOffset contained in the image data header.
     *
     * @return the startOffset contained in the image data header.
     */
    public int getStartOffset()
    {
        return startOffset;
    }

    /**
     * The size contained in the image data header.
     */
    private final int size;

    /**
     * Gets the size contained in the image data header.
     *
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * The maxSize contained in the image data header.
     */
    private final int maxSize;

    /**
     * Gets the maxSize contained in the image data header.
     *
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
        return maxSize;
    }

    /**
     * The targetSize contained in the image data header.
     */
    private final int targetSize;

    /**
     * Gets the targetSize contained in the image data header.
     *
     * @return the targetSize contained in the image data header.
     */
    public int getTargetSize()
    {
        return targetSize;
    }

    /**
     * The qFactor contained in the image data header.
     */
    private final int qFactor;

    /**
     * Gets the qFactor contained in the image data header.
     *
     * @return the qFactor contained in the image data header.
     */
    public int getQFactor()
    {
        return qFactor;
    }

    /**
     * The alarmBitmaskHigh contained in the image data header.
     */
    private final int alarmBitmaskHigh;

    /**
     * Gets the alarmBitmaskHigh contained in the image data header.
     *
     * @return the alarmBitmaskHigh contained in the image data header.
     */
    public int getAlarmBitmaskHigh()
    {
        return alarmBitmaskHigh;
    }

    /**
     * The status contained in the image data header.
     */
    private final int status;

    /**
     * Gets the status contained in the image data header.
     *
     * @return the status contained in the image data header.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * The sessionTime contained in the image data header.
     */
    private final int sessionTime;

    /**
     * Gets the sessionTime contained in the image data header.
     *
     * @return the sessionTime contained in the image data header.
     */
    public int getSessionTime()
    {
        return sessionTime;
    }

    /**
     * The milliseconds contained in the image data header.
     */
    private final int milliseconds;

    /**
     * Gets the milliseconds contained in the image data header.
     *
     * @return the milliseconds contained in the image data header.
     */
    public int getMilliseconds()
    {
        return milliseconds;
    }

    /**
     * The res contained in the image data header.
     */
    private final byte[] res;

    /**
     * Gets the res contained in the image data header.
     *
     * @return the res contained in the image data header.
     */
    public byte[] getRes()
    {
        return res;
    }

    /**
     * The title contained in the image data header.
     */
    private final String title;

    /**
     * Gets the title contained in the image data header.
     *
     * @return the title contained in the image data header.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * The alarm contained in the image data header.
     */
    private final String alarm;

    /**
     * Gets the alarm contained in the image data header.
     *
     * @return the alarm contained in the image data header.
     */
    public String getAlarm()
    {
        return alarm;
    }

    /**
     * The format contained in the image data header.
     */
    private final PictureStruct format;

    /**
     * Gets the format contained in the image data header.
     *
     * @return the format contained in the image data header.
     */
    public PictureStruct getFormat()
    {
        return format;
    }

    /**
     * The locale contained in the image data header.
     */
    private final String locale;

    /**
     * Gets the locale contained in the image data header.
     *
     * @return the locale contained in the image data header.
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * The utcOffset contained in the image data header.
     */
    private final int utcOffset;

    /**
     * Gets the utcOffset contained in the image data header.
     *
     * @return the utcOffset contained in the image data header.
     */
    public int getUtcOffset()
    {
        return utcOffset;
    }

    /**
     * The alarmBitmask contained in the image data header.
     */
    private final int alarmBitmask;

    /**
     * Gets the alarmBitmask contained in the image data header.
     *
     * @return the alarmBitmask contained in the image data header.
     */
    public int getAlarmBitmask()
    {
        return alarmBitmask;
    }

    /**
     * Reads an ASCII String from the specified byte[], stopping when a 0 byte is found or the end of the array is reached.
     * @param input the byte[] to read from.
     * @return an ASCII String.
     * @throws NullPointerException if input is null.
     */
    private static String nullTerminate(byte[] input)
    {
        CheckParameters.areNotNull(input);
        try
        {
            return nullTerminate(new String(input, "US-ASCII"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes the part of the specified String before its first '\0', or the whole String if none is found.
     * @param input the String to read from.
     * @return a String without '\0' characters.
     * @throws NullPointerException if input is null.
     */
    private static String nullTerminate(String input)
    {
        CheckParameters.areNotNull(input);
        int indexOfNull = input.indexOf('\0');
        return indexOfNull == -1 ? input : input.substring(0, indexOfNull);
    }
}