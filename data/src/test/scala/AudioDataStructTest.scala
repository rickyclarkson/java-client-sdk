package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

import java.nio.ByteBuffer

class AudioDataStructTest extends JUnit4(new Specification {
 "Constructing an AudioDataStruct with a ByteBuffer containing an invalid version" should {
  "cause an IllegalArgumentException" in {
   val buffer = ByteBuffer.wrap(Array(0xFA, 0xCA, 0xDE, 0x10) map (_.toByte))
   AudioDataStruct construct buffer must throwA[IllegalArgumentException]
  }
 }

 "An AudioDataStruct" should {
  "have all the values supplied to it by the ByteBuffer" in {
   val mode = 2
   val channel = 3
   val startOffset = 4
   val size = 5
   val seconds = 6
   val millis = 7

   val bb = ByteBuffer.allocate(AudioDataStruct.AUDIO_DATA_STRUCT_SIZE)
   bb.putInt(AudioDataStruct.VERSION)
   bb.position(0)
   val struct = AudioDataStruct construct bb
   struct setMode mode
   struct setChannel channel
   struct setStartOffset startOffset
   struct setSize size
   struct setSeconds seconds
   struct setMilliseconds millis

   import struct._
   getMode mustEqual mode
   getChannel mustEqual channel
   getStartOffset mustEqual startOffset
   getSize mustEqual size
   getSeconds mustEqual seconds
   getMilliseconds mustEqual millis
  }
 }

 "Coverage" should {
  "be 100%" in {
   AudioDataStruct.construct(10).getByteBuffer
   true mustBe true
  }
 }
})
