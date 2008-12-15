package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.Arrays;
import uk.org.netvu.util.CheckParameters;

/**
 * A utility class for dealing with InputStreams, ByteBuffers and byte[]s.
 */
final class IO
{
    /**
     * Converts a byte[] to a String using the US-ASCII character encoding.
     * 
     * @param b
     *        the byte[] to convert to a String.
     * @return the String after conversion.
     * @throws NullPointerException
     *         if b is null.
     */
    public static String bytesToString( final byte[] b )
    {
        CheckParameters.areNotNull( b );

        try
        {
            return new String( b, "US-ASCII" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Duplicates a ByteBuffer, creating a ByteBuffer with the same backing
     * data, but with a new position and limit. The position of the new
     * ByteBuffer is 0.
     * 
     * @param b
     *        the ByteBuffer to duplicate.
     * @return a copy of this ByteBuffer, with the position set to 0.
     * @throws NullPointerException
     *         if b is null.
     */
    public static ByteBuffer duplicate( final ByteBuffer b )
    {
        CheckParameters.areNotNull( b );

        final ByteBuffer result = b.duplicate();
        result.position( 0 );
        return result;
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
     * Searches a String for a certain String, returning the portion of the
     * original String after the end of the String to search for, or a specified
     * default value if the text isn't found.
     * 
     * @param in
     *        the String to search in.
     * @param after
     *        the String to search for.
     * @param ifNotFound
     *        the String to return if 'after' is not found.
     * @return the portion of the String from the 'in' parameter that is after
     *         the 'after' parameter, or the ifNotFound parameter otherwise.
     * @throws NullPointerException
     *         if in or after are null.
     */
    public static String find( final String in, final String after, final String ifNotFound )
    {
        CheckParameters.areNotNull( in, after );
        final int index = in.indexOf( after );
        return index < 0 ? ifNotFound : in.substring( index + after.length(), in.indexOf( "\r", index ) );
    }

    /**
     * Searches a String for a certain String, returning the portion of the
     * original String after the end of the String to search for, as an int, or
     * the specified default value if the text isn't found.
     * 
     * @param in
     *        the String to search in.
     * @param after
     *        the String to search for.
     * @param ifNotFound
     *        the value to return if 'after' is not found.
     * @return the portion of the String from the 'in' parameter that is after
     *         the 'after' parameter, as an int, or the ifNotFound parameter
     *         otherwise.
     * @throws NullPointerException
     *         if any of the parameters are null.
     * @throws NumberFormatException
     *         if the String to parse as an int does not represent an int.
     */
    public static int findInt( final String in, final String after, final int ifNotFound )
    {
        final String s = find( in, after, null );
        return s == null ? ifNotFound : Integer.parseInt( s );
    }

    /**
     * Gives a copy of the specified ByteBuffer, from the specified position. No
     * bytes are copied, the copy has the same data as the original but begins
     * at the specified position.
     * 
     * @param b
     *        the ByteBuffer to copy.
     * @param position
     *        the position within the ByteBuffer to begin the new ByteBuffer at.
     * @return a copy of the specified ByteBuffer, from the specified position.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    public static ByteBuffer from( final ByteBuffer b, final int position )
    {
        CheckParameters.areNotNull( b, position );
        ByteBuffer result = b.duplicate();
        result.position( position );
        result = result.slice();
        result.position( 0 );
        return result;
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

    /**
     * Reads the next line of text from the specified InputStream and returns it
     * as an int.
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
    public static int readIntFromRestOfLine( final InputStream input ) throws IOException
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
     * Reads the specified number of bytes from the specified ByteBuffer, into a
     * byte[].
     * 
     * @param from
     *        the ByteBuffer to read from.
     * @param length
     *        the number of bytes to read.
     * @return a byte[] containing the bytes read from the ByteBuffer.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    public static byte[] readIntoByteArray( final ByteBuffer from, final int length )
    {
        CheckParameters.areNotNull( from );

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

    /**
     * Searches in the specified ByteBuffer for the specified byte[], returning
     * the index of the found byte[].
     * 
     * @param in
     *        the ByteBuffer to search in.
     * @param toFind
     *        the byte[] to search for.
     * @return the index of the byte[] in the specified ByteBuffer.
     * @throws NullPointerException
     *         if any of the parameters are null.
     * @throws BufferUnderflowException
     *         if the end of the ByteBuffer is reached without finding the
     *         byte[].
     */
    public static int searchFor( final ByteBuffer in, final byte[] toFind )
    {
      try
      {
        CheckParameters.areNotNull( in, toFind );

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
      catch (BufferUnderflowException e)
        {
          System.out.println("Could not find "+Arrays.toString(toFind)+" in "+toString(in));
          throw e;
        }
    }

  private static String toString(ByteBuffer b)
  {
    b.position(0);
    StringBuilder builder = new StringBuilder();
    for (int a=0;a<b.limit();a+=4)
      builder.append(Integer.toHexString(b.getInt())+", ");
    return builder.toString();
    
  }

    /**
     * Converts a String to a byte[] using the US-ASCII character encoding.
     * 
     * @param s
     *        the String to convert to a byte[].
     * @return the byte[] after conversion.
     * @throws NullPointerException
     *         if s is null.
     */
    public static byte[] stringToBytes( final String s )
    {
        CheckParameters.areNotNull( s );

        try
        {
            return s.getBytes( "US-ASCII" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Gives a copy of the specified ByteBuffer, of the specified length, from
     * the specified index. No bytes are copied, the copy has the same data as
     * the original but only has access to the specified portion.
     * 
     * @param b
     *        the ByteBuffer to copy.
     * @param from
     *        the position within the ByteBuffer to begin the new ByteBuffer at.
     * @param length
     *        the length of the new ByteBuffer, in bytes.
     * @return a copy of the specified ByteBuffer, of the specified length, from
     *         the specified index.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    static ByteBuffer slice( final ByteBuffer b, final int from, final int length )
    {
        CheckParameters.areNotNull( b );
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
