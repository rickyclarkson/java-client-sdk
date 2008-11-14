package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

final class BinaryStreamMetadata implements StreamMetadata
{
    public final FrameType frameType;
    public final int channel;
    private final int length;

    public BinaryStreamMetadata( InputStream input ) throws IOException
    {
        int format = input.read() & 0xFF;

        switch (format)
        {
        case 0:
            frameType = FrameType.JPEG;
            break;
        case 1:
            frameType = FrameType.JFIF;
            break;
        case 2:
        case 3:
            frameType = FrameType.MPEG4;
            break;
        case 9:
            frameType = FrameType.INFO;
            break;
        default:
            frameType = FrameType.UNKNOWN;
        }

        channel = input.read() + 1;
        length = new DataInputStream( input ).readInt();
    }

    public int getLength()
    {
        return length;
    }
}