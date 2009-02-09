package uk.org.netvu.jpeg;

import java.awt.Image;
import java.nio.ByteBuffer;

import uk.org.netvu.util.Function;

public interface JPEGDecoder
{
    Image decodeJPEG( ByteBuffer buffer );
}
