package uk.org.netvu.util;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public final class Buffers
{
    /**
     * Constructs a ByteBuffer containing the data from the specified file.
     * 
     * @param filename
     *        the name of the file that contains the data to read.
     * @return a ByteBuffer containing the data from the specified file.
     * @throws IOException
     *         if any I/O error occurs.
     */
    public static ByteBuffer bufferFor( final String filename ) throws IOException
    {
        final ByteBuffer first =
                new FileInputStream( filename ).getChannel().map( FileChannel.MapMode.READ_ONLY, 0,
                        new File( filename ).length() );
        first.position( 0 );
        final ByteBuffer result = ByteBuffer.allocateDirect( first.limit() );
        result.put( first );
        result.position( 0 );
        return result;
    }


    /**
     * Constructs an array of bytes containing the data from the specified file.
     * 
     * @param filename
     *        the name of the file that contains the data to read.
     * @return an array of bytes containing the data from the specified file.
     * @throws IOException
     *         if any I/O error occurs.
     */
    public static byte[] byteArrayFor( final String filename ) throws IOException
    {
        final ByteBuffer buffer = bufferFor( filename );
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get( bytes );
        return bytes;
    }
}