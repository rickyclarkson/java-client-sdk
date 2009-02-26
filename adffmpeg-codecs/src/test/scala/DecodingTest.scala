package uk.org.netvu.adffmpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import uk.org.netvu.util.Buffers
import uk.org.netvu.jpeg.JPEGDecoder

class DecodingTest extends JUnit4(new Specification {
 for (decoder <- List[JPEGDecoder](ADFFMPEGDecoder.getInstance()))
  decoder.toString + isSpecifiedBy(ValidDecoder(decoder))
})

case class ValidDecoder(decoder: JPEGDecoder) extends Specification {
 for (file <- List(
  (352, 256, "../jpeg/192-168-106-206-352x256.jpg" ),
  ( 320, 240, "../jpeg/mews-camvu-1-320x240.jpg" ),
  ( 1600, 1200, "../jpeg/mews-camvu-2-1600x1200.jpg" ),
  ( 320, 256, "../jpeg/192-168-106-207-320x256.jpg" ),
  ( 352, 256, "../jpeg/dvip3s-ad-dev-adh-352x256.jpeg" )
 )) {
  ("Decoding " + file._3) should {
   "give an Image whose width and height are the same as documented" in {
    decoder.decodeJPEG(Buffers.bufferFor(file._3)) match { case image => image.getWidth(null) mustEqual file._1
                                                           image.getHeight(null) mustEqual file._2 }
   }
  }
 }
}
