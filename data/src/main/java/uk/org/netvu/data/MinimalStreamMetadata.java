package uk.org.netvu.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import uk.org.netvu.util.CheckParameters;

/**
 * Data about an individual packet parsed from a 'minimal' binary stream.
 */
final class MinimalStreamMetadata implements StreamMetadata
{
    /**
     * The type of data contained in this packet.
     */
    private final FrameType frameType;

    /**
     * The length of the data contained in this packet.
     */
    private final int length;

    /**
     * The channel this packet is on.
     */
    private final int channel;

    /**
     * Constructs a MinimalStreamMetadata using the data read from the specified
     * InputStream.
     * 
     * @param input
     *        the InputStream to read data from.
     * @throws IOException
     *         if an I/O error occurs.
     * @throws NullPointerException
     *         if input is null.
     */
    public MinimalStreamMetadata( final InputStream input ) throws IOException
    {
        CheckParameters.areNotNull( input );

        switch ( input.read() )
        {
            case 0:
                frameType = FrameType.JPEG;
                break;
            case 1:
                frameType = FrameType.JFIF;
                break;
            default:
                frameType = FrameType.UNKNOWN;
        }

        final DataInputStream data = new DataInputStream( input );
        channel = data.read();
        length = data.readInt();
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
     * Gets the type of data contained in this packet.
     * 
     * @return the type of data contained in this packet.
     */
    public FrameType getFrameType()
    {
        return frameType;
    }

    /**
     * Gets the length of this packet.
     * 
     * @return the length of this packet.
     */
    public int getLength()
    {
        return length;
    }
}
