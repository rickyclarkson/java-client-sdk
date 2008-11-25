package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

import java.nio.ByteBuffer

class ImageDataStructTest extends JUnit4(new Specification {
 "Constructing an ImageDataStruct with a ByteBuffer containing an invalid version" should {
  "cause an IllegalArgumentException" in {
   val buffer = ByteBuffer.wrap(Array(0xFA, 0xCA, 0xDE, 0x10) map (_.toByte))
   new ImageDataStruct(buffer) must throwA(new IllegalArgumentException)
  }
 }

 "An ImageDataStruct" should {
  "have all the values supplied to it by the ByteBuffer" in {
   val b = ByteBuffer.wrap(new Array(ImageDataStruct.IMAGE_DATA_STRUCT_SIZE))
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
   val title: Array[Byte] = "0123456789012345678901234567890".getBytes("US-ASCII")
   val alarm: Array[Byte] = "9876543210987654321098765432109".getBytes("US-ASCII")
   val format_srcPixels: Array[Byte] = Array(10, 0)
   val format_srcLines: Array[Byte] = Array(20, 0)
   val format_targetPixels: Array[Byte] = Array(30, 0)
   val format_targetLines: Array[Byte] = Array(40, 0)
   val format_pixelOffset: Array[Byte] = Array(50, 0)
   val format_lineOffset: Array[Byte] = Array(60, 0)
   val locale = "012345678901234567890123456789".getBytes("US-ASCII")
   val utcOffset = 700
   val alarmBitmask = 800

   b putInt version
   b putInt mode
   b putInt camera
   b putInt videoFormat.index
   b putInt startOffset
   b putInt size
   b putInt maxSize
   b putInt targetSize
   b putInt qFactor
   b putInt alarmBitmaskHigh
   b putInt status
   b putInt sessionTime
   b putInt milliseconds
   b put res
   b put title
   b put alarm
   b put format_srcPixels
   b put format_srcLines
   b put format_targetPixels
   b put format_targetLines
   b put format_pixelOffset
   b put format_lineOffset
   b put locale
   b putInt utcOffset
   b putInt alarmBitmask
   b position 0

   val struct = new ImageDataStruct(b)
   import struct._
   getVersion mustEqual version
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
   getTitle mustEqual new String(title, "US-ASCII")
   getAlarm mustEqual new String(alarm, "US-ASCII")
   getFormat.getSrcPixels mustEqual 10
   getFormat.getSrcLines mustEqual 20
   getFormat.getTargetPixels mustEqual 30
   getFormat.getTargetLines mustEqual 40
   getFormat.getPixelOffset mustEqual 50
   getFormat.getLineOffset mustEqual 60
   getLocale mustEqual new String(locale, "US-ASCII")
   getUtcOffset mustEqual utcOffset
   getAlarmBitmask mustEqual alarmBitmask
  }
 }
})
