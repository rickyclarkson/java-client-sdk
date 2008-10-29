package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.IOException;

public enum DataType
{
    BINARY
    {
        public Parser parser()
        {
            return new Parser()
            {
                public void parse( final InputStream input, final Handler handler ) throws IOException
                {
                    while ( true )
                    {
                        BinaryStreamHeader header = new BinaryStreamHeader( input );
                        handler.binaryStreamHeader( header );
                        header.frameType.deliverTo( input, header.dataLength, handler );
                    }
                }
            };
        }
    },

    MIME
    {
        public Parser parser()
        {
            return new Parser()
            {
                public void parse( final InputStream input, final Handler handler ) throws IOException
                {    
                    new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                impl();
                            }
                            catch ( IOException e )
                            {
                                throw new RuntimeException( e );
                            }
                        }

                        private void impl() throws IOException
                        {
                            expectLine( "" );
                            while ( true )
                            {
                                expectLineMatching( "--.*" );
                                expectLineMatching( "HTTP/1\\.[01] 200 .*" );
                                expectLine( "Server: ADH-Web" );
                                expectLine( "Content-type: image/jpeg" );
                                expectString( "Content-length: " );
                                final int length = expectIntFromRestOfLine();
                                expectLine( "" );
                                ByteBuffer jpeg = jpegParse( length );
                                handler.jpeg(new JPEGPacket(jpeg, length));
                                expectLine( "" );
                            }
                        }

                        private String readLine() throws IOException
                        {
                            final int b = input.read();
                            if ( b=='\n' )
                            {
                                return "";
                            }
                            if ( b=='\r' )
                            {
                                return readLine();
                            }
                            if ( b==-1 )
                            {
                                throw null;
                            }
                            return String.valueOf( (char)b )+readLine();
                        }

                        private void expectLine(final String expected) throws IOException
                        {
                            final String line = readLine();
                            if (!expected.equals(line))
                            {
                                throw new IOException( expected + " expected, " + line + " found" );
                            }
                        }

                        private void expectLineMatching(final String regex) throws IOException
                        {
                            final String line = readLine();
                            if (!line.matches(regex))
                            {
                                throw new IOException(regex + " expected, " + line + " found");
                            }
                        }

                        private void expectString( final String string) throws IOException
                        {
                            if ( string.equals(""))
                            {
                                return;
                            }

                            if (input.read() != string.charAt(0))
                            {
                                throw null;
                            }

                            if (string.length() > 1)
                                expectString(string.substring(1));
                        }

                        private int expectIntFromRestOfLine() throws IOException
                        {
                            return Integer.parseInt(readLine());
                        }

                        private ByteBuffer jpegParse( final int length ) throws IOException
                        {
                            ByteBuffer buffer = ByteBuffer.allocate( length );
                            Channels.newChannel( input ).read(buffer);
                            buffer.position(0);
                            return buffer;
                        }
                    }.run();
                }
            };
        }
    };

    public abstract Parser parser();
}
