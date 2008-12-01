package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.org.netvu.util.CheckParameters;

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

    private final ByteOrder order;

    /**
     * The point in the ByteBuffer at which to begin from.
     */
    private final int offset;

    /**
     * Constructs a PictureStruct with data from the specified ByteBuffer,
     * beginning at the specified offset in the ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to parse data from.
     * @param order
     *        the ByteOrder to use for reading and writing the ByteBuffer.
     * @param offset
     *        the offset within the ByteBuffer to begin at.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public PictureStruct( final ByteBuffer buffer, final ByteOrder order, final int offset )
    {
        CheckParameters.areNotNull( buffer, order );
        this.buffer = buffer;
        this.order = order;
        this.offset = offset;
    }

    /**
     * @return the ByteBuffer backing this PictureStruct.
     */
    public ByteBuffer getByteBuffer()
    {
        return buffer;
    }

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

    /**
     * Gets the target lines (the picture height) of this PictureStruct.
     * 
     * @return the target lines of this PictureStruct.
     */
    public short getTargetLines()
    {
        return readShort( TARGET_LINES );
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
        buffer.order( order );
        return buffer.getShort( offset + where );
    }
}
