package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class PictureStruct
{
    private static final int INT16 = 2;

    private static final int SRC_PIXELS = 0;
    private static final int SRC_LINES = SRC_PIXELS + INT16;
    private static final int TARGET_PIXELS = SRC_LINES + INT16;
    private static final int TARGET_LINES = TARGET_PIXELS + INT16;
    private static final int PIXEL_OFFSET = TARGET_LINES + INT16;
    private static final int LINE_OFFSET = PIXEL_OFFSET + INT16;

    public static final int SIZE = LINE_OFFSET + INT16;

    public PictureStruct(ByteBuffer buffer, int offset)
    {
        this.buffer = buffer;
        this.offset = offset;

        srcPixels = readShort(SRC_PIXELS);
        srcLines = readShort(SRC_LINES);
        targetPixels = readShort(TARGET_PIXELS);
        targetLines = readShort(TARGET_LINES);
        pixelOffset = readShort(PIXEL_OFFSET);
        lineOffset = readShort(LINE_OFFSET);
    }

    private short readShort(int where)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort(offset + where);
    }

    private final ByteBuffer buffer;
    private final int offset;

    private final short srcPixels;

    public short getsrcPixels()
    {
        return srcPixels;
    }

    private final short srcLines;

    public short getsrcLines()
    {
        return srcLines;
    }

    private final short targetPixels;

    public short getTargetPixels()
    {
        return targetPixels;
    }

    private final short targetLines;

    public short getTargetLines()
    {
        return targetLines;
    }

    private final short pixelOffset;

    public short getPixelOffset()
    {
        return pixelOffset;
    }

    private final short lineOffset;

    public short getLineOffset()
    {
        return lineOffset;
    }
}