package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

import java.nio.ByteBuffer

class ImageDataStructTest extends JUnit4(new Specification {
 "Constructing an ImageDataStruct with a ByteBuffer containing an invalid version" should {
  "cause an IllegalArgumentException" in {
   val buffer = ByteBuffer.wrap(Array(0xFA, 0xCA, 0xDE, 0x10) map (_.toByte))
   new ImageDataStruct(buffer) must throwA[IllegalArgumentException]
  }
 }

 "An ImageDataStruct" should {
  "have all the values supplied to it by the ByteBuffer" in {

   val version = 0xDECADE10
   val mode = 2
   val camera = 3
   val videoFormat = VideoFormat.JPEG_422
   val startOffset = 10
   val size = 20
   val maxSize = 40
   val targetSize = 60
   val qFactor = 100
   val alarmBitmaskHigh = 140
   val status = 200
   val sessionTime = 240
   val milliseconds = 460
   val res: Array[Byte] = Array(1,3,2,6)
   val title = "0123456789012345678901234567890"
   val alarm = "9876543210987654321098765432109"
   val format = new PictureStructBuilder(java.nio.ByteOrder.BIG_ENDIAN) srcPixels 10 srcLines 20 targetPixels 30 targetLines 40 pixelOffset 50 lineOffset 60 build
   val locale = "012345678901234567890123456789"
   val utcOffset = 700
   val alarmBitmask = 800

   val struct = (new ImageDataStructBuilder() version version mode mode camera camera
                 videoFormat videoFormat startOffset startOffset size size maxSize maxSize
                 targetSize targetSize qFactor qFactor alarmBitmaskHigh alarmBitmaskHigh
                 status status sessionTime sessionTime milliseconds milliseconds res res
                 title title alarm alarm format format locale locale utcOffset utcOffset
                 alarmBitmask alarmBitmask build)
   import struct._
   getVersion mustEqual version
   getMode mustEqual mode
   getCamera mustEqual camera
   getVideoFormat mustEqual videoFormat
   getStartOffset mustEqual startOffset
   getSize mustEqual size
   getMaxSize mustEqual maxSize
   getTargetSize mustEqual targetSize
   getQFactor mustEqual qFactor
   getAlarmBitmaskHigh mustEqual alarmBitmaskHigh
   getStatus mustEqual status
   getSessionTime mustEqual sessionTime
   getMilliseconds mustEqual milliseconds
   getRes.sameElements(res) mustEqual true
   getTitle mustEqual title
   getAlarm mustEqual alarm
   getFormat.getSrcPixels mustEqual 10
   getFormat.getSrcLines mustEqual 20
   getFormat.getTargetPixels mustEqual 30
   getFormat.getTargetLines mustEqual 40
   getFormat.getPixelOffset mustEqual 50
   getFormat.getLineOffset mustEqual 60
   getLocale mustEqual locale
   getUtcOffset mustEqual utcOffset
   getAlarmBitmask mustEqual alarmBitmask
  }
 }

 
})
