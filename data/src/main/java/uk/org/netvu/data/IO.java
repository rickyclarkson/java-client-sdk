package uk.org.netvu.data;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

final class IO
{
    public static void expectLine(InputStream input, String string) throws IOException
    {
        String line = readLine(input);
        if (!string.equals(line))
        {
            throw new IllegalStateException("expected " + string + ", found " + line);
        }
    }

    private static String readLine(InputStream input) throws IOException
    {
        final int b = input.read();
        switch (b)
        {
            case '\n': return "";
            case '\r': return readLine(input);
            case -1: throw new EOFException();
            default: return String.valueOf( (char)b ) + readLine(input);
        }
    }

    public static void expectLineMatching(InputStream input, final String regex) throws IOException
    {
        final String line = readLine(input);
        if (!line.matches(regex))
        {
            throw new IllegalStateException(regex + " expected, " + line + " found");
        }
    }

    public static void expectString(final InputStream input,final String string) throws IOException
    {
        if (string.equals(""))
        {
            return;
        }

        if (input.read() != string.charAt(0))
        {
            throw new IllegalStateException();
        }

        if (string.length() > 1)
        {
            expectString(input, string.substring(1));
        }
    }

    public static int expectIntFromRestOfLine(InputStream input) throws IOException
    {
        try
        {
            return Integer.parseInt(IO.readLine(input));
        }
        catch (NumberFormatException exception)
        {
            throw new IllegalStateException();
        }
    }

    public static ByteBuffer readIntoByteBuffer( InputStream input, int bytes ) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocate( bytes );
        int read = Channels.newChannel( input ).read( buffer );
        if (read != bytes)
            throw new EOFException();

        buffer.position( 0 );
        return buffer;
    }

    /*    public static byte[] readIntoByteArray( ByteBuffer buffer, int bytes ) throws IOException
    {
        byte[] results = new byte[bytes];
        buffer.get(results);
        return results;
        }*/
}