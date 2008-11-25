package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.ByteBuffer;

/**
 * A parser that can take an InputStream containing a certain data format and deliver packets of data from it to a StreamHandler.
 */
public interface Parser
{
    /**
     * Parses data from the specified InputStream, delivering packets to the specified StreamHandler.
     * @param input the InputStream to read data from.
     * @param handler the StreamHandler to deliver packets to.
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if either parameter is null.
     */
    void parse( InputStream input, StreamHandler handler ) throws IOException;
}