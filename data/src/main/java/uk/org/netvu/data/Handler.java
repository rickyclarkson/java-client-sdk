package uk.org.netvu.data;

import java.io.IOException;
import java.nio.ByteBuffer;

interface Handler
{
    void handleJPEG( ByteBuffer data ) throws IOException;
}
