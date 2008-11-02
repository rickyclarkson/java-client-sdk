package uk.org.netvu.data;

public final class MimeStreamMetadata implements StreamMetadata
{
    private final int length;

    public MimeStreamMetadata(int length)
    {
        this.length = length;
    }

    public int getLength()
    {
        return length;
    }
}