package uk.org.netvu.data;

import java.nio.ByteBuffer;

public final class JPEGPacket
{
    public final ByteBuffer byteBuffer;
    public final StreamMetadata metadata;

    public JPEGPacket( ByteBuffer byteBuffer, StreamMetadata metadata )
    {
        metadata.toString();
        this.byteBuffer = byteBuffer;
        this.metadata = metadata;
    }
}