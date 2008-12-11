package uk.org.netvu.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;

import uk.org.netvu.util.CheckParameters;

/**
 * A JFIFPacket represents a single JFIF frame read from a stream. The JFIF data
 * is available as a ByteBuffer via the method getData().
 */
final class JFIFPacket
        extends Packet
{
    /**
     * The JFIF frame.
     */
    private final ByteBuffer data;

    private final boolean truncated;

    /**
     * Constructs a JFIFPacket with the specified data and metadata.
     * 
     * @param data
     *        the JFIF frame to store.
     * @param metadata
     *        the metadata about the JFIFPacket.
     * @throws NullPointerException
     *         if data or metadata are null.
     */
    JFIFPacket( final ByteBuffer data, final int channel, final boolean truncated )
    {
        super( channel );
        this.truncated = truncated;

        CheckParameters.areNotNull( data );
        this.data = data;
    }

    /**
     * Gets the JFIF frame held by the JFIFPacket.
     * 
     * @return the JFIF frame held by the JFIFPacket.
     */
    @Override
    public ByteBuffer getData()
    {
        return truncated ? JFIFHeader.jpegToJfif( data ) : data.duplicate();
    }

    @Override
    public ByteBuffer getOnWireFormat()
    {
        if ( truncated )
        {
            return data.duplicate();
        }

        final int commentPosition = IO.searchFor( data, JFIFHeader.byteArrayLiteral( new int[] { 0xFF, 0xFE } ) );
        data.position( commentPosition + 2 );
        final int commentLength = data.getShort();
        String comment = IO.bytesToString( IO.readIntoByteArray( data, commentLength ) );

        final VideoFormat videoFormat =
                data.get( IO.searchFor( data, JFIFHeader.byteArrayLiteral( new int[] { 0xFF, 0xC0 } ) ) + 11 ) == 0x22 ? VideoFormat.JPEG_422
                        : VideoFormat.JPEG_411;
        final short targetPixels = data.getShort( IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xC0 } ) + 5 );
        final short targetLines = data.getShort( IO.searchFor( data, new byte[] { (byte) 0xFF, (byte) 0xC0 } ) + 7 );
        final ImageDataStruct imageDataStruct =
                ImageDataStruct.createImageDataStruct( data, comment, videoFormat, targetLines, targetPixels );
        return imageDataStruct.getByteBuffer();
    }
}
