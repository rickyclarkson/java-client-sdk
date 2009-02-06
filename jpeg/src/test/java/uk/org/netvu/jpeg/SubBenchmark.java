package uk.org.netvu.jpeg;

import javax.swing.*;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.io.*;
import uk.org.netvu.util.Function;
import java.awt.Image;

public class SubBenchmark
{
  public static int[] rangeOver(int length)
  {
    int[] indices = new int[length];
    for (int a = 0; a < indices.length; a++)
      indices[a] = a;

    return indices;
  }

  public static final JPEGDecoder[] decoders = { JPEGDecoders.adffmpegDecoder,
                                                 JPEGDecoders.toolkitDecoder,
                                                 JPEGDecoders.imageIODecoder
 };
  static class SampleFile { final int width, height; final String filename; SampleFile(int width, int height, String filename) { this.width = width;
        this.height=height;
        this.filename=filename;
    } }
   
  public static SampleFile[] sampleFiles = { new SampleFile(352, 256, "192-168-106-206-352x256.jpg"),
                                             new SampleFile(320, 240, "mews-camvu-1-320x240.jpg"),
                                             new SampleFile(1600, 1200, "mews-camvu-2-1600x1200.jpg"),
                                             new SampleFile(320, 256, "192-168-106-207-320x256.jpg"),
                                             new SampleFile(352, 256, "dvip3s-ad-dev-adh-352x256.jpeg") };

  public static void main(final String[] args) throws IOException, InterruptedException
  {
    int[] iterationAmounts = { 100, 1000, 10000 };
    int[] warmUpTimes = { 100, 1000, 10000 };
    String[] inputTypes = { "byte[]", "ByteBuffer" };
    
    if (args.length != 0)
    {
      JPEGDecoder decoder = decoders[Integer.parseInt(args[0])];
      SampleFile sampleFile = sampleFiles[Integer.parseInt(args[1])];
      int iterations = iterationAmounts[Integer.parseInt(args[2])];
      int warmUpTime = warmUpTimes[Integer.parseInt(args[3])];
      String inputType = inputTypes[Integer.parseInt(args[4])];
            
      String info = decoder.getClass().getSimpleName() + "," + sampleFile.filename + "," + sampleFile.width + "," + sampleFile.height + "," + inputType + "," + warmUpTime + "," + iterations;

      if (inputType.equals("ByteBuffer"))
        time(iterations, warmUpTime, decoder.decodeByteBuffer, bufferFor(sampleFile.filename), info);
      else
        time(iterations, warmUpTime, decoder.decodeByteArray, byteArrayFor(sampleFile.filename), info);
    }
  }

  public static ByteBuffer bufferFor(String filename) throws IOException
  {
    ByteBuffer first = new FileInputStream(filename).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, new File(filename).length());
    first.position(0);
    ByteBuffer result = ByteBuffer.allocateDirect(first.limit());
    result.put(first);
    result.position(0);
    return result;
  }

  public static byte[] byteArrayFor(String filename) throws IOException
  {
    ByteBuffer buffer = bufferFor(filename);
    byte[] bytes = new byte[buffer.limit()];
    buffer.get(bytes);
    return bytes;
  }

  public static <T> void time(int iterations, long warmUpMillis, Function<T, ?> decoder, T input, String info)
  {
    long start = System.nanoTime();
    while (System.nanoTime() - start < warmUpMillis * 1000000)
      decoder.apply(input);

    humourNetbeans(iterations, decoder, input, info);
  }

  public static <T> void humourNetbeans(int iterations, Function<T, ?> decoder, T input, String info)
  {
    long start = System.nanoTime();
    for (int i = 0; i < iterations; i ++)
      decoder.apply(input);

    long time = System.nanoTime() - start;
    System.out.println(info + "," + iterations * 1.0 / (time / 1000000000.0));
  }
}