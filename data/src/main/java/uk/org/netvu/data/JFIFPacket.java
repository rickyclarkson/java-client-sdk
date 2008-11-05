package uk.org.netvu.data;

import java.nio.ByteBuffer;

public final class JFIFPacket
{
    public final ByteBuffer byteBuffer;
    public final StreamMetadata metadata;

    public JFIFPacket( ByteBuffer byteBuffer, StreamMetadata metadata )
    {
        this.byteBuffer = byteBuffer;
        this.metadata = metadata;
    }
}