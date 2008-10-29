package uk.org.netvu.data;

public final class ParserFactory
{
    public static Parser parserFor(DataType dataType)
    {
        return dataType.parser();
    }
}