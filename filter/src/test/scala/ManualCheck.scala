package uk.org.netvu.filter

import javax.swing._
import java.awt.{Image, GridLayout}
import javax.imageio._
import java.io._

object ManualCheck { def main(args: Array[String]) = {
 def andThen(first: ImageFilter, second: ImageFilter) = new ImageFilter { def filter(image: Image) = first filter second.filter(image) }

 val frame = new JFrame
 frame setLayout new GridLayout(3, 3)
 import Filters._
 val filters: List[(ImageFilter, String)] = List(new ImageFilter { def filter(image: Image) = image } -> "No filter",
                                                 monochromeFilter(1.0/3, 1.0/3, 1.0/3) -> "Averaged monochrome filter",
                                                 standardLuminanceMonochromeFilter -> "Standard luminance monochrome filter",
                                                 contrastFilter(1.5) -> "Contrast",
                                                 simpleBrightnessFilter(1.5) -> "Simple brightness filter",
                                                 betterBrightnessFilter(30) -> "Better brightness filter",
                                                 andThen(contrastFilter(1.5), betterBrightnessFilter(30)) -> "Contrast filter then brightness filter",
                                                 andThen(monochromeFilter(0.5, 0.2, 0.3), betterBrightnessFilter(30)) -> "Monochrome filter then brightness filter")

 for ((filter, name) <- filters)
  frame add { val label = new JLabel(new ImageIcon(filter filter ImageIO.read(new File("../codecs/dvip3s-ad-dev-adh-352x256.jpeg"))))
              label setToolTipText name
              label }
 frame.pack
 frame setVisible true
} }
