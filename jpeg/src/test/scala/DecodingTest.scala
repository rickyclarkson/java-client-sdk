package uk.org.netvu.jpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import uk.org.netvu.util.Buffers

class DecodingTest extends JUnit4(new Specification {
 for { decoder <- List[JPEGDecoder with JPEGDecoderFromArray](ToolkitDecoder.getInstance(), ImageIODecoder.getInstance())
       file <- List((352, 256, "192-168-106-206-352x256.jpg" ),
                    ( 320, 240, "mews-camvu-1-320x240.jpg" ),
                    ( 1600, 1200, "mews-camvu-2-1600x1200.jpg" ),
                    ( 320, 256, "192-168-106-207-320x256.jpg" ),
                    ( 352, 256, "dvip3s-ad-dev-adh-352x256.jpeg" )) } {
        ("Decoding " + file._3) should {
         "give an Image whose width and height are the same as documented" in {
          { val image = JPEGDecoders.decodeJPEG(decoder).apply(Buffers.bufferFor(file._3))
           image.getWidth(null) mustEqual file._1
           image.getHeight(null) mustEqual file._2 }
          { val image = JPEGDecoders.decodeJPEGFromArray(decoder).apply(Buffers.byteArrayFor(file._3))
           image.getWidth(null) mustEqual file._1
           image.getHeight(null) mustEqual file._2 }
         }
        }
       }
} )
