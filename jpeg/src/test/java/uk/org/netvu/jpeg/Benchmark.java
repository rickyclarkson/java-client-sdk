package uk.org.netvu.jpeg;

import javax.swing.*;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class Benchmark
{
  public static void main(String[] args)
  {
    JFrame frame = new JFrame()
      {{
        add(new JPanel()
          {{
            try
            {
              add(new JLabel(new ImageIcon(JPEGDecoders.toolkitDecoder.decode(new FileInputStream("Jpegvergroessert.jpg").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, new File("Jpegvergroessert.jpg").length())))));
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