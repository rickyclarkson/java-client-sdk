package uk.org.netvu.filter

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import javax.imageio.ImageIO
import java.io.File

class FilterTest extends JUnit4(new Specification {
 val filters: List[(ImageFilter, String)] = List(MonochromeFilter.monochromeFilter(0.5, 0.2, 0.3) -> "MonochromeFilter",
                                                  ContrastFilter.contrastFilter(1.5) -> "ContrastFilter",
                                                  BrightnessFilter.simpleBrightnessFilter(1.5) -> "BrightnessFilter.simpleBrightnessFilter",
                                                  BrightnessFilter.betterBrightnessFilter(30) -> "BrightnessFilter.betterBrightnessFilter")
 for ((filter, name) <- filters)
  name should { "be able to filter an image to give an image of the same dimensions" in {
   val original = ImageIO.read(new File("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg"))
   val result = filter.filter(original)
   result.getWidth(null) mustEqual(original.getWidth(null))
   result.getHeight(null) mustEqual(original.getHeight(null))
  } }
})
