package uk.org.netvu.data;

import java.nio.ByteBuffer;

/**
 * An UnknownPacket represents a single packet of data of an unknown or unsupported type read from a stream.
 */
public final class UnknownPacket extends Packet
{
  /**
   * The data stored in the packet.
   */
  private final ByteBuffer data;

  /**
   * Constructs an UnknownPacket with the specified data and metadata.
   *
   * @param data the single packet of data.
   * @param metadata the metadata about the packet.
   */
  public UnknownPacket(ByteBuffer data, int channel, int length, FrameType frameType )
  {
    super(channel, length, frameType);
    this.data = data;
  }

  /**
   * Gets the data stored in the packet.
   *
   * @return the data stored in the packet.
   */
  public ByteBuffer getData()
  {
    return data;
  }
}