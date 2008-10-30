package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.IOException;

public enum FrameType
{
    JPEG
    {
        public void deliverTo( InputStream input, int length, Handler handler ) throws IOException
        {
            ByteBuffer buffer = ByteBuffer.allocate( length );
            int read = Channels.newChannel( input ).read( buffer );
            buffer.position(0);
            handler.jpeg( new JPEGPacket( buffer, length ) );
        }
    };

    public abstract void deliverTo( InputStream input, int length, Handler handler ) throws IOException;
}