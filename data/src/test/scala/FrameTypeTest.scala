package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification
import java.nio.ByteBuffer
import java.io.ByteArrayInputStream

class FrameTypeTest extends JUnit4(new Specification {
 "FrameType.UNKNOWN.deliverTo" should {
  "deliver the input to the StreamHandler" in {
   var pass = false
   FrameType.UNKNOWN.deliverTo(new StreamHandler { def dataArrived(packet: Packet[ByteBuffer]) = pass = true
                                                   def info(packet: Packet[String]) = ()
                                                   def jfif(packet: Packet[ByteBuffer]) = ()
                                                   def mpeg4(packet: MPEG4Packet) = ()
                                                 },
                               new ByteArrayInputStream(Array(1, 2, 3, 4, 5)),
                               new PacketMetadata(5, 3, FrameType.UNKNOWN))

   pass mustBe true
  }
 }

 "FrameType.frameTypeFor" should {
  import FrameType._
  val known = List(0 -> JPEG, 1 -> JFIF, 2 -> MPEG4, 3 -> MPEG4, 6 -> MPEG4_MINIMAL, 9 -> INFO)
  val unknown = List(-1, 4, 5, 7, 8, 10, 11, 12) map (x => x -> UNKNOWN)
  
  for ((index, frameType) <- known ++ unknown) { "give " + frameType + " for " + index in { frameTypeFor(index) mustEqual frameType } }
 }
})
