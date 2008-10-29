package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;

interface Handler
{
    void jpeg( JPEGPacket packet ) throws IOException;
    void binaryStreamHeader( BinaryStreamHeader header );
}
