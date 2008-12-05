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
     * @param packet
     *        the Packet of data that has arrived.
     */
    void unknownDataArrived( UnknownPacket packet );

    /**
     * Signals the arrival of textual information to the client.
     * 
     * @param packet
     *        the Packet of data that has arrived.
     */
    void infoArrived( InfoPacket packet );

    /**
     * Signals the arrival of a JFIF packet to the client.
     * 
     * @param packet
     *        the Packet of data that has arrived.
     */
    void jpegFrameArrived( JFIFPacket packet );

    /**
     * Signals the arrival of a MPEG-4 packet to the client.
     * 
     * @param packet
     *        the MPEG4Packet that has arrived.
     */
    void mpeg4FrameArrived( MPEG4Packet packet );
}
