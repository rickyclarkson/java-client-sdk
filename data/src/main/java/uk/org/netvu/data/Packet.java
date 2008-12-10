package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * A Packet represents a single piece of data read from a stream, such as a JFIF
 * frame, or an MPEG-4 i-frame.
 */
public abstract class Packet
{
    private final int channel;

    /**
     * Constructs a Packet.
     */
    Packet( final int channel )
    {
        this.channel = channel;
    }

    public int getChannel()
    {
        return channel;
    }

    public abstract ByteBuffer getData();

    public abstract ByteBuffer getOnWireFormat();
}
