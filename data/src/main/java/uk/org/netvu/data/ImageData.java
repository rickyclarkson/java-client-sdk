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
    private static final int FACTOR = TARGET_SIZE + INT;
    private static final int ALM_BITMASK_HI = FACTOR + INT;
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

    private final ByteBuffer buffer;
    private final int offset;

    public ImageData(ByteBuffer buffer, int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
    }

    private int readInt(int where)
    {
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt(offset + where);
    }

    public byte[] read(int length, int where)
    {
        byte[] result = new byte[length];
        buffer.get(offset + where -1);
        buffer.get(result);
        return result;
    }

    public final int version = readInt(VERSION);
    public final int mode = readInt(MODE);
    public final int cam = readInt(CAM);
    public final int vidFormat = readInt(VID_FORMAT);
    public final int startOffset = readInt(START_OFFSET);
    public final int size = readInt(SIZE);
    public final int maxSize = readInt(MAX_SIZE);
    public final int targetSize = readInt(TARGET_SIZE);
    public final int factor = readInt(FACTOR);
    public final int almBitmaskHi = readInt(ALM_BITMASK_HI);
    public final int status = readInt(STATUS);
    public final int sessionTime = readInt(SESSION_TIME);
    public final int milliseconds = readInt(MILLISECONDS);
    public final byte[] res = read(4, RES);
    public final byte[] title = read(TITLE_LENGTH + 1, TITLE);
    public final byte[] alarm = read(TITLE_LENGTH + 1, ALARM);

    public final Picture format = format();

    private final Picture format()
    {
        return new Picture(buffer, offset + FORMAT);
    }

    public final byte[] locale = read(MAX_NAME_LENGTH + 1, LOCALE);
    public final int utcOffset = readInt(UTC_OFFSET);
    public final int almBitmask = readInt(ALM_BITMASK);
}