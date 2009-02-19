package uk.org.netvu.mpeg;

import java.nio.ByteBuffer;
import java.awt.Image;

public interface MPEGDecoder
{
  Image decodeMPEG4(ByteBuffer buffer);
}