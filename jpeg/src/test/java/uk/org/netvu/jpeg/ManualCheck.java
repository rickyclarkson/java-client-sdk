package uk.org.netvu.jpeg;

import javax.swing.*;
import java.awt.*;
import uk.org.netvu.jpeg.Benchmark.SampleFile;
import java.io.*;

public class ManualCheck
{
  public static void main(String[] args)
  {
    new JFrame()
    {
      {
        setSize(800, 600);
        add(new JScrollPane(new JPanel(new GridLayout(Benchmark.decoders.length, Benchmark.sampleFiles.length))
          {
            {
              try
              {
                for (JPEGDecoder decoder: Benchmark.decoders)
                  for (SampleFile sampleFile: Benchmark.sampleFiles)
                    add(new JLabel(new ImageIcon(decoder.decodeByteArray(Benchmark.byteArrayFor(sampleFile.filename)))));
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