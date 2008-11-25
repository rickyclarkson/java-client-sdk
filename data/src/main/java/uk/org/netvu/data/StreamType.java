package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.IOException;
import uk.org.netvu.util.CheckParameters;

/**
 * An enumeration of the supported StreamTypes.
 */
public enum StreamType
{
    /**
     * A StreamType representing 'binary' streams.
     */
    BINARY(new BinaryParser()),

    /**
     * A StreamType representing 'mime' streams.
     */
    MIME(new MimeParser()),
       
    /**
     * A StreamType representing 'minimal' streams.
     */
    MINIMAL(new MinimalParser());
    
    /**
     * The Parser for this StreamType.
     */
    final Parser parser;

    /**
     * Constructs a StreamType whose Parser is the specified Parser.
     * @param parser the Parser for this StreamType.
     * @throws NullPointerException if parser is null.
     */
    StreamType(Parser parser)
    {
        CheckParameters.areNotNull(parser);
        this.parser = parser;
    }
}