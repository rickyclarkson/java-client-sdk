package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * A Packet represents a single piece of data read from a stream, such as a JFIF
 * frame, or an MPEG-4 i-frame.
 * 
 * @param <T>
 *        the type of data held by the Packet.
 */
public abstract class Packet<T>
{
    /**
     * Constructs a Packet with the specified data and metadata.
     * 
     * @param text
     *        the textual information that the constructed Packet contains.
     * @param metadata
     *        data about the Packet.
     * @return a Packet holding the passed-in text and metadata.
     */
    static Packet<String> infoPacket( final String text, final PacketMetadata metadata )
    {
        return packet( text, metadata );
    }

    /**
     * Constructs a Packet with the specified data and metadata.
     * 
     * @param data
     *        the JFIF data that the constructed Packet contains.
     * @param metadata
     *        data about the Packet.
     * @return a Packet holding the passed-in JFIF data and metadata.
     */
    static Packet<ByteBuffer> jfifPacket( final ByteBuffer data, final PacketMetadata metadata )
    {
        return packet( data, metadata );
    }

    /**
     * Constructs a Packet with the specified data and metadata.
     * 
     * @param data
     *        the bytes that the constructed Packet contains.
     * @param metadata
     *        data about the Packet.
     * @return a Packet holding the passed-in data and metadata.
     */
    static Packet<ByteBuffer> unknownPacket( final ByteBuffer data, final PacketMetadata metadata )
    {
        return packet( data, metadata );
    }

    private static <T> Packet<T> packet( final T t, final PacketMetadata metadata )
    {
        return new Packet<T>()
        {
            @Override
            public T getData()
            {
                return t;
            }

            @Override
            public PacketMetadata getMetadata()
            {
                return metadata;
            }
        };
    }

    /**
     * Constructs a Packet.
     */
    Packet()
    {
    }

    /**
     * Gets the data held by this Packet.
     * 
     * @return the data held by this Packet.
     */
    public abstract T getData();

    /**
     * Gets the metadata held by this Packet.
     * 
     * @return the metadata held by this Packet.
     */
    public abstract PacketMetadata getMetadata();
}
