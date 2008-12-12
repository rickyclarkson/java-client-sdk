package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;

/**
 * A data structure compatible with the audio_data struct that audio samples arrive in in binary streams.
 */
public final class AudioDataStruct
{
  /**
   * The offset of the version field in the data, in bytes.
   */
  private static final int VERSION_OFFSET = 0;

  /**
   * The offset of the mode field in the data, in bytes.
   */
    private static final int MODE_OFFSET = VERSION_OFFSET + 4;

  /**
   * The offset of the channel field in the data, in bytes.
   */
    private static final int CHANNEL_OFFSET = MODE_OFFSET + 4;
  
  /**
   * The offset of the data position field in the data, in bytes.
   */
    private static final int DATA_POSITION_OFFSET = CHANNEL_OFFSET + 4;

  /**
   * The offset of the size field in the data, in bytes.
   */
    private static final int SIZE_OFFSET = DATA_POSITION_OFFSET + 4;

  /**
   * The offset of the seconds field in the data, in bytes.
   */
    private static final int SECONDS_OFFSET = SIZE_OFFSET + 4;

  /**
   * The offset of the milliseconds field in the data, in bytes.
   */
    private static final int MILLISECONDS_OFFSET = SECONDS_OFFSET + 4;

  /**
   * The size of an AudioDataStruct without the audio data, in bytes.
   */
    public static final int AUDIO_DATA_STRUCT_SIZE = MILLISECONDS_OFFSET + 4;

  /**
   * The ByteBuffer that holds the data for this AudioDataStruct.
   */
    private final ByteBuffer buffer;

  /**
   * The magic number that identifies the current version of the AudioDataStruct.
   */
    public static final int VERSION = 0x00ABCDEF;

  /**
   * Constructs an AudioDataStruct using the data in the specified ByteBuffer.
   *
   * The first 4 bytes of the ByteBuffer must match the current version of the AudioDataStruct, given by {@link AudioDataStruct}.
   * @param buffer the ByteBuffer to retrieve data from for this AudioDataStruct.
   * @throws NullPointerException if buffer is null.
   */
    public AudioDataStruct( final ByteBuffer buffer )
    {
      CheckParameters.areNotNull(buffer);
      
      this.buffer = IO.duplicate( buffer );
      
      if ( getVersion() != VERSION )
        {
          throw new IllegalArgumentException( Integer.toHexString( getVersion() ) + " unsupported." );
        }
    }

  /**
   * Gets the channel, or camera number, that the data arrived on.
   *
   * @return the channel, or camera number, that the data arrived on.
   */
    public int getChannel()
    {
        return IO.readInt( buffer, CHANNEL_OFFSET );
    }

  /**
   * Gets the milliseconds part of the timestamp for the data.  Note that if there was no AudioDataStruct in the original stream data, the constructed AudioDataStruct will have 0 for this field.
   *
   * @return the milliseconds part of the timestamp for the data.
   */
    public int getMilliseconds()
    {
        return IO.readInt( buffer, MILLISECONDS_OFFSET );
    }

  /**
   * Gets the mode value for this AudioDataStruct.
   *
   * @return the mode value for this AudioDataStruct.
   */
    public int getMode()
    {
        return IO.readInt( buffer, MODE_OFFSET );
    }

  /**
   * Gets the seconds part of the timestamp for the data.  Note that if there was no AudioDataStruct in the original stream data, the constructed AudioDataStruct will have 0 for this field.
   *
   * @return the seconds part of the timestamp for the data.
   */
    public int getSeconds()
    {
        return IO.readInt( buffer, SECONDS_OFFSET );
    }

  /**
   * Gets the size of the data described by this AudioDataStruct, in bytes.
   *
   * @return the size of the data described by this AudioDataStruct, in bytes.
   */
    public int getSize()
    {
        return IO.readInt( buffer, SIZE_OFFSET );
    }

  /**
   * Returns the offset in the ByteBuffer at which the audio data begins, in bytes.
   *
   * @return the offset in the ByteBuffer at which the audio data begins, in bytes.
   */
    public int getStartOffset()
    {
      return IO.readInt( buffer, DATA_POSITION_OFFSET );
    }

  /**
   * Gets the version of the AudioDataStruct that is held in the ByteBuffer.
   *
   * @return the version of the AudioDataStruct that is held in the ByteBuffer.
   */
    public int getVersion()
    {
        return IO.readInt( buffer, VERSION_OFFSET );
    }

  /**
   * Sets the channel, or camera number, for this AudioDataStruct.
   *
   * @param the channel, or camera number, for this AudioDataStruct.
   */
    public void setChannel( final int channel )
    {
        buffer.putInt( CHANNEL_OFFSET, channel );
    }

  /**
   * Sets the milliseconds part of the timestamp for this AudioDataStruct.
   *
   * @param milliseconds the milliseconds part of the timestamp for this AudioDataStruct.
   */
    public void setMilliseconds( final int milliseconds )
    {
        buffer.putInt( MILLISECONDS_OFFSET, milliseconds );
    }

  /**
   * Sets the mode for this AudioDataStruct.
   *
   * @param mode the mode for this AudioDataStruct.
   */
    public void setMode( final int mode )
    {
        buffer.putInt( MODE_OFFSET, mode );
    }

  /**
   * Sets the seconds part of the timestamp for this AudioDataStruct.
   *
   * @param seconds the seconds part of the timestamp for this AudioDataStruct.
   */
    public void setSeconds( final int seconds )
    {
        buffer.putInt( SECONDS_OFFSET, seconds );
    }

    public void setSize( final int size )
    {
        buffer.putInt( SIZE_OFFSET, size );
    }

  /**
   * Sets the offset in the ByteBuffer at which the audio data begins, in bytes.
   *
   * @param startOffset the offset in the ByteBuffer at which the audio data begins, in bytes.
   */
    public void setStartOffset( final int startOffset )
    {
        buffer.putInt( DATA_POSITION_OFFSET, startOffset );
    }
}
