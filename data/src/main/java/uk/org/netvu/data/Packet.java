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
  private final int length;
  private final FrameType frameType;

    /**
     * Constructs a Packet.
     */
  Packet(int channel, int length, FrameType frameType)
    {
      CheckParameters.areNotNull(frameType);
      this.channel = channel;
      this.length = length;
      this.frameType = frameType;
    }

  public int getChannel()
  {
    return channel;
  }

  public int getLength()
  {
    return length;
  }

  public FrameType getFrameType()
  {
    return frameType;
  }

  public abstract ByteBuffer getData();
  public abstract ByteBuffer getOnWireFormat();
}
