package uk.org.netvu.core.cgi.displaypic;

import java.io.IOException;
import java.nio.ByteBuffer;

interface Handler
{
    void handleJPEG( ByteBuffer data ) throws IOException;
}
