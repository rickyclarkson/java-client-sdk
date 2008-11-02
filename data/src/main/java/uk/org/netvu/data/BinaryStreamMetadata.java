package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class BinaryStreamMetadata implements StreamMetadata
{
    public final FrameType frameType;
    public final int channel;
    private final int length;

    public BinaryStreamMetadata( InputStream input ) throws IOException
    {
        int firstByte = input.read();
        System.out.println("firstByte = "+firstByte);
        frameType = firstByte == 1 ? FrameType.JFIF : FrameType.UNKNOWN;
        channel = input.read() + 1;
        length = new DataInputStream( input ).readInt();
    }

    public int getLength()
    {
        return length;
    }
}