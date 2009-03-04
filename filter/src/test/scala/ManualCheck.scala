package uk.org.netvu.filter

import javax.swing._
import java.awt.{Image, GridLayout}
import javax.imageio._
import java.io._

object ManualCheck { def main(args: Array[String]) = {
 val frame = new JFrame
 frame setLayout new GridLayout(3, 3)
 val filters: List[(ImageFilter, String)] = List(new ImageFilter { def filter(image: Image) = image } -> "No filter",
                                                 new MonochromeFilter(0.5, 0.2, 0.3) -> "Weighted monochrome filter",
                                                 new ContrastFilter(1.5) -> "Contrast",
                                                 BrightnessFilter.simpleBrightnessFilter(1.5) -> "Simple brightness filter",
                                                 BrightnessFilter.betterBrightnessFilter(30) -> "Better brightness filter",
                                                 Filters.andThen(new ContrastFilter(1.5), BrightnessFilter.betterBrightnessFilter(30)) -> "Contrast filter then brightness filter",
                                                 Filters.andThen(new MonochromeFilter(0.5, 0.2, 0.3), BrightnessFilter.betterBrightnessFilter(30)) -> "Monochrome filter then brightness filter")

 for ((filter, name) <- filters)
  frame add { val label = new JLabel(new ImageIcon(filter filter ImageIO.read(new File("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg"))))
              label setToolTipText name
              label }
 frame.pack
 frame setVisible true
} }
