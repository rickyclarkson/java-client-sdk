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
      for (int a = 0; a < 3; a ++)
      {
        Process process = Runtime.getRuntime().exec(new String[]{"java", "-classpath", System.getProperty("java.class.path"), "uk.org.netvu.jpeg.Benchmark", String.valueOf(a)});
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
    else
    {
      int which = Integer.parseInt(args[0]);
      final ByteBuffer buffer = new FileInputStream("Jpegvergroessert.jpg").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, new File("Jpegvergroessert.jpg").length());
    
      Runnable[] decoders = { new Runnable() { public void run() { JPEGDecoders.adffmpegDecoder.decode(buffer); } },
                              new Runnable() { public void run() { JPEGDecoders.toolkitDecoder.decode(buffer); } },
                              new Runnable() { public void run() { JPEGDecoders.imageIODecoder.decode(buffer); } } };
                            
      time(1000, 1000, decoders[which]);
    }
  }

  public static void time(int iterations, int warmUpMillis, Runnable runnable)
  {
    long start = System.nanoTime();
    while (start + System.nanoTime() < start + warmUpMillis * 1000000)
      runnable.run();

    start = System.nanoTime();

    for (int i = 0; i < iterations; i ++)
      runnable.run();

    long time = System.nanoTime() - start;
    System.out.println(time + " for " + iterations + " iterations; " + time / iterations + " nanoseconds (" + time / iterations / 1000000 + " milliseconds ) per iteration.");
  }

  public static void main2(String[] args)
  {
    JFrame frame = new JFrame()
      {{
        add(new JPanel()
          {{
            try
            {
              add(new JLabel(new ImageIcon(JPEGDecoders.adffmpegDecoder.decode(new FileInputStream("Jpegvergroessert.jpg").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, new File("Jpegvergroessert.jpg").length())))));
            }
            catch (IOException e)
              {
                throw new RuntimeException(e);
              }
          }});
      }};
    frame.setSize(400, 400);
    frame.setVisible(true);
  }
}