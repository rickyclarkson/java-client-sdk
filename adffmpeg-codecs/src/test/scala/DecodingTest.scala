package uk.org.netvu.adffmpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import uk.org.netvu.util.Buffers
import uk.org.netvu.jpeg.JPEGDecoder
import uk.org.netvu.jpeg.DecodingTests

class DecodingTest extends JUnit4(new Specification {
 for (decoder <- List[JPEGDecoder](ADFFMPEGDecoder.getInstance()))
  decoder.toString + isSpecifiedBy(DecodingTests.validDecoder(decoder))
})
