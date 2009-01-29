package uk.org.netvu.jpeg;

import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;

public interface JPEGDecoder
{
  BufferedImage decode(ByteBuffer buffer);
}