package uk.org.netvu.data;

import java.nio.ByteBuffer;
import uk.org.netvu.util.CheckParameters;

/**
 * An MPEG-4 packet plus metadata and the comment block.
 */
public final class MPEG4Packet
{
    /**
     * The data part of the MPEG-4 packet.
     */
    private final ByteBuffer data;

    /**
     * Gets the data part of the MPEG-4 packet.
     * @return the data part of the MPEG-4 packet.
     */
    public ByteBuffer getData()
    {
        return data;
    }

    /**
     * The stream metadata for the stream the current packet was read from.
     */
    private final StreamMetadata metadata;
    
    /**
     * Gets the stream metadata for the stream the current packet was read from.
     *
     * @return the stream metadata for the stream the current packet was read from.
     */
    public StreamMetadata getMetadata()
    {
        return metadata;
    }

    /**
     * The ImageDataStruct associated with this MPEG4Packet.
     */
    private final ImageDataStruct imageDataStruct;

    /**
     * Gets the ImageDataStruct associated with this MPEG4Packet.
     * @return the ImageDataStruct associated with this MPEG4Packet.
     */
    public ImageDataStruct getImageDataStruct()
    {
        return imageDataStruct;
    }

    /**
     * The comment data associated with this MPEG4Packet.
     */
    private final ByteBuffer commentData;

    /**
     * Gets the comment data associated with this MPEG4Packet.
     * @return the comment data associated with this MPEG4Packet.
     */
    public ByteBuffer getCommentData()
    {
        return commentData;
    }

    /**
     * Constructs an MPEG4Packet with the specified data, stream metadata, ImageDataStruct and comment data.
     * @param data the MPEG-4 data.
     * @param metadata the metadata about the stream the packet was parsed from.
     * @param imageDataStruct information about the image.
     * @param commentData the comment data extracted from the stream.
     * @throws NullPointerException if any of the values are null.
     */
    public MPEG4Packet(ByteBuffer data, StreamMetadata metadata, ImageDataStruct imageDataStruct, ByteBuffer commentData)
    {
        CheckParameters.areNotNull(data, metadata, imageDataStruct, commentData);
        this.data=data;
        this.metadata = metadata;
        this.imageDataStruct = imageDataStruct;
        this.commentData = commentData;
    }
}
    