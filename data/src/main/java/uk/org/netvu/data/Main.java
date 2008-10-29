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
        final URL url = new URL( "file:testdata/192-168-106-206-binary-jpeg" );
        URLConnection connection = url.openConnection();

        ParserFactory.parserFor( DataType.BINARY ).parse( connection.getInputStream(), new Handler()
        {
            int index = 0;
            public void jpeg( final JPEGPacket packet ) throws IOException
            {
                final FileOutputStream out = new FileOutputStream( System.getProperty( "user.dir" ) + 
                        "/deleteme" + index++ + ".jpg" );
                ByteChannel channel = out.getChannel();
                int total = 0;

                while ( ( total += channel.write( packet.byteBuffer ) ) < packet.length )
                {
                    System.out.println( "Still writing: total is "+total );
                }

                channel.close();                
                out.close();
            }

            public void binaryStreamHeader( BinaryStreamHeader header )
            {
                System.out.println( "A header received" );
                System.out.println( "frametype: "+header.frameType );
                System.out.println( "channel: "+header.channel );
                System.out.println( "dataLength: "+header.dataLength );
            }
        } );
    }

    public static void main2(String[] args) throws Exception
    {
        final URL url = new URL("file:testdata/192-168-106-204");
        URLConnection connection = url.openConnection();

        ParserFactory.parserFor( DataType.MIME ).parse( connection.getInputStream(), new Handler()
        {
            int index = 0;
            public void jpeg( final JPEGPacket packet ) throws IOException
            {
                final FileOutputStream out = new FileOutputStream( System.getProperty("user.dir")+"/deletehim"+index+++".jpg");
                ByteChannel channel=out.getChannel();
                int total = 0;
                while ((total+=channel.write(packet.byteBuffer))<packet.length)
                {
                    System.out.println("Still writing: total is "+total);
                }

                channel.close();
                out.close();
            }

            public void binaryStreamHeader(BinaryStreamHeader header)
            {
                throw null;
            }
        });
    }
}
