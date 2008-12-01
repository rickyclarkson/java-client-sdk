package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * An MPEG-4 packet plus metadata and the comment block.
 */
public final class MPEG4Packet
        extends Packet<ByteBuffer>
{
    /**
     * The data part of the MPEG-4 packet.
     */
    private final ByteBuffer data;

    /**
     * The stream metadata for the stream the current packet was read from.
     */
    private final PacketMetadata metadata;

    /**
     * The ImageDataStruct associated with this MPEG4Packet.
     */
    private final ImageDataStruct imageDataStruct;

    /**
     * The comment data associated with this MPEG4Packet.
     */
    private final ByteBuffer commentData;

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
    public MPEG4Packet( final ByteBuffer data, final PacketMetadata metadata, final ImageDataStruct imageDataStruct,
            final ByteBuffer commentData )
    {
        // CheckParameters.areNotNull( data, metadata, imageDataStruct,
        // commentData );
        this.data = data;
        this.metadata = metadata;
        this.imageDataStruct = imageDataStruct;
        this.commentData = commentData;
    }

    /**
     * Gets the comment data associated with this MPEG4Packet.
     * 
     * @return the comment data associated with this MPEG4Packet.
     */
    public ByteBuffer getCommentData()
    {
        return commentData;
    }

    /**
     * Gets the data part of the MPEG-4 packet.
     * 
     * @return the data part of the MPEG-4 packet.
     */
    @Override
    public ByteBuffer getData()
    {
        return data;
    }

    /**
     * Gets the ImageDataStruct associated with this MPEG4Packet.
     * 
     * @return the ImageDataStruct associated with this MPEG4Packet.
     */
    public ImageDataStruct getImageDataStruct()
    {
        return imageDataStruct;
    }

    /**
     * Gets the stream metadata for the stream the current packet was read from.
     * 
     * @return the stream metadata for the stream the current packet was read
     *         from.
     */
    @Override
    public PacketMetadata getMetadata()
    {
        return metadata;
    }
}
