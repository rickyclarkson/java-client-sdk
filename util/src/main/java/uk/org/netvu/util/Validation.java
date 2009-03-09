package uk.org.netvu.util;

import java.nio.ByteBuffer;
import uk.org.netvu.util.CheckParameters;
import java.util.Arrays;

/**
 * Validation routines for video data.
 */
public final class Validation
{
    /**
     * The beginning header for VOP information in an MPEG-4 frame.
     */
    private static final byte[] MPEG4_VOP_START = { 0x00, 0x00, 0x01, (byte) 0xB6 };

    /**
     * Determines whether the specified {@code ByteBuffer} contains valid JPEG data, by looking for
     * JFIF SOI and EOI data.
     */
    public static boolean isValidJPEG(ByteBuffer originalBuffer)
    {
        ByteBuffer buffer = originalBuffer.duplicate();
        if ((buffer.get() & 0xFF) == 0xFF && (buffer.get() & 0xFF) == 0xD8)
        {
            buffer.position(buffer.limit() - 2);
            return (buffer.get() & 0xFF) == 0xFF && (buffer.get() & 0xFF) == 0xD9;
        }
        return false;
    }

    /**
     * Identifies whether the data in the ByteBuffer is an MPEG-4 I-frame, by
     * finding and parsing VOP headers.
     * 
     * @param data
     *        the ByteBuffer to search.
     * @return true if the data is an I-frame, false otherwise.
     * @throws NullPointerException
     *         if data is null.
     */
    public static boolean isIFrame( final ByteBuffer data )
    {
        CheckParameters.areNotNull( data );
        boolean foundVOP = false;
        byte[] startCode;
        int pos = 0;

        startCode = new byte[4];
        while ( !foundVOP && pos + 4 < data.limit() )
        {
            data.position( pos );
            data.get( startCode );

            if ( Arrays.equals( startCode, MPEG4_VOP_START ) )
            {
                foundVOP = true;
            }
            pos++;
        }
        pos += 3;
        if ( foundVOP )
        {
            final int pictureType = data.get( pos ) >> 6 & 0xFF;
            if ( pictureType == 0 )
            {

                return true;
            }
        }
        else
        {
            throw new IllegalStateException( "Cannot find MPEG-4 VOP start code; cannot tell if I-frame" );
        }
        return false;
    }

    /**
     * A template for a start of sequence marker in MPEG-4 data.
     */
    private static final byte[] START_OF_SEQUENCE_TEMPLATE = { 0x00, 0x00,
        0x01, (byte) ( 0xB0 & 0xFF ), 0x03, 0x00, 0x00, 0x01,
        (byte) ( 0xB5 & 0xFF ), 0x0B, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01,
        0x20, 0x00, (byte) ( 0x84 & 0xFF ), 0x40, (byte) ( 0xFA & 0xFF ), 0x28,
        0x00, 0x20, 0x00, (byte) ( 0xA3 & 0xFF ), 0x00 };

    /**
     * The index of the first byte in the start of sequence header that specifies the width.
     */
    private static final int INDEX_OF_FIRST_WIDTH_BYTE = 22;

    /**
     * Prefixes the specified MPEG-4 frame with a Start Of Sequence marker.
     *
     * @param frame the MPEG-4 frame to prefix with a Start Of Sequence marker.
     * @param width the width of the frame.
     * @param height the height of the frame.
     * @return a new ByteBuffer containing a Start Of Sequence marker followed by the specified frame.
     * @throws NullPointerException if frame is null.
     * @throws IllegalStateException if width or height are 0 or negative.
     */
    public static ByteBuffer prefixStartOfSequence(ByteBuffer frame, int width, int height)
    {
        CheckParameters.areNotNull(frame).arePositive(width, height);
        ByteBuffer result = ByteBuffer.allocateDirect(START_OF_SEQUENCE_TEMPLATE.length + frame.limit());
        result.put(START_OF_SEQUENCE_TEMPLATE);
        byte temp = result.get(INDEX_OF_FIRST_WIDTH_BYTE);
        temp |= (width & 0x1C00) >> 10;
        result.put(INDEX_OF_FIRST_WIDTH_BYTE, temp);
        result.put(INDEX_OF_FIRST_WIDTH_BYTE + 1, (byte)((width & 0x3FC) >> 2));
        temp = result.get(INDEX_OF_FIRST_WIDTH_BYTE + 2);
        temp |= (width & 0x3) << 6;
        temp |= (height & 0x1F00) >> 8;
        result.put(INDEX_OF_FIRST_WIDTH_BYTE + 2, temp);
        result.put(INDEX_OF_FIRST_WIDTH_BYTE + 3, (byte)(height & 0xFF));
        result.position(START_OF_SEQUENCE_TEMPLATE.length);
        result.put(frame);
        result.position(0);
        return result;        
    }
}