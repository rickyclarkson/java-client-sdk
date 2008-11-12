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

enum FrameType
{
    JFIF
    {
        public void deliverTo( InputStream input, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            handler.jfif( new JFIFPacket( IO.readIntoByteBuffer( input, metadata.getLength() ), metadata ) );
        }
    },
    JPEG
    {
        public void deliverTo( InputStream input, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            ImageDataStruct imageDataStruct = new ImageDataStruct( IO.readIntoByteBuffer( input, ImageDataStruct.IMAGE_DATA_STRUCT_SIZE ) );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - ImageDataStruct.IMAGE_DATA_STRUCT_SIZE );
            handler.jfif( JFIFHeader.jpegToJfif( restOfData, metadata, imageDataStruct ) );
        }
    },
    UNKNOWN
    {
        public void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
        }
    };

    public abstract void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException;
}