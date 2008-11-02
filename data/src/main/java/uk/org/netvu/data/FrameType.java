package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.IOException;

public enum FrameType
{
    JFIF
    {
        public void deliverTo( InputStream input, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            //ByteBuffer imageMetadataBuffer = ByteBuffer.allocate( ImageData.IMAGE_DATA_SIZE );
            ReadableByteChannel channel = Channels.newChannel( input );
            //channel.read( imageMetadataBuffer );
            //imageMetadataBuffer.position(0);
            ByteBuffer buffer = ByteBuffer.allocate( metadata.getLength() );// - ImageData.IMAGE_DATA_SIZE );
            channel.read( buffer );
            buffer.position(0);
            handler.jfif( new JPEGPacket( buffer, metadata ) );
        }
    },

    UNKNOWN
    {
        public void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            ByteBuffer buffer = ByteBuffer.allocate( metadata.getLength() );
            Channels.newChannel( data ).read( buffer );
            buffer.position( 0 );
            handler.unknown( buffer, metadata );
        }
    };

    public abstract void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException;
}