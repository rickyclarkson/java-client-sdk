package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.IOException;

public enum StreamType
{
    BINARY(new BinaryParser()),
    MIME(new MimeParser());
    
    final Parser parser;

    StreamType(Parser parser)
    {
        this.parser = parser;
    }
}
