package uk.org.netvu.data;

import uk.org.netvu.util.CheckParameters;

/**
 * A factory that, given a StreamType, gives a Parser that can parse it.
 */
public final class ParserFactory
{
    /**
     * Gets a Parser for the specified StreamType.
     *
     * @param streamType the StreamType to get a Parser for.
     * @return a Parser for the specified StreamType.
     * @throws NullPointerException if the streamType is null.
     */
    public static Parser parserFor(StreamType streamType)
    {
        CheckParameters.areNotNull(streamType);
        return streamType.parser;
    }
}