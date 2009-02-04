package uk.org.netvu.jpeg;

import javax.swing.*;
import java.awt.*;
import uk.org.netvu.jpeg.Benchmark.Resolution;
import java.io.*;

public class ManualCheck
{
  public static void main(String[] args)
  {
    new JFrame()
    {
      {
        setSize(800, 600);
        add(new JScrollPane(new JPanel(new GridLayout(Benchmark.decoders.length, Benchmark.resolutions.length))
          {
            {
              try
              {
                for (JPEGDecoder decoder: Benchmark.decoders)
                  for (Resolution resolution: Benchmark.resolutions)
                    add(new JLabel(new ImageIcon(decoder.decodeByteArray(Benchmark.byteArrayFor(resolution.filename)))));
              }
              catch (IOException e)
                {
                  throw new RuntimeException(e);
                }
            }
          }));
      }
    }.setVisible(true);
  }
}