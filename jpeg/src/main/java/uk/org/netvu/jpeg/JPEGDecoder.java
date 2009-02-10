package uk.org.netvu.jpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

public interface JPEGDecoder
{
    Image decodeJPEG( ByteBuffer buffer );
}
