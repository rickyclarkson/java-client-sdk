package uk.org.netvu.data;


/**
 * An enumeration of the supported StreamTypes.
 */
public enum StreamType
{
    /**
     * A StreamType representing 'binary' streams.
     */
    BINARY( new BinaryParser() ),

    /**
     * A StreamType representing 'mime' streams.
     */
    MIME( new MimeParser() ),

    /**
     * A StreamType representing 'minimal' streams.
     */
    MINIMAL( new MinimalParser() );

    /**
     * The Parser for this StreamType.
     */
    final Parser parser;

    /**
     * Constructs a StreamType whose Parser is the specified Parser.
     * 
     * @param parser
     *        the Parser for this StreamType.
     * @throws NullPointerException
     *         if parser is null.
     */
    StreamType( final Parser parser )
    {
        CheckParameters.areNotNull( parser );
        this.parser = parser;
    }
}
