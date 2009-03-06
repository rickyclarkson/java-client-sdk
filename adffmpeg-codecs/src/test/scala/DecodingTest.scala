package uk.org.netvu.adffmpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import uk.org.netvu.util.Buffers
import uk.org.netvu.codecs.VideoDecoder
import uk.org.netvu.codecs.VideoCodec
import uk.org.netvu.codecs.DecodingTests
import java.io.{File, FileInputStream, ByteArrayOutputStream}
import uk.org.netvu.util.Images
import javax.imageio.ImageIO

class JPEGDecodingTest extends JUnit4(new Specification {
 val decoder = ADFFMPEGDecoders.getJPEGDecoder()
 decoder.toString + isSpecifiedBy(DecodingTests.validDecoder(decoder))
 decoder.dispose
})

class MPEG4DecodingTest extends JUnit4(new Specification {
 val decoder = ADFFMPEGDecoders.getMPEG4Decoder

 for (a <- 0 to 4) {
  val file = new File("testdata/mpeg4frames/" + a)
  val in = new FileInputStream(file)
  val out = new ByteArrayOutputStream
  var b = in.read
  while (b != -1) {
   out write b
   b = in.read
  }
  val bytes = out.toByteArray
  val buffer = ByteBuffer allocateDirect bytes.length
  buffer put bytes
  buffer position 0
  val image = decoder.decode(buffer)
  Images.equal(image, ImageIO.read(new File("testdata/png/" + a + ".png")), 20) mustBe true
  image.getWidth(null) > 10 mustBe true
  image.getHeight(null) > 10 mustBe true
 }

 decoder.dispose
})
