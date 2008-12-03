package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A JFIFPacket represents a single JFIF frame read from a stream.  The JFIF data is available as a ByteBuffer via the method getData().
 */
public final class JFIFPacket extends Packet
{
  /**
   * The JFIF frame.
   */
  private final ByteBuffer data;

  /**
   * Constructs a JFIFPacket with the specified data and metadata.
   * @param data the JFIF frame to store.
   * @param metadata the metadata about the JFIFPacket.
   * @throws NullPointerException if data or metadata are null.
   */
  JFIFPacket(ByteBuffer data, int channel, int length, FrameType frameType )
  {
    super(channel, length, frameType);
    CheckParameters.areNotNull(data);
    this.data = data;
  }

  /**
   * Gets the JFIF frame held by the JFIFPacket.
   *
   * @return the JFIF frame held by the JFIFPacket.
   */
  public ByteBuffer getData()
  {
    return data;
  }
}