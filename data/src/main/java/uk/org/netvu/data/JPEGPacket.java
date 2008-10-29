package uk.org.netvu.data;

import java.nio.ByteBuffer;

public final class JPEGPacket
{
    public final ByteBuffer byteBuffer;
    public final int length;

    public JPEGPacket( ByteBuffer byteBuffer, int length )
    {
        this.byteBuffer = byteBuffer;
        this.length = length;
    }
}