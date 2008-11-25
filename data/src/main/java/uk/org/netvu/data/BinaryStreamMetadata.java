package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

final class BinaryStreamMetadata implements StreamMetadata
{
    private final FrameType frameType;
    private final int channel;
    private final int length;

    public BinaryStreamMetadata( InputStream input ) throws IOException
    {
        frameType = FrameType.frameTypeFor(input.read() & 0xFF);
        channel = input.read() + 1;
        length = new DataInputStream( input ).readInt();
    }

    public int getLength()
    {
        return length;
    }

    public int getChannel()
    {
        return channel;
    }

    public FrameType getFrameType()
    {
        return frameType;
    }
}