package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification
import java.io.{FileInputStream, ByteArrayInputStream, EOFException}
import java.nio.BufferUnderflowException

object Support {
 def asInputStream(strings: List[String]) = {
  val results = new Array[Byte](strings.map(_.length).reduceLeft(_+_) + strings.size - 1)
  var r = -1

  for ((s, i) <- strings.zipWithIndex) {
   for (c <- s) {
    r += 1
    if (c > 127) throw null
    results(r) = c.toByte
   }
   r += 1
   if (i < strings.size - 1) results(r) = '\n'
  }

  new java.io.ByteArrayInputStream(results)
 } 
}

import Support.asInputStream

class IOTest extends JUnit4(new Specification {
 "IO.expectLine" should {
  "complete normally when the expected string is found" in {
   IO.expectLine(asInputStream(List("foo", "bar")), "foo")
   true must beTrue
  }
  "throw an IllegalStateException when the expected string is not found" in {
   IO.expectLine(asInputStream(List("foo", "bar")), "fop") must throwA[IllegalStateException]
  }
  "throw an EOFException when there is no end of line marker" in {
   IO.expectLine(new ByteArrayInputStream("foo".getBytes("US-ASCII")), "foo") must throwA[EOFException]
  }
 }

 "IO.expectLineMatching" should {
  "complete normally when the expected regex is found" in {
   IO.expectLineMatching(asInputStream(List("foo", "bar")), "f.o")
   true must beTrue
  }
  "throw an IllegalStateException when the regex is not matched" in {
   IO.expectLineMatching(asInputStream(List("foo", "bar")), "g.o") must throwA[IllegalStateException]
  }
 }

 "IO.expectString" should {
  "complete normally when the expected String is found" in {
   IO.expectString(asInputStream(List("foo", "bar")), "fo")
   true must beTrue
  }
  "throw an IllegalStateException when the expected String is not found" in {
   IO.expectString(asInputStream(List("foo", "bar")), "oo") must throwA[IllegalStateException]
  }
  "complete normally on an empty String" in {
   IO.expectString(asInputStream(List("foo", "bar")), "")
   true must beTrue
  }
 }

 "IO.readIntFromRestOfLine" should {
  "return an int when the rest of the line contains an int" in {
   IO.readIntFromRestOfLine(asInputStream(List("123", "bar"))) mustEqual 123
  }
  "throw an IllegalStateException when the rest of the line does not contain an int" in {
   IO.readIntFromRestOfLine(asInputStream(List("23a", "bar"))) must throwA[IllegalStateException]
  }
 }
 
 import java.nio.ByteBuffer

 "IO.from" should {
  "give a ByteBuffer with position 0 and starting at the specified index in the original." in {
   val bb = IO.from(ByteBuffer.wrap(Array(1,2,3)), 1)
   bb.position mustEqual 0
   bb.get mustEqual 2
   bb.limit mustEqual 2
  }
 }

 "IO.slice" should {
  "give a ByteBuffer with position 0 and containing the specified range of bytes." in {
   val bb = IO.slice(ByteBuffer.wrap(Array(1,2,3,4)), 1, 2)
   bb.limit mustEqual 2
   bb.position mustEqual 0
   bb.get mustEqual 2
   bb.get mustEqual 3
  }
 }
})

import java.net.URL
import java.nio.ByteBuffer

class ParseBinaryStreamsTest extends JUnit4(new Specification {
 "parsing binary streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-binary-jfif", StreamType.BINARY)
 }

/* "parsing binary streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/remguard_mews-with-alarm-text", StreamType.BINARY)
 }*/

 "parsing mime streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-mime-jfif", StreamType.MIME)
 }

 "parsing a mime stream containing engineered 4:2:2 colourspace JFIFs" isSpecifiedBy {
  validlyParse("file:testdata/engineered-mime-jfif-422", StreamType.MIME)
 }

 "parsing a binary stream containing engineered 4:2:2 colourspace truncated JPEGs" isSpecifiedBy {
  validlyParse("file:testdata/engineered-binary-jpeg-422-colorspace", StreamType.BINARY)
 }

 def validlyParse(filename: String, streamType: StreamType) = new Specification {
  "reading "+filename+", a " + streamType.toString.toLowerCase( java.util.Locale.ENGLISH ) + " stream containing JFIFs" should {
   "parse out at least two valid JFIF images" in {
    val url = new URL(filename)
    val connection = url.openConnection
    var numValidFrames = 0
    var numInvalidFrames = 0
    
    ParserFactory parserFor streamType parse (connection.getInputStream, null, new StreamHandler {
     def jpegFrameArrived(packet: Packet) = {
      val rawData = packet.getOnDiskFormat
      val buffer = packet.getData
      val channel = packet.getChannel
      
      def next = buffer.get & 0xFF
      val first = (next, next)

      buffer.position(buffer.limit - 2)
      val last = (next, next)

      val version = packet.getOnDiskFormat.getInt
      if (version != 0xDECADE10 && version != 0xDECADE11 )
       throw new RuntimeException(String.valueOf(version))

      (first, last) match {
       case ((0xFF, 0xD8), (0xFF, 0xD9)) => numValidFrames += 1
       case _ => numInvalidFrames += 1
      }
     }

     def unknownDataArrived(packet: Packet) = ()
     def mpeg4FrameArrived(packet: Packet) = ()
     def infoArrived(packet: Packet) = ()
     def audioDataArrived(packet: Packet) = ()
    })

    numInvalidFrames == 0 must beTrue
    numValidFrames >= 2 must beTrue
   }
  }
 }

 "parsing binary streams containing JPEG" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-binary-jpeg", StreamType.BINARY)
 }

 "parsing minimal streams containing JPEG" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-minimal-jpeg", StreamType.BINARY)
 }

 "parsing minimal streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-minimal-jfif", StreamType.BINARY)
 }

 "parsing binary streams containing MPEG4" isSpecifiedBy {
  validlyParseMPEG4("file:testdata/192-168-106-206-binary-mp4", StreamType.BINARY)
 }

 "parsing 207's binary/mp4" isSpecifiedBy {
  validlyParseMPEG4("file:testdata/192-168-106-207-binary-mp4", StreamType.BINARY)
 }

 "parsing 207's minimal/mp4" isSpecifiedBy {
  validlyParseMPEG4("file:testdata/192-168-106-207-minimal-mp4", StreamType.BINARY)
 }

 "parsing 206's mime/mp4" isSpecifiedBy {
  validlyParseMPEG4("file:testdata/192-168-106-206-mime-mp4", StreamType.MIME)
 }

 def validlyParseMPEG4(filename: String, streamType: StreamType) = new Specification {
  "parsing an MPEG4 stream ( "+filename+")" should {
   "produce at least two valid MPEG frames" in {
    val url = new URL(filename)
    val connection = url.openConnection
    var numValidFrames = 0
    var numInvalidFrames = 0
    var index = 0
    
    ParserFactory parserFor streamType parse (connection.getInputStream, null, new StreamHandler {
     def audioDataArrived(packet: Packet) = ()
     def jpegFrameArrived(packet: Packet) = ()
     def unknownDataArrived(packet: Packet) = ()
     def mpeg4FrameArrived(packet: Packet) = {

      val version = packet.getOnDiskFormat.getInt
      if (version != 0xDECADE10 && version != 0xDECADE11 )
       throw new RuntimeException(Integer.toHexString(version))

      val isIFrame: Boolean = {
       var foundVOP = false
       var last = 0xFFFF
       val data = packet.getData
       try {
        while (!foundVOP) {
         val current = data.getShort
         val joined = ((last & 0xFFFF) << 16) | current
         if (joined == 0x000001B6) { //vop start
          foundVOP = true
         }
         last=current
        }
        foundVOP
       } catch { case e: BufferUnderflowException => false }
      }
      
      if (isIFrame)
       numValidFrames += 1
      
      index += 1
     }
     def infoArrived(packet: Packet) = ()
    })
    
    numValidFrames >= 2 must beTrue
   }
  }
 }

 "parsing a binary stream containing unknown data" should {
  "yield at least one unknown data packet to the StreamHandler" in {
   val url = new URL("file:testdata/engineered-192-168-106-207-binary-unknown")
   val connection = url.openConnection
   var success = false
   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = ()
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
    def unknownDataArrived(packet: Packet) = success = true
   })

   success mustBe true
  }
 }

 "parsing a minimal stream containing MPEG-4 data with no IMAGESIZE comment" should {
  "cause an IllegalStateException" in {
   val url = new URL("file:testdata/engineered-minimal-mp4-no-imagesize")
   val connection = url.openConnection
   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = ()
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
    def unknownDataArrived(packet: Packet) = ()
   }) must throwA[IllegalStateException]
  }
 }

 "parsing a mime stream containing MPEG-4 data with no VOP header" should {
  "cause an IllegalStateException" in {
   val url = new URL("file:testdata/engineered-mime-mp4-no-vop")
   val connection = url.openConnection
   ParserFactory parserFor StreamType.MIME parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = ()
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = {
     packet.getData
     packet.getOnDiskFormat
    }
    def unknownDataArrived(packet: Packet) = ()
   }) must throwA[IllegalStateException]  
  }
 }

 "parsing a mime stream containing JFIFs with an invalid Time or Date field, and requesting the on-wire format" should {
  "cause an IllegalStateException" in {
   for (url <- List(new URL("file:testdata/engineered-mime-jfif-invalid-time"), new URL("file:testdata/engineered-mime-jfif-invalid-date"))) {
    val connection = url.openConnection
    ParserFactory parserFor StreamType.MIME parse (connection.getInputStream, null, new StreamHandler {
     def audioDataArrived(packet: Packet) = ()
     def jpegFrameArrived(packet: Packet) = packet.getOnDiskFormat
     def infoArrived(packet: Packet) = ()
     def mpeg4FrameArrived(packet: Packet) = ()
     def unknownDataArrived(packet: Packet) = ()
    }) must throwA[IllegalStateException]
   }
  }
 }

 "parsing a mime stream containing MPEG-4s with text/plian instead of text/plain" should {
  "cause an IllegalStateException" in {
   val connection = new URL("file:testdata/engineered-mime-mp4-plian-text").openConnection
   ParserFactory parserFor StreamType.MIME parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = ()
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
    def unknownDataArrived(packet: Packet) = ()
   }) must throwA[IllegalStateException]
  }
 }

 "parsing a binary stream containing JPEGs with the wrong image_data magic number" should {
  "cause an IllegalStateException" in {
   val url = new URL("file:testdata/engineered-binary-jpeg-wrong-magic-number")
   val connection = url.openConnection
   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = packet.getData
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
    def unknownDataArrived(packet: Packet) = ()
   }) must throwA[IllegalStateException]
  }
 }  
    
 "parsing a binary stream containing JPEGs" should {
  "produce identical JFIFs to GenericVideoHeader" in {
   val url = new URL("file:testdata/192-168-106-204-binary-jpeg")
   val connection = url.openConnection
   var index = 0
   var success = 0
   var fail = 0  

   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, null, new StreamHandler {
    def audioDataArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = {
     val buffer = packet.getData
     if (diff(buffer, mapFile("testdata/expected-192-168-106-204-binary-jpeg/" + index + ".jpg")))
      fail += 1
     else
      success += 1
     index += 1
    }

    def unknownDataArrived(packet: Packet) = ()
    def infoArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
   })

   fail mustEqual 0
   success mustEqual 30
  }
 }

 def diff(one: ByteBuffer, two: ByteBuffer): Boolean =
  if (one.position<one.limit) one.get == two.get && diff(one, two) else two.position == two.limit

 import java.nio.channels.FileChannel.MapMode
 import java.io.File
 def mapFile(name: String) = new FileInputStream(name).getChannel.map(MapMode.READ_ONLY, 0, new File(name).length())
})
