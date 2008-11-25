package uk.org.netvu.data;

/**
 * Information about a single packet retrieved while parsing a stream.
 */
public interface StreamMetadata
{
    /**
     * Gets the length of the packet.
     *
     * @return the length of the packet.
     */
    public int getLength();
}