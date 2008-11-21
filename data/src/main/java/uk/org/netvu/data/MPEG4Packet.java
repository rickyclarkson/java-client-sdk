package uk.org.netvu.data;

import java.nio.ByteBuffer;

public class MPEG4Packet
{
    public final ByteBuffer data;
    public final StreamMetadata metadata;
    public final ImageDataStruct imageDataStruct;
    public final ByteBuffer commentData;

    public MPEG4Packet(ByteBuffer data, StreamMetadata metadata, ImageDataStruct imageDataStruct, ByteBuffer commentData)
    {
        this.data=data;
        this.metadata = metadata;
        this.imageDataStruct = imageDataStruct;
        this.commentData = commentData;
    }
}
    