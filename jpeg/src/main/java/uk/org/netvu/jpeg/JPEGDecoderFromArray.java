package uk.org.netvu.jpeg;

import java.awt.Image;

public interface JPEGDecoderFromArray
{
  Image decodeJPEGFromArray(byte[] array);
}