package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import uk.org.netvu.util.CheckParameters;

/**
 * A utility class for dealing with reading values from InputStreams.
 */
final class IO
{
    /**
     * Reads the next line from the specified InputStream and returns it as an
     * int.
     * 
     * @param input
     *        the InputStream to read an int from.
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
    private static String readLine( final InputStream input ) throws IOException
    {
        CheckParameters.areNotNull( input );
        final int b = input.read();
        switch ( b )
        {
            case '\n':
                return "";
            case '\r':
                return readLine( input );
            case -1:
                throw new EOFException();
            default:
                return String.valueOf( (char) b ) + readLine( input );
        }
    }

    /**
     * Private to prevent instantiation.
     */
    private IO()
    {
    }
}
