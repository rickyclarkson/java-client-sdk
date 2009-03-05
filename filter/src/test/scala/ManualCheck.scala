package uk.org.netvu.filter

import javax.swing._
import java.awt.{Image, GridLayout}
import javax.imageio._
import java.io._

object ManualCheck { def main(args: Array[String]) = {
 def andThen(first: ImageFilter, second: ImageFilter) = new ImageFilter { def filter(image: Image) = first filter second.filter(image) }

 val frame = new JFrame
 frame setLayout new GridLayout(3, 3)
 val filters: List[(ImageFilter, String)] = List(new ImageFilter { def filter(image: Image) = image } -> "No filter",
                                                 MonochromeFilters.monochromeFilter(1.0/3, 1.0/3, 1.0/3) -> "Averaged monochrome filter",
                                                 MonochromeFilters.standardLuminanceMonochromeFilter -> "Standard luminance monochrome filter",
                                                 ContrastFilters.contrastFilter(1.5) -> "Contrast",
                                                 BrightnessFilters.simpleBrightnessFilter(1.5) -> "Simple brightness filter",
                                                 BrightnessFilters.betterBrightnessFilter(30) -> "Better brightness filter",
                                                 andThen(ContrastFilters.contrastFilter(1.5), BrightnessFilters.betterBrightnessFilter(30)) -> "Contrast filter then brightness filter",
                                                 andThen(MonochromeFilters.monochromeFilter(0.5, 0.2, 0.3), BrightnessFilters.betterBrightnessFilter(30)) -> "Monochrome filter then brightness filter")

 for ((filter, name) <- filters)
  frame add { val label = new JLabel(new ImageIcon(filter filter ImageIO.read(new File("../codecs/dvip3s-ad-dev-adh-352x256.jpeg"))))
              label setToolTipText name
              label }
 frame.pack
 frame setVisible true
} }
