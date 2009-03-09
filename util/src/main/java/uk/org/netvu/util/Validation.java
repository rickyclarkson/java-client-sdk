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
}