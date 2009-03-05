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

class JPEGDecodingTest extends JUnit4(new Specification {
 for (decoder <- List[VideoDecoder[VideoCodec.JPEG]](ADFFMPEGDecoders.getJPEGDecoder())) {
  decoder.toString + isSpecifiedBy(DecodingTests.validDecoder(decoder))
  decoder.dispose
 }
})

class MPEG4DecodingTest extends JUnit4(new Specification {
 val decoder = ADFFMPEGDecoders.getMPEG4Decoder

 var a = 0
 while (a < 5) {
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
  image.getWidth(null) > 10 mustBe true
  image.getHeight(null) > 10 mustBe true
  a += 1
 }

 decoder.dispose
})
