package uk.org.netvu.filter

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Image

class FilterTest extends JUnit4(new Specification {
 import Filters._
 val filters: List[(ImageFilter, String)] = List(monochromeFilter(0.5, 0.2, 0.3) -> "monochromeFilter",
                                                 contrastFilter(1.5) -> "contrastFilter",
                                                 simpleBrightnessFilter(1.5) -> "simpleBrightnessFilter",
                                                 betterBrightnessFilter(30) -> "betterBrightnessFilter")

 val original = ImageIO.read(new File("../codecs/dvip3s-ad-dev-adh-352x256.jpeg"))

 for ((filter, name) <- filters)
  name should { "be able to filter an image to give an image of the same dimensions" in {
   val original = ImageIO.read(new File("../codecs/dvip3s-ad-dev-adh-352x256.jpeg"))
   val result = filter.filter(original)
   result.getWidth(null) mustEqual(original.getWidth(null))
   result.getHeight(null) mustEqual(original.getHeight(null))
  } }

 "Filters.bindToWithin(x, 0, 255)" should {
  "always give numbers between 0 and 255" in {
   for (x <- List(-10, -1, 0, 1, 120, 254, 255, 256, 10000)) {
    Filters.bindToWithin(x, 0, 255) >= 0 mustBe true
    Filters.bindToWithin(x, 0, 255) <= 255 mustBe true
   }
  }
  "be the identity function for numbers between 0 and 255" in {
   for (x <- 0 to 255)
    Filters.bindToWithin(x, 0, 255) mustEqual x
  }
 }

 def pixelData(image: Image) = {
  val bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB)
  bufferedImage.getGraphics.drawImage(image, 0, 0, null)
  bufferedImage.getData.getDataBuffer
 }

 def pixelsFor(image: Image) = { val dataBuffer = pixelData(image)
                                 for (a <- 0 until dataBuffer.getSize) yield dataBuffer.getElem(a) }

 // Gives the brightness of the image, just a total of the pixel intensities.
 // The actual value isn't important, only that it is greater for a brighter image.
 // The comparison would be invalid for images of different sizes.
 def brightness(image: Image) = {
  val dataBuffer = pixelData(image)
  
  val pixels = for (a <- 0 until dataBuffer.getSize) yield dataBuffer.getElem(a)
  pixels.foldLeft(0.0)((acc, x) => acc + (x >> 16 & 0xFF) + (x >> 8 & 0xFF) + (x & 0xFF))
 }
                                      
 "simpleBrightnessFilter(1.5)" should {
  "make the image brighter" in {
   brightness(simpleBrightnessFilter(1.5).filter(original)) > brightness(original) mustBe true
  }
 }

 "betterBrightnessFilter(40)" should {
  "make the image brighter" in {
   brightness(betterBrightnessFilter(40).filter(original)) > brightness(original) mustBe true
  }
 }

 // Gives the contrast of the image as a total of the difference between each pixel and the mean
 // for each colour component.  The actual value isn't important, only that it is greater for an
 // image with more contrast.
 // The comparison would be invalid for images of different sizes.
 def contrast(image: Image) = {
  val dataBuffer = pixelData(image)
  val pixels = for (a <- 0 until dataBuffer.getSize) yield dataBuffer.getElem(a)
  var average = (0.0, 0.0, 0.0)
  for (p <- pixels)
   average = average match { case (r, g, b) => (r + ((p >> 16) & 0xFF),
                                                g + ((p >> 8) & 0xFF),
                                                b + (p & 0xFF)) }
  average = average match { case (r, g, b) => (r / pixels.length, g / pixels.length, b / pixels.length) }
  var contrast = 0.0
  for (p <- pixels)
   contrast += (average match { case (r, g, b) => Math.abs(r - ((p >> 16) & 0xFF)) + Math.abs(g - ((p >> 8) & 0xFF)) + Math.abs(b - (p & 0xFF)) })

  contrast
 }

 "contrastFilter(1.5)" should {
  "make the image have more contrast" in {
   contrast(contrastFilter(1.5).filter(original)) > contrast(original) mustBe true
  }
 }

 // Tests that the image is greyscale, i.e., that red, green and blue are equal for each pixel.
 def grey(image: Image) = !pixelsFor(image).exists(p => (p & 0xFF) != ((p >> 8) & 0xFF) || (p & 0xFF) != ((p >> 16) & 0xFF))

 "monochromeFilter(0.6, 0.3, 0.1)" should {
  "produce a grey image" in {
   grey(monochromeFilter(0.6, 0.3, 0.1).filter(original)) mustBe true
  }
 }
})
