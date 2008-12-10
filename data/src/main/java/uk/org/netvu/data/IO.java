package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;

import uk.org.netvu.util.CheckParameters;

/**
 * A utility class for dealing with reading values from InputStreams.
 */
final class IO
{
    public static ByteBuffer duplicate( final ByteBuffer b )
    {
        final ByteBuffer result = b.duplicate();
        result.position( 0 );
        return result;
    }

    /**
     * Reads the next line from the specified InputStream and returns it as an
     * int.
     * 
     * @param input
     *        the InputStream to read an int from.
     * @return the next line from the specified InputStream, as an int.
     * @throws IllegalStateException
     *         if the line read doesn't parse as an int.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input is null.
     */
    public static int expectIntFromRestOfLine( final InputStream input ) throws IOException
    {
        CheckParameters.areNotNull( input );
        try
        {
            return Integer.parseInt( IO.readLine( input ) );
        }
        catch ( final NumberFormatException exception )
        {
            throw new IllegalStateException();
        }
    }

    /**
     * Asserts that the next line read from the specified InputStream matches
     * the specified String.
     * 
     * @param input
     *        the InputStream to read a line of text from.
     * @param string
     *        the expected line.
     * @throws IllegalStateException
     *         if the expected line is not found.
     * @throws IOException
     *         if any I/O errors occur.
     * @throws NullPointerException
     *         if either of the parameters are null.
     */
    public static void expectLine( final InputStream input, final String string ) throws IOException
    {
        CheckParameters.areNotNull( input, string );
        final String line = readLine( input );
        if ( !string.equals( line ) )
        {
            throw new IllegalStateException( "expected " + string + ", found " + line );
        }
    }

    /**
     * Asserts that the next line read from the specified InputStream matches
     * the specified regular expression.
     * 
     * @param input
     *        the InputStream to read a line of text from.
     * @param regex
     *        the regex the line should match.
     * @throws IllegalStateException
     *         if the line doesn't match the regex.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if either of the parameters are null.
     */
    public static void expectLineMatching( final InputStream input, final String regex ) throws IOException
    {
        CheckParameters.areNotNull( input, regex );
        final String line = readLine( input );
        if ( !line.matches( regex ) )
        {
            throw new IllegalStateException( regex + " expected, " + line + " found" );
        }
    }

    /**
     * Asserts that the specified String is the next text read from the
     * specified InputStream. Note that this method does not read to the end of
     * the line.
     * 
     * @param input
     *        the InputStream to read text from.
     * @param string
     *        the expected text.
     * @throws IllegalStateException
     *         if the text read doesn't match the passed-in String.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if either of the parameters are null.
     */
    public static void expectString( final InputStream input, final String string ) throws IOException
    {
        CheckParameters.areNotNull( input, string );
        if ( string.equals( "" ) )
        {
            return;
        }

        if ( input.read() != string.charAt( 0 ) )
        {
            throw new IllegalStateException();
        }

        if ( string.length() > 1 )
        {
            expectString( input, string.substring( 1 ) );
        }
    }

    public static String find( final String in, final String after, final String ifNotFound )
    {
        final int index = in.indexOf( after );
        return index < 0 ? ifNotFound : in.substring( index + after.length(), in.indexOf( "\r", index ) );
    }

    public static int findInt( final String in, final String after, final int ifNotFound )
    {
        final String s = find( in, after, null );
        return s == null ? ifNotFound : Integer.parseInt( s );
    }

    /**
     * Reads a big-endian int from the specified position in the specified
     * ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to read an int from.
     * @param where
     *        the position to read from.
     * @return a big-endian int read from the specified position in the
     *         specified ByteBuffer.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public static int readInt( final ByteBuffer buffer, final int where )
    {
        CheckParameters.areNotNull( buffer );
        buffer.order( ByteOrder.BIG_ENDIAN );
        return buffer.getInt( where );
    }

    public static byte[] readIntoByteArray( final ByteBuffer from, final int length )
    {
        final byte[] bytes = new byte[length];
        from.get( bytes );
        return bytes;
    }

    /**
     * Reads the specified number of bytes from the specified InputStream into a
     * ByteBuffer.
     * 
     * @param input
     *        the InputStream to read bytes from.
     * @param bytes
     *        the number of bytes to read.
     * @return a ByteBuffer containing the specified number of bytes read from
     *         the specified InputStream.
     * @throws EOFException
     *         if the number of bytes read is not the same as the number of
     *         bytes specified.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input is null.
     */
    public static ByteBuffer readIntoByteBuffer( final InputStream input, final int bytes ) throws IOException
    {
        CheckParameters.areNotNull( input );
        final ByteBuffer buffer = ByteBuffer.allocate( bytes );
        final int read = Channels.newChannel( input ).read( buffer );
        if ( read != bytes )
        {
            throw new EOFException();
        }

        buffer.position( 0 );
        return buffer;
    }

    /**
     * Reads a line of text from the specified InputStream.
     * 
     * @param input
     *        the InputStream to read a line of text from.
     * @throws EOFException
     *         if the end of the InputStream is reached before the end of the
     *         line is reached.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input is null.
     * @return a line of text read from the specified InputStream.
     */
    public static String readLine( final InputStream input ) throws IOException
    {
        CheckParameters.areNotNull( input );

        final StringBuilder soFar = new StringBuilder();

        for ( ;; )
        {
            final int b = input.read();
            switch ( b )
            {
                case '\n':
                    return soFar.toString();
                case '\r':
                    break;
                case -1:
                    throw new EOFException();
                default:
                    soFar.append( (char) b );
            }
        }
    }

    public static int searchFor( final ByteBuffer in, final byte[] toFind )
    {
        int i = 0;
        outer: while ( true )
        {
            in.position( i++ );

            for ( final byte b : toFind )
            {
                if ( in.get() != b )
                {
                    continue outer;
                }
            }

            return i - 1;
        }
    }

    static ByteBuffer from( final ByteBuffer b, final int position )
    {
        ByteBuffer result = b.duplicate();
        result.position( position );
        result = result.slice();
        result.position( 0 );
        return result;
    }

    static ByteBuffer slice( final ByteBuffer b, final int from, final int length )
    {
        final ByteBuffer result = from( b, from );
        result.limit( length );
        return result;
    }

    /**
     * Private to prevent instantiation.
     */
    private IO()
    {
    }
}
