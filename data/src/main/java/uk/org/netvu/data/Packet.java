package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * A Packet represents a single piece of data read from a stream, such as a JFIF
 * frame, or an MPEG-4 i-frame.
 */
public abstract class Packet
{
    /**
     * Constructs a Packet with the specified channel, data and onDiskFormat.
     * This is a utility method to avoid repetitive subclasses of Packet.
     * 
     * @param channel
     *        the channel that the Packet arrived on.
     * @param sourceIdentifier
     *        the caller-supplied source identifier.
     * @param data
     *        the data contained in the Packet.
     * @param onDiskFormat
     *        the data in its on-disk format.
     * @return a Packet with the specified channel, data and onDiskFormat.
     */
    static Packet constructPacket( final int channel, final Object sourceIdentifier, final ByteBuffer data,
            final ByteBuffer onDiskFormat )
    {
        return new Packet( channel, sourceIdentifier )
        {
            @Override
            public ByteBuffer getData()
            {
                return IO.duplicate( data );
            }

            @Override
            public ByteBuffer getOnDiskFormat()
            {
                return IO.duplicate( onDiskFormat );
            }
        };
    }

    /**
     * The channel that the Packet arrived on.
     */
    private final int channel;

    /**
     * The caller-supplied source identifier.
     */
    private final Object sourceIdentifier;

    /**
     * Constructs a Packet with the specified channel. Package-private to
     * prevent subclassing from outside this package.
     * 
     * @param channel
     *        the channel that the Packet arrived on.
     * @param sourceIdentifier
     *        the caller-supplied identifier for this Packet's source.
     */
    Packet( final int channel, final Object sourceIdentifier )
    {
        this.channel = channel;
        this.sourceIdentifier = sourceIdentifier;
    }

    /**
     * Gets the channel that the Packet arrived on.
     * 
     * @return the channel that the Packet arrived on.
     */
    public final int getChannel()
    {
        return channel;
    }

    /**
     * Gets the data contained in the Packet.
     * 
     * @return the data contained in the Packet.
     */
    public abstract ByteBuffer getData();

    /**
     * Gets the data in its on-disk format. For image data, this will always be
     * in the form of an ImageDataStruct. For audio data, this will always be in
     * the form of an AudioDataStruct.
     * 
     * @return the data in its on-disk format.
     */
    public abstract ByteBuffer getOnDiskFormat();

    /**
     * Gets the caller-supplied identifier for this Packet's source.
     * 
     * @return the caller-supplied identifier for this Packet's source.
     */
    public final Object getSourceIdentifier()
    {
        return sourceIdentifier;
    }
}
