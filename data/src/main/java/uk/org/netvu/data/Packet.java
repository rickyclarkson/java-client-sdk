package uk.org.netvu.data;

import java.nio.ByteBuffer;

public abstract class Packet<T>
{
    static Packet<String> infoPacket( final String text, final PacketMetadata metadata )
    {
        return packet( text, metadata );
    }

    static Packet<ByteBuffer> jfifPacket( final ByteBuffer data, final PacketMetadata metadata )
    {
        return packet( data, metadata );
    }

    static Packet unknownPacket( final ByteBuffer data, final PacketMetadata metadata )
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

    Packet()
    {
    }

    public abstract T getData();

    public abstract PacketMetadata getMetadata();
}
