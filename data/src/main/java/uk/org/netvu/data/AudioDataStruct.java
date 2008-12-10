package uk.org.netvu.data;

import java.nio.*;

public final class AudioDataStruct
{
  private static final int VERSION_OFFSET = 0;
  private static final int MODE_OFFSET = VERSION_OFFSET + 4;
  private static final int CHANNEL_OFFSET = MODE_OFFSET + 4;
  private static final int START_OFFSET_OFFSET = CHANNEL_OFFSET + 4;
  private static final int SIZE_OFFSET = START_OFFSET_OFFSET + 4;
  private static final int SECONDS_OFFSET = SIZE_OFFSET + 4;
  private static final int MILLISECONDS_OFFSET = SECONDS_OFFSET + 4;

  public static final int AUDIO_DATA_STRUCT_SIZE = MILLISECONDS_OFFSET + 4;

  private ByteBuffer buffer;

  public static final int VERSION = 0x00ABCDEF;

  public AudioDataStruct(ByteBuffer buffer)
  {
    this.buffer = IO.duplicate(buffer);

    if (getVersion() != VERSION)
      throw new IllegalArgumentException(Integer.toHexString(getVersion())+" unsupported.");
  }

  public int getVersion()
  {
    return IO.readInt( buffer, VERSION_OFFSET );
  }

  public int getMode() { return IO.readInt( buffer, MODE_OFFSET ); }
  public int getChannel() { return IO.readInt( buffer, CHANNEL_OFFSET ); }
  public int getStartOffset() { return IO.readInt( buffer, START_OFFSET_OFFSET); }
  public int getSize() { return IO.readInt( buffer, SIZE_OFFSET ); }
  public int getSeconds() { return IO.readInt(buffer, SECONDS_OFFSET ); }
  public int getMilliseconds() { return IO.readInt(buffer, MILLISECONDS_OFFSET); }
  
  public void setMode(int mode) { buffer.putInt( MODE_OFFSET, mode); }
  public void setChannel(int channel) { buffer.putInt( CHANNEL_OFFSET, channel); }
  public void setStartOffset(int startOffset) { buffer.putInt( START_OFFSET_OFFSET, startOffset); }
  public void setSize(int size) { buffer.putInt( SIZE_OFFSET, size); }
  public void setSeconds(int seconds) { buffer.putInt( SECONDS_OFFSET, seconds); }
  public void setMilliseconds(int milliseconds) { buffer.putInt( MILLISECONDS_OFFSET, milliseconds); }
}