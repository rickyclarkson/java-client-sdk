package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Picture
{
    private static final int INT16 = 2;

    private static final int SRC_PIXELS = 0;
    private static final int SRC_LINES = SRC_PIXELS + INT16;
    private static final int TARGET_PIXELS = SRC_LINES + INT16;
    private static final int TARGET_LINES = TARGET_PIXELS + INT16;
    private static final int PIXEL_OFFSET = TARGET_LINES + INT16;
    private static final int LINE_OFFSET = PIXEL_OFFSET + INT16;

    public static final int SIZE = LINE_OFFSET + INT16;

    public Picture(ByteBuffer buffer, int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
    }

    private short readShort(int where)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort(offset + where);
    }

    private final ByteBuffer buffer;
    private final int offset;

    public final short srcPixels = readShort(SRC_PIXELS);
    public final short srcLines = readShort(SRC_LINES);
    public final short targetPixels = readShort(TARGET_PIXELS);
    public final short targetLines = readShort(TARGET_LINES);
    public final short pixelOffset = readShort(PIXEL_OFFSET);
    public final short lineOffset = readShort(LINE_OFFSET);
}