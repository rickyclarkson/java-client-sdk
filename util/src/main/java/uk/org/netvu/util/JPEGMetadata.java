package uk.org.netvu.util;

import java.nio.ByteBuffer;

/**
 * A utility class for extracting JPEG metadata.
 */
public final class JPEGMetadata
{
    /**
     * Extracts the comment data from a JPEG.
     *
     * @param jfifData the raw JFIF bytes.
     * @return a String containing the comments.
     * @throws BufferUnderflowException if no comment field is found.
     * @throws NullPointerException if jfifData is null.
     */
    public static String getCommentData(final ByteBuffer jfifData)
    {
        CheckParameters.areNotNull( jfifData );
        
        while ( true )
        {
            if ( ( jfifData.get() & 0xFF ) == 0xFF && ( jfifData.get() & 0xFF ) == 0xFE )
            {
                final short commentLength = jfifData.getShort();
                final byte[] comment = new byte[commentLength];
                jfifData.get( comment );
                try
                {
                    return new String( comment );
                }
                finally
                {
                    jfifData.position( 0 );
                }
            }
        }
    }
}