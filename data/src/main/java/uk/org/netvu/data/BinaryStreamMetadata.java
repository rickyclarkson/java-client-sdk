package uk.org.netvu.data;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import uk.org.netvu.util.CheckParameters;

/**
 * Data about an individual packet of data obtained from a 'binary' stream.
 */
public final class BinaryStreamMetadata implements StreamMetadata
{
    /**
     * The type of frame this packet holds.
     */
    private final FrameType frameType;

    /**
     * The channel this packet is on.
     */
    private final int channel;

    /**
     * The length of this packet, in bytes, not including the 6-byte header that a 'binary' stream packet has.
     */
    private final int length;

    /**
     * Constructs a BinaryStreamMetadata by reading the frame type, channel and length from the passed-in InputStream.
     * @param input the InputStream to read metadata from.
     * @throws IOException if any I/O errors occur.
     * @throws NullPointerException if input is null.
     */
    public BinaryStreamMetadata( InputStream input ) throws IOException
    {
        CheckParameters.areNotNull(input);
        frameType = FrameType.frameTypeFor(input.read() & 0xFF);
        channel = input.read() + 1;
        length = new DataInputStream( input ).readInt();
    }

    /**
     * Gets the length of this packet, in bytes, not including the 6-byte header that a 'binary' stream packet has.
     *
     * @return the length of this packet.
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Gets the channel this packet is on.
     * 
     * @return the channel this packet is on.
     */
    public int getChannel()
    {
        return channel;
    }
    
    /**
     * Gets the type of frame this packet holds.
     *
     * @return the type of frame this packet holds.
     */
    public FrameType getFrameType()
    {
        return frameType;
    }
}