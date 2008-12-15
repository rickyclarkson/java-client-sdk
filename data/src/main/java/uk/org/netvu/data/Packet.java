package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * A Packet represents a single piece of data read from a stream, such as a JFIF
 * frame, or an MPEG-4 i-frame.
 */
public abstract class Packet
{
  /**
   * The channel that the Packet arrived on.
   */
    private final int channel;

    /**
     * Constructs a Packet with the specified channel.
     * Package-private to prevent subclassing from outside this package.
     * @param channel the channel that the Packet arrived on.
     */
    Packet( final int channel )
    {
        this.channel = channel;
    }

  /**
   * Gets the channel that the Packet arrived on.
   * @return the channel that the Packet arrived on.
   */
    public final int getChannel()
    {
        return channel;
    }

  /**
   * Gets the data contained in the Packet.
   */
    public abstract ByteBuffer getData();

  /**
   * Gets the data in its on-wire format.  For image data, this will always be in the form of an ImageDataStruct.  For audio data, this will always be in the form of an AudioDataStruct.
   * @return the data in its on-wire format.
   */
    public abstract ByteBuffer getOnWireFormat();
}
