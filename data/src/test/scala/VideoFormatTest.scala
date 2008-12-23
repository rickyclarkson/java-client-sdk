package uk.org.netvu.data

import _root_.org.{specs, scalacheck}
import specs.runner.JUnit4
import specs.Specification

class VideoFormatTest extends JUnit4(new Specification {
 "VideoFormat.valueOf" should {
  "give a VideoFormat for values from 0 to 5 inclusive" in {
   for (i <- 0 to 5)
    VideoFormat.valueOf(i) == null mustBe false
  }
  "throw an IllegalArgumentException for values outside 0 to 5" in {
   for (i <- List(-1, -100, 6, 100))
    VideoFormat.valueOf(i) must throwAn[IllegalArgumentException]
  }
 }
})
