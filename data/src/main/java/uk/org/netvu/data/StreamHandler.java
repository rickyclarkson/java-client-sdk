package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;

interface StreamHandler
{
    void jfif( JPEGPacket packet ) throws IOException;
    void unknown( ByteBuffer data, StreamMetadata metadata ) throws IOException;
}
