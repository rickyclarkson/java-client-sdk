package uk.org.netvu.jpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

import uk.org.netvu.util.Function;

public abstract class JPEGDecoder
{
    public abstract Image decode( ByteBuffer buffer );

    abstract Image decodeByteArray( byte[] array );

    final Function<ByteBuffer, Image> decode = new Function<ByteBuffer, Image>()
    {
        @Override
        public Image apply( final ByteBuffer buffer )
        {
            return decode( buffer );
        }
    };

    final Function<byte[], Image> decodeByteArray = new Function<byte[], Image>()
    {
        @Override
        public Image apply( final byte[] array )
        {
            return decodeByteArray( array );
        }
    };
}
