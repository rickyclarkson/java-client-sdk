package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface StreamHandler
{
    void jfif( JFIFPacket packet ) throws IOException;
    void mpeg4( MPEG4Packet packet ) throws IOException;
    void info( ByteBuffer buffer ) throws IOException;
    void dataArrived( ByteBuffer buffer, StreamMetadata metadata ) throws IOException;
}
