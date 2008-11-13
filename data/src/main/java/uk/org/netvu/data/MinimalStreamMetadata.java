package uk.org.netvu.data;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

final class MinimalStreamMetadata implements StreamMetadata
{
    public final FrameType frameType;

    private final int length;

    public MinimalStreamMetadata( InputStream input ) throws IOException
    {
        int format = input.read();
        switch (format)
        {
        case 0:
            frameType = FrameType.JPEG;
            break;
        case 1:
            frameType = FrameType.JFIF;
            break;
        default:
            frameType = FrameType.UNKNOWN;
        }

        input.read();
        input.read();
        input.read();
        length = new DataInputStream(input).readShort();
    }

    public int getLength()
    {
        return length;
    }
}