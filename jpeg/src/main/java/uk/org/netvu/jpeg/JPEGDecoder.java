package uk.org.netvu.jpeg;

import java.nio.ByteBuffer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import uk.org.netvu.util.Function;
import java.io.InputStream;

public abstract class JPEGDecoder
{
  public abstract Image decodeByteBuffer(ByteBuffer buffer);
  public abstract Image decodeByteArray(byte[] array);

  final Function<ByteBuffer, Image> decodeByteBuffer = new Function<ByteBuffer, Image>() { public Image apply(ByteBuffer buffer) { return decodeByteBuffer(buffer); } };
  final Function<byte[], Image> decodeByteArray = new Function<byte[], Image>() { public Image apply(byte[] array) { return decodeByteArray(array); } };
}