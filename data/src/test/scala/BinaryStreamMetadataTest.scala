package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification
import java.nio.ByteBuffer
import java.io.ByteArrayInputStream

class BinaryStreamMetadataTest extends JUnit4(new Specification {
 "BinaryStreamMetadata" should {
  val metadata = new BinaryStreamMetadata(new ByteArrayInputStream(Array(1,2,0,0,0,3): Array[Byte]))
  "have the correct frame type" in { metadata.getFrameType mustEqual FrameType.frameTypeFor(1) }
  "have the correct channel" in { metadata.getChannel mustEqual 3 }
  "have the correct length" in { metadata.getLength mustEqual 3 }
 }
})
