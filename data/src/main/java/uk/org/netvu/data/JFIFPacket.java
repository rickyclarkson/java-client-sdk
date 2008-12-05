package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A JFIFPacket represents a single JFIF frame read from a stream.  The JFIF data is available as a ByteBuffer via the method getData().
 */
final class JFIFPacket extends Packet
{
  /**
   * The JFIF frame.
   */
  private final ByteBuffer data;
  private final int realFrameType;

  /**
   * Constructs a JFIFPacket with the specified data and metadata.
   * @param data the JFIF frame to store.
   * @param metadata the metadata about the JFIFPacket.
   * @throws NullPointerException if data or metadata are null.
   */
  JFIFPacket(ByteBuffer data, int channel, int length, int frameType )
  {
    super(channel, length, FrameType.JFIF);
    this.realFrameType = frameType;

    if (length == 1)
      throw null;
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
    return realFrameType == 1 ? data : JFIFHeader.jpegToJfif(data);
  }

  public ByteBuffer getOnWireFormat()
  {
    return data;
  }
}