package uk.org.netvu.data;

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser that can take an InputStream containing a certain data format and
 * deliver packets of data from it to a StreamHandler.
 */
public interface Parser
{
    /**
     * Parses data from the specified InputStream, delivering packets to the
     * specified StreamHandler.
     * 
     * @param input
     *        the InputStream to read data from.
     * @param sourceIdentifier
     *        an Object to use to identify the original source - useful if one
     *        StreamHandler is reused.
     * @param handler
     *        the StreamHandler to deliver packets to.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input or handler are null.
     */
    void parse( InputStream input, Object sourceIdentifier, StreamHandler handler ) throws IOException;
}
