package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Information about a single packet retrieved while parsing a stream.
 */
public final class StreamMetadata
{
    private final int length;
    private final int channel;
    private final FrameType frameType;

    public StreamMetadata(int length, int channel, FrameType frameType)
    {
        this.length = length;
        this.channel = channel;
        this.frameType = frameType;
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

    public static StreamMetadata fromBinaryOrMinimalStream(InputStream input) throws IOException
    {
        FrameType frameType = FrameType.frameTypeFor(input.read() & 0xFF);
        int channel = input.read() + 1;
        int length = new DataInputStream( input ).readInt();
        
        return new StreamMetadata(length, channel, frameType);
    }
}
