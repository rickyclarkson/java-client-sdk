package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.ByteBuffer;

public interface Parser
{
    void parse( InputStream input, StreamHandler handler ) throws IOException;
}