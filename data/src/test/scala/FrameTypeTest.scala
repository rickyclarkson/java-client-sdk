package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

class FrameTypeTest extends JUnit4(new Specification {
 "FrameType.frameTypeFor" should {
  import FrameType._
  val types = List(0 -> JPEG, 1 -> JFIF, 2 -> MPEG4, 3 -> MPEG4, 9 -> INFO, 4 -> UNKNOWN, 100 -> UNKNOWN, -1 -> UNKNOWN)
  for ((index, frameType) <- types) { "give " + frameType + " for " + index in { frameTypeFor(index) mustEqual frameType } }
 }
})
