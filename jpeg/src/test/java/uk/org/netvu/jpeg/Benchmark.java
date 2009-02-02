package uk.org.netvu.jpeg;

import javax.swing.*;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.io.*;

public class Benchmark
{
  public static void main(String[] args) throws IOException, InterruptedException
  {
    if (args.length == 0)
    {
      for (int b = 0; b < 4; b ++)
      {
        for (int a = 0; a < 3; a ++)
        {
          Process process = Runtime.getRuntime().exec(new String[]{"java", "-classpath", System.getProperty("java.class.path"), "uk.org.netvu.jpeg.Benchmark", String.valueOf(a), String.valueOf(b)});
          process.waitFor();
          BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
          BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
          String line;
          while ((line = inputStreamReader.readLine())!=null)
            System.out.println(line);
          while ((line = errorStreamReader.readLine())!=null)
            System.err.println(line);
          inputStreamReader.close();
          errorStreamReader.close();
        }
      }
    }
    else
    {
      int whichDecoder = Integer.parseInt(args[0]);
      int whichImage = Integer.parseInt(args[1]);
      
      final String[] filenames = { "192-168-106-206-352x256.jpg", "mews-camvu-1-320x240.jpg", "mews-camvu-2-1600x1200.jpg", "Jpegvergroessert-129x256.jpg" };
      
      final ByteBuffer buffer = bufferFor(filenames[whichImage]);
    
      Runnable[] decoders = { new Runnable() { public void run() { JPEGDecoders.adffmpegDecoder.decode(buffer); } public String toString() { return "adffmpeg"; } },
                              new Runnable() { public void run() { JPEGDecoders.toolkitDecoder.decode(buffer); } public String toString() { return "toolkit"; } },
                              new Runnable() { public void run() { JPEGDecoders.imageIODecoder.decode(buffer); } public String toString() { return "imageio"; } } };
                            
      time(1000, 1000, decoders[whichDecoder], filenames[whichImage]);
    }
  }

  public static ByteBuffer bufferFor(String filename) throws IOException
  {
    return new FileInputStream(filename).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, new File(filename).length());
  }

  public static void time(int iterations, long warmUpMillis, Runnable runnable, String extraInfo)
  {
    long start = System.nanoTime();
    while (System.nanoTime() - start > warmUpMillis * 1000000)
      runnable.run();

    start = System.nanoTime();

    for (int i = 0; i < iterations; i ++)
      runnable.run();

    long time = System.nanoTime() - start;
    System.out.println(extraInfo + ": " + runnable + ": " + time + " for " + iterations + " iterations; " + time / iterations + " nanoseconds (" + time / iterations / 1000000 + " milliseconds ) per iteration or " + iterations * 1.0 / (time / 1000000000.0) + " iterations per second.");
  }
}