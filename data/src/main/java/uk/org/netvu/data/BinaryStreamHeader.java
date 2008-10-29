package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class BinaryStreamHeader
{
    public final FrameType frameType;
    public final int channel;
    public final int dataLength;

    public BinaryStreamHeader(InputStream input) throws IOException
    {
        frameType = input.read() == 1 ? FrameType.JPEG : null;
        channel = input.read() + 1;
        dataLength = new DataInputStream(input).readInt();
    }
}