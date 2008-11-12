package uk.org.netvu.data;

public final class ParserFactory
{
    public static Parser parserFor(StreamType streamType)
    {
        return streamType.parser;
    }
}