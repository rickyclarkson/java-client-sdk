package uk.org.netvu.core.cgi.displaypic;

import java.io.IOException;
import java.net.ByteBuffer;

interface Handler
{
    void handleJPEG( ByteBuffer data ) throws IOException;
}
