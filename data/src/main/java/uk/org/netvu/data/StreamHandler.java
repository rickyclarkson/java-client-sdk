package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * The interface the client is expected to interface to listen for data packets
 * as they arrive.
 */
public interface StreamHandler
{
    /**
     * Signals the arrival of data of an unknown type to the client.
     * 
     * @param buffer
     *        the data in the packet.
     * @param metadata
     *        information about the packet.
     */
    void dataArrived( ByteBuffer buffer, StreamMetadata metadata );

    /**
     * Signals the arrival of textual information to the client.
     * 
     * @param text
     *        the text read from the stream.
     */
    void info( String text );

    /**
     * Signals the arrival of a JFIF packet to the client.
     * 
     * @param buffer
     *        the JFIF data.
     */
    void jfif( ByteBuffer buffer );

    /**
     * Signals the arrival of a MPEG-4 packet to the client.
     * 
     * @param MPEG4Packet
     *        the MPEG-4 data and metadata.
     */
    void mpeg4( MPEG4Packet packet );
}
