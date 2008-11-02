package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.IOException;

public enum DataType
{
    BINARY(new BinaryParser()),
    MIME(new MimeParser());
    
    final Parser parser;

    DataType(Parser parser)
    {
        this.parser = parser;
    }
}
