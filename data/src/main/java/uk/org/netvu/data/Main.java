package uk.org.netvu.data;

import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.io.IOException;
import java.io.FileOutputStream;

public class Main
{
    public static void main( final String[] args ) throws Exception
    {
        System.out.println("Aquí");
        try
        {
            doIt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            main2(args);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void doIt() throws IOException
    {
        final URL url = new URL( "file:testdata/192-168-106-204-binary-jfif" );
        URLConnection connection = url.openConnection();

        ParserFactory.parserFor( DataType.BINARY ).parse( connection.getInputStream(), new StreamHandler()
        {
            int index = 0;
            public void jfif( final JPEGPacket packet ) throws IOException
            {
                final FileOutputStream out = new FileOutputStream( System.getProperty( "user.dir" ) + 
                        "/deleteme" + index++ + ".jpg" );
                ByteChannel channel = out.getChannel();
                int total = 0;

                channel.write( packet.byteBuffer );

                channel.close();                
                out.close();
            }

            public void unknown( ByteBuffer data, StreamMetadata metadata )
            {
                System.out.println(metadata.getLength() + " bytes of unknown data received");
            }
        } );
    }

    public static void main2(String[] args) throws Exception
    {
        final URL url = new URL("file:testdata/192-168-106-204");
        URLConnection connection = url.openConnection();

        ParserFactory.parserFor( DataType.MIME ).parse( connection.getInputStream(), new StreamHandler()
        {
            int index = 0;
            public void jfif( final JPEGPacket packet ) throws IOException
            {
                final FileOutputStream out = new FileOutputStream( System.getProperty("user.dir")+"/deletehim"+index+++".jpg");
                ByteChannel channel=out.getChannel();
                int total = 0;
                channel.write(packet.byteBuffer);

                channel.close();
                out.close();
            }

            public void unknown( final ByteBuffer data, StreamMetadata metadata )
            {
            }
        });
    }
}
