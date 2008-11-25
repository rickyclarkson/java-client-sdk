package uk.org.netvu.data;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

final class MinimalStreamMetadata implements StreamMetadata
{
    public final FrameType frameType;

    private final int length;
    private final int channel;

    public MinimalStreamMetadata( InputStream input ) throws IOException
    {
        switch (input.read())
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

        DataInputStream data = new DataInputStream(input);
        channel = data.read();
        length = data.readInt();
    }

    public int getLength()
    {
        return length;
    }

    public int getChannel()
    {
        return channel;
    }
}