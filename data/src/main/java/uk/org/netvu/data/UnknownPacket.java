package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * An UnknownPacket represents a single packet of data of an unknown or
 * unsupported type read from a stream.
 */
final class UnknownPacket
        extends Packet
{
    /**
     * The data stored in the packet.
     */
    private final ByteBuffer data;

    /**
     * Constructs an UnknownPacket with the specified data and metadata.
     * 
     * @param data
     *        the single packet of data.
     * @param metadata
     *        the metadata about the packet.
     */
    public UnknownPacket( final ByteBuffer data, final int channel, final int length )
    {
        super( channel );
        this.data = data;
    }

    /**
     * Gets the data stored in the packet.
     * 
     * @return the data stored in the packet.
     */
    @Override
    public ByteBuffer getData()
    {
        return data;
    }

    @Override
    public ByteBuffer getOnWireFormat()
    {
        return data;
    }
}
