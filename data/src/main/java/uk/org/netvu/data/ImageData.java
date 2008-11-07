package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ImageData
{
    private static final int INT = 4;
    private static final int TIME_SIZE = 4;
    private static final int TITLE_LENGTH = 30;
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
    private static final int ALARM = TITLE + TITLE_LENGTH + 1;
    private static final int FORMAT = ALARM + TITLE_LENGTH + 1;
    private static final int LOCALE = FORMAT + Picture.SIZE;
    private static final int UTC_OFFSET = LOCALE + MAX_NAME_LENGTH;
    private static final int ALM_BITMASK = UTC_OFFSET + INT;
    public static final int IMAGE_DATA_SIZE = ALM_BITMASK + INT;

    private final ByteBuffer buffer;

    public ImageData(ByteBuffer buffer)
    {
        if (buffer == null)
            throw null;

        this.buffer = buffer;
        version = readInt(VERSION);
        if ((version & 0xDECADE00) != 0xDECADE00)
            throw null;
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

    public final int version;
    public int getMode() { return readInt(MODE); }
    public int getCam() { return readInt(CAM); }
    public VideoFormat getVidFormat() { return VideoFormat.valueOf(readInt(VID_FORMAT)); }
    public int getStartOffset() { return readInt(START_OFFSET); }
    public int getSize() { return readInt(SIZE); }
    public int getMaxSize() { return readInt(MAX_SIZE); }
    public int getTargetSize() { return readInt(TARGET_SIZE); }
    public int getQFactor() { return readInt(Q_FACTOR); }
    public int getAlmBitmaskHi() { return readInt(ALM_BITMASK_HI); }
    public int getStatus() { return readInt(STATUS); }
    public int getSessionTime() { return readInt(SESSION_TIME); }
    public int getMilliseconds() { return readInt(MILLISECONDS); }
    public byte[] getRes() { return read(4, RES); }
    public byte[] getTitle() { return read(TITLE_LENGTH + 1, TITLE); }
    public byte[] getAlarm() { return read(TITLE_LENGTH + 1, ALARM); }
    public Picture getFormat() { return new Picture( buffer, FORMAT ); }
    public byte[] getLocale() { return read(MAX_NAME_LENGTH + 1, LOCALE); }
    public int getUtcOffset() { return readInt(UTC_OFFSET); }
    public int getAlmBitmask() { return readInt(ALM_BITMASK); }
}