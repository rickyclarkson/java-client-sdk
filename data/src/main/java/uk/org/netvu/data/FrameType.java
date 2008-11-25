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

enum FrameType
{
    JFIF
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata ) throws IOException
        {
            handler.jfif( new JFIFPacket( IO.readIntoByteBuffer( input, metadata.getLength() ), metadata ) );
        }
    },
    JPEG
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata ) throws IOException
        {
            ImageDataStruct imageHeader = new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE ) );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - IMAGE_DATA_STRUCT_SIZE );
            handler.jfif( JFIFHeader.jpegToJfif( restOfData, metadata, imageHeader ) );
        }
    },
    MPEG4
    {
        public void deliverTo( StreamHandler handler, InputStream input, StreamMetadata metadata) throws IOException
        {
            ImageDataStruct imageHeader = new ImageDataStruct( IO.readIntoByteBuffer( input, IMAGE_DATA_STRUCT_SIZE));
            ByteBuffer commentData = IO.readIntoByteBuffer( input, imageHeader.startOffset );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - ImageDataStruct.IMAGE_DATA_STRUCT_SIZE - imageHeader.startOffset);
            handler.mpeg4(new MPEG4Packet(restOfData, metadata, imageHeader, commentData));
        }
    },
    INFO
    {
        public void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata) throws IOException
        {
            handler.info( IO.readIntoByteBuffer(data, metadata.getLength()) );
        }
    },
    UNKNOWN
    {
        public void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata ) throws IOException
        {
            handler.dataArrived( IO.readIntoByteBuffer(data, metadata.getLength()), metadata);
        }
    };

    public abstract void deliverTo( StreamHandler handler, InputStream data, StreamMetadata metadata ) throws IOException;

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