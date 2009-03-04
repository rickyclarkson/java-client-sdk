package uk.org.netvu.jpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import java.awt.image.BufferedImage
import uk.org.netvu.util.Buffers

object DecodingTests {
 val data = List((352, 256, "192-168-106-206-352x256.jpg"),
                 (320, 240, "mews-camvu-1-320x240.jpg"),
                 (1600, 1200, "mews-camvu-2-1600x1200.jpg"),
                 (320, 256, "192-168-106-207-320x256.jpg"),
                 (352, 256, "dvip3s-ad-dev-adh-352x256.jpeg")) map { case (_width, _height, _name) => new { val width = _width
                                                                                                            val height = _height
                                                                                                            val name = _name } }

 def validDecoder(decoder: JPEGDecoder) = new Specification {
  for (file <- data) {
   ("Decoding " + file.name) should {
    "give an Image whose width and height are the same as documented" in {
     decoder.decodeJPEG(Buffers.bufferFor(file.name)) match { case image => image.getWidth(null) mustEqual file.width
                                                             image.getHeight(null) mustEqual file.height }
    }
   }
  }

  for (file <- data) {
   def decodeToIntArray(decoder: JPEGDecoder): Array[Int] = { val image = decoder decodeJPEG Buffers.bufferFor(file.name)
                                                              val bufferedImage = new BufferedImage(image getWidth null, image getHeight null, BufferedImage.TYPE_INT_ARGB)
                                                              bufferedImage.getGraphics.drawImage(image, 0, 0, null)
                                                              val dataBuffer = bufferedImage.getData.getDataBuffer
                                                              val array = new Array[Int](dataBuffer.getSize)
                                                              for (i <- 0 until array.length) array(i) = dataBuffer.getElem(i)
                                                              array }

   val control = decodeToIntArray(ToolkitDecoder.createInstance)
   val fromImageIO = decodeToIntArray(decoder)

   import Math.abs

   def toRGB(i: Int) = (i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF)

   def equal: Boolean = {
    for { i <- 0 until control.length
          (r, g, b) = toRGB(control(i))
          (r2, g2, b2) = toRGB(fromImageIO(i))
          if abs(r2 - r) > 20
          if abs(g2 - g) > 20
          if abs(b2 - b) > 20 } return false
    return true
   }
  
   ("An image decoded by " + decoder) should { "be roughly equal to an image decoded by Toolkit" in {
    equal must beTrue
   } }
  }
 }
}
 
class DecodingTest extends JUnit4(new Specification {
 for (decoder <- List(ToolkitDecoder.createInstance(), ImageIODecoder.createInstance()))
  decoder.toString + isSpecifiedBy(DecodingTests.validDecoder(decoder))
})

