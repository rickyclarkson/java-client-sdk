package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification
import specs.util.DataTables

import java.nio.ByteBuffer

class ImageDataStructTest extends JUnit4(new Specification with DataTables {
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

 "Constructing an ImageDataStruct with a set image and comment size" should {
  "succeed" in {
   ImageDataStruct construct (10, 10)
   true mustBe true
  }
 }

 def data[T](getter: ImageDataStruct => T, expected: T) = new Specification {
  getter(ImageDataStruct.construct(10, 10)) mustEqual expected
 }

 "getAlarmBitmask" isSpecifiedBy data(_ getAlarmBitmask, 0)
 "getAlarmBitmaskHigh" isSpecifiedBy data(_ getAlarmBitmaskHigh, 0)
 "getLineOffset" isSpecifiedBy data(_ getLineOffset, 0)
 "getMaxSize" isSpecifiedBy data(_ getMaxSize, 0)
 "getMode" isSpecifiedBy data(_ getMode, 0)
 "getPixelOffset" isSpecifiedBy data(_ getPixelOffset, 0)
 "getRes" isSpecifiedBy data(_ getRes, "")
 "getSize" isSpecifiedBy data(_ getSize, 0)
 "getSrcLines" isSpecifiedBy data(_ getSrcLines, 0)
 "getSrcPixels" isSpecifiedBy data(_ getSrcPixels, 0)
 "getStatus" isSpecifiedBy data(_ getStatus, 0)
 "getTargetSize" isSpecifiedBy data(_ getTargetSize, 0)

 val ids = ImageDataStruct.construct(10, 10)
 ids setRes "blob"
 ids.getRes mustEqual "blob"                                          
})
