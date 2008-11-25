package uk.org.netvu.data;

import static uk.org.netvu.data.ImageDataStruct.IMAGE_DATA_STRUCT_SIZE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An enumeration of the supported frame types from binary, minimal and mime
 * streams.
 */
public enum FrameType
{
    /**
     * A complete JFIF, compatible with most display and image manipulation
     * programs.
     */
    JFIF
    {
        @Override
        public void deliverTo( final StreamHandler handler, final InputStream input, final StreamMetadata metadata )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, metadata );
            handler.jfif( IO.readIntoByteBuffer( input, metadata.getLength() ) );
        }
    },
    /**
     * JFIF data with an AD-specific minimised header.
     */
    JPEG
    {
        @Override
        public void deliverTo( final StreamHandler handler, final InputStream input, final StreamMetadata metadata )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, metadata );
            final ImageDataStruct imageHeader =
                    new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE ) );
            final ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - IMAGE_DATA_STRUCT_SIZE );
            handler.jfif( JFIFHeader.jpegToJfif( restOfData, /* metadata, */imageHeader ) );
        }
    },
    /**
     * An MPEG-4 I-frame or P-frame.
     */
    MPEG4
    {
        @Override
        public void deliverTo( final StreamHandler handler, final InputStream input, final StreamMetadata metadata )
                throws IOException
        {
            CheckParameters.areNotNull( handler, input, metadata );
            final ImageDataStruct imageHeader =
                    new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE ) );
            final ByteBuffer commentData = IO.readIntoByteBuffer( input, imageHeader.getStartOffset() );
            final ByteBuffer restOfData =
                    IO.readIntoByteBuffer( input, metadata.getLength() - ImageDataStruct.IMAGE_DATA_STRUCT_SIZE
                            - imageHeader.getStartOffset() );
            handler.mpeg4( new MPEG4Packet( restOfData, metadata, imageHeader, commentData ) );
        }
    },
    /**
     * Information (such as comments about the other data).
     */
    INFO
    {
        @Override
        public void deliverTo( final StreamHandler handler, final InputStream data, final StreamMetadata metadata )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, metadata );
            handler.info( IO.readIntoByteBuffer( data, metadata.getLength() ) );
        }
    },
    /**
     * Unknown data. This should not be seen in normal circumstances.
     */
    UNKNOWN
    {
        @Override
        public void deliverTo( final StreamHandler handler, final InputStream data, final StreamMetadata metadata )
                throws IOException
        {
            CheckParameters.areNotNull( handler, data, metadata );
            handler.dataArrived( IO.readIntoByteBuffer( data, metadata.getLength() ), metadata );
        }
    };

    /**
     * Gives the type of frame, according to the numbers used in the minimal and
     * binary stream formats.
     * 
     * @param value
     *        the numeric value to find a matching FrameType for.
     * @return the FrameType that corresponds with value, or UNKNOWN if none
     *         exists.
     */
    static FrameType frameTypeFor( final int value )
    {
        switch ( value )
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

    /**
     * Delivers a frame of this type of data to the passed-in StreamHandler.
     * 
     * @param handler
     *        the StreamHandler to deliver data to.
     * @param data
     *        the InputStream to read data from.
     * @param metadata
     *        information about the packet of data.
     * @throws IOException
     *         if there are any I/O errors.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    public abstract void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata )
            throws IOException;
}
