package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * An MPEG-4 packet plus metadata and the comment block.
 */
public final class MPEG4Packet
        extends Packet
{
    /**
     * The data part of the MPEG-4 packet.
     */
    private final ByteBuffer data;

    /**
     * Constructs an MPEG4Packet with the specified data, stream metadata,
     * ImageDataStruct and comment data.
     * 
     * @param data
     *        the MPEG-4 data.
     * @param metadata
     *        the metadata about the stream the packet was parsed from.
     * @param imageDataStruct
     *        information about the image.
     * @param commentData
     *        the comment data extracted from the stream.
     * @throws NullPointerException
     *         if any of the values are null.
     */
  public MPEG4Packet( final ByteBuffer data, final int channel )
    {
      super(channel);
        // CheckParameters.areNotNull( data, metadata, imageDataStruct,
        // commentData );
        this.data = data;
    }

    /**
     * Gets the data part of the MPEG-4 packet.
     * 
     * @return the data part of the MPEG-4 packet.
     */
    public ByteBuffer getData()
    {
        return data;
    }

  public ByteBuffer getOnWireFormat()
  {
    return data;
  }
}
