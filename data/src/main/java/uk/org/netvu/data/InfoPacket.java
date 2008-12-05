package uk.org.netvu.data;

import uk.org.netvu.util.CheckParameters;
import java.nio.ByteBuffer;

/**
 * An InfoPacket represents a single information packet read from a stream.  The information is available via the method getData().
 */
public final class InfoPacket extends Packet
{
  /**
   * The information that the InfoPacket holds.
   */
  private final ByteBuffer data;

  /**
   * Constructs an InfoPacket with the specified data and metadata.
   *
   * @param data the information that the InfoPacket holds.
   * @param metadata the metadata about the InfoPacket.
   * @throws NullPointerException if data or metadata are null.
   */
  InfoPacket(ByteBuffer data, int channel, int length)
  {
    super(channel, length);
    CheckParameters.areNotNull(data);
    this.data = data;
  }

  /**
   * Gets the information that this InfoPacket holds.
   */
  public ByteBuffer getData()
  {
    return data;
  }

  public ByteBuffer getOnWireFormat()
  {
    return data;
  }
}