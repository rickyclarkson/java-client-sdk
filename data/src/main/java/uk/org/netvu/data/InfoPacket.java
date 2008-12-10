package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * An InfoPacket represents a single information packet read from a stream. The
 * information is available via the method getData().
 */
public final class InfoPacket
        extends Packet
{
    /**
     * The information that the InfoPacket holds.
     */
    private final ByteBuffer data;

    /**
     * Constructs an InfoPacket with the specified data and metadata.
     * 
     * @param data
     *        the information that the InfoPacket holds.
     * @param metadata
     *        the metadata about the InfoPacket.
     * @throws NullPointerException
     *         if data or metadata are null.
     */
    InfoPacket( final ByteBuffer data, final int channel, final int length )
    {
        super( channel );
        CheckParameters.areNotNull( data );
        this.data = data;
    }

    /**
     * Gets the information that this InfoPacket holds.
     */
    @Override
    public ByteBuffer getData()
    {
        return data;
    }

    @Override
    public ByteBuffer getOnWireFormat()
    {
        return data;
    }
}
