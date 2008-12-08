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
   val res = "foo"
   val title = "0123456789012345678901234567890"
   val alarm = "9876543210987654321098765432109"
   val locale = "012345678901234567890123456789"
   val utcOffset = 700
   val alarmBitmask = 800

   val bb = ByteBuffer.allocate(ImageDataStruct.IMAGE_DATA_STRUCT_SIZE)
   bb.putInt(0xDECADE11)
   bb.position(0)
   val struct = new ImageDataStruct(bb)
   struct setVersion version
   struct setMode mode
   struct setCamera camera
   struct setVideoFormat videoFormat
   struct setStartOffset startOffset
   struct setSize size
   struct setMaxSize maxSize
   struct setTargetSize targetSize
   struct setQFactor qFactor
   struct setAlarmBitmaskHigh alarmBitmaskHigh
   struct setStatus status
   struct setSessionTime sessionTime
   struct setMilliseconds milliseconds
   struct setRes res
   struct setTitle title
   struct setAlarm alarm
   struct setSrcPixels 10
   struct setSrcLines 20
   struct setTargetPixels 30
   struct setTargetLines 40
   struct setPixelOffset 50
   struct setLineOffset 60

   struct setLocale locale
   struct setUtcOffset utcOffset
   struct setAlarmBitmask alarmBitmask
   import struct._
   getVersion.toHexString mustEqual version.toHexString
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
   getRes mustEqual res
   getTitle mustEqual title
   getAlarm mustEqual alarm
   getSrcPixels mustEqual 10
   getSrcLines mustEqual 20
   getTargetPixels mustEqual 30
   getTargetLines mustEqual 40
   getPixelOffset mustEqual 50
   getLineOffset mustEqual 60
   getLocale mustEqual locale
   getUtcOffset mustEqual utcOffset
   getAlarmBitmask mustEqual alarmBitmask
  }
 }

 
})
