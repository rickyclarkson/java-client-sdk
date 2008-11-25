package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A representation of the 'picture' part of the ImageDataStruct.
 */
public final class PictureStruct
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
     * The ByteBuffer to read from.
     */
    private final ByteBuffer buffer;

    /**
     * The point in the ByteBuffer at which to begin from.
     */
    private final int offset;

    private final short srcPixels;

    private final short srcLines;

    private final short targetPixels;

    private final short targetLines;

    private final short pixelOffset;

    private final short lineOffset;

    /**
     * Constructs a PictureStruct with data from the specified ByteBuffer,
     * beginning at the specified offset in the ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to parse data from.
     * @param offset
     *        the offset within the ByteBuffer to begin at.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public PictureStruct( final ByteBuffer buffer, final int offset )
    {
        CheckParameters.areNotNull( buffer );
        this.buffer = buffer;
        this.offset = offset;

        srcPixels = readShort( SRC_PIXELS );
        srcLines = readShort( SRC_LINES );
        targetPixels = readShort( TARGET_PIXELS );
        targetLines = readShort( TARGET_LINES );
        pixelOffset = readShort( PIXEL_OFFSET );
        lineOffset = readShort( LINE_OFFSET );
    }

    public short getLineOffset()
    {
        return lineOffset;
    }

    public short getPixelOffset()
    {
        return pixelOffset;
    }

    public short getSrcLines()
    {
        return srcLines;
    }

    public short getSrcPixels()
    {
        return srcPixels;
    }

    public short getTargetLines()
    {
        return targetLines;
    }

    public short getTargetPixels()
    {
        return targetPixels;
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
        buffer.order( ByteOrder.LITTLE_ENDIAN );
        return buffer.getShort( offset + where );
    }
}
