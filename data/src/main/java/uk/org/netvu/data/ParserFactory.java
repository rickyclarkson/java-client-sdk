package uk.org.netvu.data;


/**
 * A factory that, given a StreamType, gives a Parser that can parse it.
 */
public final class ParserFactory
{
    /**
     * Gets a Parser for the specified StreamType.
     * 
     * @param streamType
     *        the StreamType to get a Parser for.
     * @return a Parser for the specified StreamType.
     * @throws NullPointerException
     *         if the streamType is null.
     */
    public static Parser parserFor( final StreamType streamType )
    {
        CheckParameters.areNotNull( streamType );
        return streamType.parser;
    }

    /**
     * Private to prevent instantiation.
     */
    private ParserFactory()
    {
    }
}
