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

    final Effect<Packet> audioDataArrived = new Effect<Packet>() { public void apply(Packet packet) { audioDataArrived(packet); } };
    final Effect<Packet> infoArrived = new Effect<Packet>() { public void apply(Packet packet) { infoArrived(packet); } };
    final Effect<Packet> jpegFrameArrived = new Effect<Packet>() { public void apply(Packet packet) { jpegFrameArrived(packet); } };
    final Effect<Packet> mpeg4FrameArrived = new Effect<Packet>() { public void apply(Packet packet) { mpeg4FrameArrived(packet); } };
    final Effect<Packet> unknownDataArrived = new Effect<Packet>() { public void apply(Packet packet) { unknownDataArrived(packet); } };
}
