package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Information about a single packet retrieved while parsing a stream.
 */
public final class PacketMetadata
{
    /**
     * Constructs a PacketMetadata using data taken from a binary or a minimal
     * data stream. There is no difference in format between the two streams at
     * this point.
     * 
     * @param input
     *        the InputStream from which to read data.
     * @return a PacketMetadata constructed using data taken from the binary or
     *         minimal data stream.
     * @throws IOException
     *         if any I/O errors occur.
     */
    static PacketMetadata fromBinaryOrMinimalStream( final InputStream input ) throws IOException
    {
        final FrameType frameType = FrameType.frameTypeFor( input.read() & 0xFF );
        final int channel = input.read() + 1;
        final int length = new DataInputStream( input ).readInt();

        return new PacketMetadata( length, channel, frameType );
    }

    private final int length;
    private final int channel;

    private final FrameType frameType;

    /**
     * Constructs a PacketMetadata using the specified length, channel and
     * FrameType.
     * 
     * @param length
     *        the length of the packet.
     * @param channel
     *        the channel the packet was received on.
     * @param frameType
     *        the type of data held in the packet.
     */
    PacketMetadata( final int length, final int channel, final FrameType frameType )
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

    /**
     * Gets the length of the packet.
     * 
     * @return the length of the packet.
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Gets the type of data held in the packet.
     * 
     * @return the type of data held in the packet.
     */
    FrameType getFrameType()
    {
        return frameType;
    }
}
