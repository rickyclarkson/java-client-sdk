package uk.org.netvu.data;

import java.nio.ByteBuffer;
import uk.org.netvu.util.CheckParameters;

/**
 * A Packet represents a single piece of data read from a stream, such as a JFIF
 * frame, or an MPEG-4 i-frame.
 */
public abstract class Packet
{
  private final int channel;

    /**
     * Constructs a Packet.
     */
  Packet(int channel)
    {
      this.channel = channel;
    }

  public int getChannel()
  {
    return channel;
  }

  public abstract ByteBuffer getData();
  public abstract ByteBuffer getOnWireFormat();
}
