package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

import java.nio.ByteBuffer

class ImageDataStructTest extends JUnit4(new Specification {
 "Constructing an ImageDataStruct with a ByteBuffer containing an invalid version" should {
  "cause an IllegalArgumentException" in {
   val buffer = ByteBuffer.wrap(Array(0xFA, 0xCA, 0xDE, 0x10) map (_.toByte))
   ImageDataStruct construct buffer must throwA[IllegalStateException]
  }
 }

 "Constructing an ImageDataStruct with a ByteBuffer containing a valid version" should {
  "succeed" in {
   val buffer = ByteBuffer.wrap(Array(0xDE, 0xCA, 0xDE, 0x10) map (_.toByte))
   ImageDataStruct construct buffer
   true mustBe true
  }
 }
})
