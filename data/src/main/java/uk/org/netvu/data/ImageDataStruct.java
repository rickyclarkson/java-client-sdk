package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.org.netvu.util.CheckParameters;

public final class ImageDataStruct
{
    private static final int INT = 4;
    private static final int TIME_SIZE = 4;
    private static final int TITLE_LENGTH = 31;
    private static final int MAX_NAME_LENGTH = 30;

    private static final int VERSION = 0;
    private static final int MODE = VERSION + INT;
    private static final int CAM = MODE + INT;
    private static final int VID_FORMAT = CAM + INT;
    private static final int START_OFFSET = VID_FORMAT + INT;
    private static final int SIZE = START_OFFSET + INT;
    private static final int MAX_SIZE = SIZE + INT;
    private static final int TARGET_SIZE = MAX_SIZE + INT;
    private static final int Q_FACTOR = TARGET_SIZE + INT;
    private static final int ALM_BITMASK_HI = Q_FACTOR + INT;
    private static final int STATUS = ALM_BITMASK_HI + INT;
    private static final int SESSION_TIME = STATUS + INT;
    private static final int MILLISECONDS = SESSION_TIME + TIME_SIZE;
    private static final int RES = MILLISECONDS + INT;
    private static final int TITLE = RES + 4;
    private static final int ALARM = TITLE + TITLE_LENGTH;
    private static final int FORMAT = ALARM + TITLE_LENGTH;
    private static final int LOCALE = FORMAT + Picture.SIZE;
    private static final int UTC_OFFSET = LOCALE + MAX_NAME_LENGTH;
    private static final int ALM_BITMASK = UTC_OFFSET + INT;

    public static final int IMAGE_DATA_STRUCT_SIZE = ALM_BITMASK + INT;

    private final ByteBuffer buffer;

    public ImageDataStruct(ByteBuffer buffer)
    {
        CheckParameters.areNotNull(buffer);

        this.buffer = buffer;
        version = readInt(VERSION);

        if ((version & 0xDECADE00) != 0xDECADE00)
        {
            throw new IllegalArgumentException("version is "+Integer.toHexString(version));
        }

        mode = readInt(MODE);
        camera = readInt(CAM);
        videoFormat = VideoFormat.valueOf(readInt(VID_FORMAT));
        startOffset = readInt(START_OFFSET);
        size = readInt(SIZE);
        maxSize = readInt(MAX_SIZE);
        targetSize = readInt(TARGET_SIZE);
        qFactor = readInt(Q_FACTOR);
        alarmBitmaskHigh = readInt(ALM_BITMASK_HI);
        status = readInt(STATUS);
        sessionTime = readInt(SESSION_TIME);
        milliseconds = (int)((0xFFFFFFFFL & readInt(MILLISECONDS)) % 1000L);
        res = read(4, RES);
        title = nullTerminate(read(TITLE_LENGTH, TITLE));
        alarm = nullTerminate(read(TITLE_LENGTH, ALARM));
        format = new Picture(buffer, FORMAT);
        locale = nullTerminate(read(MAX_NAME_LENGTH, LOCALE));
        utcOffset = readInt(UTC_OFFSET);
        alarmBitmask = readInt(ALM_BITMASK);
    }

    private int readInt(int where)
    {
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt(where);
    }

    public byte[] read(int length, int where)
    {
        byte[] result = new byte[length];
        buffer.position(where);
        buffer.get(result);
        return result;
    }

    private final int version;

    public int getVersion()
    {
        return version;
    }

    private final int mode;

    public int getMode()
    {
        return mode;
    }

    private final int camera;

    public int getCamera()
    {
        return camera;
    }

    private final VideoFormat videoFormat;

    public VideoFormat getVideoFormat()
    {
        return videoFormat;
    }

    private final int startOffset;

    public int getStartOffset()
    {
        return startOffset;
    }

    private final int size;

    public int getSize()
    {
        return size;
    }

    private final int maxSize;

    public int getMaxSize()
    {
        return maxSize;
    }

    private final int targetSize;

    public int getTargetSize()
    {
        return targetSize;
    }

    private final int qFactor;

    public int getQFactor()
    {
        return qFactor;
    }

    private final int alarmBitmaskHigh;

    public int getAlarmBitmaskHigh()
    {
        return alarmBitmaskHigh;
    }

    private final int status;

    public int getStatus()
    {
        return status;
    }

    private final int sessionTime;

    public int getSessionTime()
    {
        return sessionTime;
    }

    private final int milliseconds;

    public int getMilliseconds()
    {
        return milliseconds;
    }

    private final byte[] res;

    public byte[] getRes()
    {
        return res;
    }

    private final String title;

    public String getTitle()
    {
        return title;
    }

    private final String alarm;

    public String getAlarm()
    {
        return alarm;
    }

    private final Picture format;

    public Picture getFormat()
    {
        return format;
    }

    private final String locale;

    public String getLocale()
    {
        return locale;
    }

    private final int utcOffset;

    public int getUtcOffset()
    {
        return utcOffset;
    }

    private final int alarmBitmask;

    public int getAlarmBitmask()
    {
        return alarmBitmask;
    }

    private static String nullTerminate(byte[] input)
    {
        return nullTerminate(new String(input));
    }

    private static String nullTerminate(String input)
    {
        int indexOfNull = input.indexOf('\0');
        return indexOfNull == -1 ? input : input.substring(0, indexOfNull);
    }
}