package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Information about a single packet retrieved while parsing a stream.
 */
public final class PacketMetadata
{
    public static PacketMetadata fromBinaryOrMinimalStream( final InputStream input ) throws IOException
    {
        final FrameType frameType = FrameType.frameTypeFor( input.read() & 0xFF );
        final int channel = input.read() + 1;
        final int length = new DataInputStream( input ).readInt();

        return new PacketMetadata( length, channel, frameType );
    }
    private final int length;
    private final int channel;

    private final FrameType frameType;

    public PacketMetadata( final int length, final int channel, final FrameType frameType )
    {
        this.length = length;
        this.channel = channel;
        this.frameType = frameType;
    }

    /**
     * Gets the channel the packet was received on.
     * 
     * @return the channel the packet was received on.
     */
    public int getChannel()
    {
        return channel;
    }

    public FrameType getFrameType()
    {
        return frameType;
    }

    /**
     * Gets the length of the packet.
     * 
     * @return the length of the packet.
     */
    public int getLength()
    {
        return length;
    }
}
