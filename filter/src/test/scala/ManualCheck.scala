package uk.org.netvu.filter

import javax.swing._
import java.awt.{Image, GridLayout}
import javax.imageio._
import java.io._

object ManualCheck { def main(args: Array[String]) = {
 def andThen(first: ImageFilter, second: ImageFilter) = new ImageFilter { def filter(pixels: Array[Int]) = first filter second.filter(pixels) }

 val frame = new JFrame
 frame setLayout new GridLayout(3, 3)
 import Utils.pixelsFor
 import Filters._
 val filters: List[(ImageFilter, String)] = List(new ImageFilter { def filter(pixels: Array[Int]) = pixels } -> "No filter",
                                                 monochromeFilter(1.0/3, 1.0/3, 1.0/3) -> "Averaged monochrome filter",
                                                 standardLuminanceMonochromeFilter -> "Standard luminance monochrome filter",
                                                 contrastFilter(1.5) -> "Contrast",
                                                 simpleBrightnessFilter(1.5) -> "Simple brightness filter",
                                                 betterBrightnessFilter(30) -> "Better brightness filter",
                                                 andThen(contrastFilter(1.5), betterBrightnessFilter(30)) -> "Contrast filter then brightness filter",
                                                 andThen(monochromeFilter(0.5, 0.2, 0.3), betterBrightnessFilter(30)) -> "Monochrome filter then brightness filter")
 import java.awt.image.BufferedImage
 def filterImage(image: Image, filter: ImageFilter) = { val result = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)
                                                        for ((pixel, index) <- filter.filter(pixelsFor(image)) zipWithIndex) {
                                                         result.setRGB(index / image.getWidth(null), index % image.getWidth(null), pixel)
                                                        }
                                                        result }
 for ((filter, name) <- filters)
  frame add { val label = new JLabel(new ImageIcon(filterImage(ImageIO.read(new File("../codecs/dvip3s-ad-dev-adh-352x256.jpeg")), filter)))
              label setToolTipText name
              label }
 frame.pack
 frame setVisible true
} }
