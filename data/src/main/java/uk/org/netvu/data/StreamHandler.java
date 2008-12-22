package uk.org.netvu.data;

/**
 * The interface the client is expected to implement to listen for data packets
 * as they arrive.
 */
public abstract class StreamHandler
{
    /**
     * Signals the arrival of audio data to the client.
     * 
     * @param packet
     *        the Packet of audio data that has arrived.
     */
    public abstract void audioDataArrived( Packet packet );

    /**
     * Signals the arrival of textual information to the client.
     * 
     * @param packet
     *        the Packet of data that has arrived.
     */
    public abstract void infoArrived( Packet packet );

    /**
     * Signals the arrival of a JFIF packet to the client.
     * 
     * @param packet
     *        the Packet of data that has arrived.
     */
    public abstract void jpegFrameArrived( Packet packet );

    /**
     * Signals the arrival of a MPEG-4 packet to the client.
     * 
     * @param packet
     *        the MPEG4Packet that has arrived.
     */
    public abstract void mpeg4FrameArrived( Packet packet );

    /**
     * Signals the arrival of data of an unknown type to the client.
     * 
     * @param packet
     *        the Packet of data that has arrived.
     */
    public abstract void unknownDataArrived( Packet packet );
}
