package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.text.SimpleDateFormat;
import static uk.org.netvu.data.ImageDataStruct.IMAGE_DATA_STRUCT_SIZE;
import uk.org.netvu.util.CheckParameters;

/**
 * An enumeration of the supported frame types from binary, minimal and mime streams.
 */
public enum FrameType
{
    /**
     * A complete JFIF, compatible with most display and image manipulation programs.
     */
    JFIF
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata ) throws IOException
        {
            CheckParameters.areNotNull(handler, input, metadata);
            handler.jfif( IO.readIntoByteBuffer( input, metadata.getLength() ) );
        }
    },
    /**
     * JFIF data with an AD-specific minimised header.
     */
    JPEG
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata ) throws IOException
        {
            CheckParameters.areNotNull(handler, input, metadata);
            ImageDataStruct imageHeader = new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE ) );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - IMAGE_DATA_STRUCT_SIZE );
            handler.jfif( JFIFHeader.jpegToJfif( restOfData, /*metadata, */imageHeader ) );
        }
    },
    /**
     * An MPEG-4 I-frame or P-frame.
     */
    MPEG4
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata) throws IOException
        {
            CheckParameters.areNotNull(handler, input, metadata);
            ImageDataStruct imageHeader = new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE));
            ByteBuffer commentData = IO.readIntoByteBuffer( input, imageHeader.getStartOffset() );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - ImageDataStruct.IMAGE_DATA_STRUCT_SIZE - imageHeader.getStartOffset());
            handler.mpeg4(new MPEG4Packet(restOfData, metadata, imageHeader, commentData));
        }
    },
    /**
     * Information (such as comments about the other data).
     */
    INFO
    {
        public void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata) throws IOException
        {
            CheckParameters.areNotNull(handler, data, metadata);
            handler.info( IO.readIntoByteBuffer(data, metadata.getLength()) );
        }
    },
    /**
     * Unknown data.  This should not be seen in normal circumstances.
     */
    UNKNOWN
    {
        public void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata ) throws IOException
        {
            CheckParameters.areNotNull(handler, data, metadata);
            handler.dataArrived( IO.readIntoByteBuffer(data, metadata.getLength()), metadata);
        }
    };

    /**
     * Delivers a frame of this type of data to the passed-in StreamHandler.
     * @param handler the StreamHandler to deliver data to.
     * @param data the InputStream to read data from.
     * @param metadata information about the packet of data.
     * @throws IOException if there are any I/O errors.
     * @throws NullPointerException if any of the parameters are null.
     */
    public abstract void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata ) throws IOException;

    /**
     * Gives the type of frame, according to the numbers used in the minimal and binary stream formats.
     * @param value the numeric value to find a matching FrameType for.
     * @return the FrameType that corresponds with value, or UNKNOWN if none exists.
     */
    static FrameType frameTypeFor(int value)
    {
        switch (value)
        {
        case 0:
            return FrameType.JPEG;
        case 1:
            return FrameType.JFIF;
        case 2:
        case 3:
            return FrameType.MPEG4;
        case 9:
            return FrameType.INFO;
        default:
            return FrameType.UNKNOWN;
        }
    }
}