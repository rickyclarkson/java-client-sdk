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
   IO.expectLine(asInputStream(List("foo", "bar")), "fop") must throwA(new IllegalStateException)                       
  }
  "throw an EOFException when there is no end of line marker" in {
   IO.expectLine(new ByteArrayInputStream("foo".getBytes("US-ASCII")), "foo") must throwA(new EOFException)
  }
 }

 "IO.expectLineMatching" should {
  "complete normally when the expected regex is found" in {
   IO.expectLineMatching(asInputStream(List("foo", "bar")), "f.o")
   true must beTrue
  }
  "throw an IllegalStateException when the regex is not matched" in {
   IO.expectLineMatching(asInputStream(List("foo", "bar")), "g.o") must throwA(new IllegalStateException)
  }
 }

 "IO.expectString" should {
  "complete normally when the expected String is found" in {
   IO.expectString(asInputStream(List("foo", "bar")), "fo")
   true must beTrue
  }
  "throw an IllegalStateException when the expected String is not found" in {
   IO.expectString(asInputStream(List("foo", "bar")), "oo") must throwA(new IllegalStateException)
  }
  "complete normally on an empty String" in {
   IO.expectString(asInputStream(List("foo", "bar")), "")
   true must beTrue
  }
 }

 "IO.expectIntFromRestOfLine" should {
  "return an int when the rest of the line contains an int" in {
   IO.expectIntFromRestOfLine(asInputStream(List("123", "bar"))) mustEqual 123
  }
  "throw an IllegalStateException when the rest of the line does not contain an int" in {
   IO.expectIntFromRestOfLine(asInputStream(List("23a", "bar"))) must throwA(new IllegalStateException)
  }
 }
})

import java.net.URL
import java.nio.ByteBuffer

class ParseBinaryStreamsTest extends JUnit4(new Specification {
 "parsing binary streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-binary-jfif", StreamType.BINARY)
 }

 "parsing mime streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-mime-jfif", StreamType.MIME)
 }

 def validlyParse(filename: String, streamType: StreamType) = new Specification {
  "reading a " + streamType.toString.toLowerCase( java.util.Locale.ENGLISH ) + " stream containing JFIFs" should {
   "parse out at least two valid JFIF images" in {
    val url = new URL(filename)
    val connection = url.openConnection
    var numValidFrames = 0
    var numInvalidFrames = 0
    
    ParserFactory parserFor streamType parse (connection.getInputStream, new StreamHandler {
     def jfif(buffer: ByteBuffer) = {
      def next = buffer.get & 0xFF
      val first = (next, next)
      buffer.position(buffer.limit - 2)
      val last = (next, next)
   
      (first, last) match {
       case ((0xFF, 0xD8), (0xFF, 0xD9)) => numValidFrames += 1
       case _ => numInvalidFrames += 1
      }
     }

     def dataArrived(byteBuffer: ByteBuffer, metadata: StreamMetadata) = ()
     def mpeg4(packet: MPEG4Packet) = ()
     def info(byteBuffer: ByteBuffer) = ()
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
  validlyParse("file:testdata/192-168-106-204-minimal-jpeg", StreamType.MINIMAL)
 }

 "parsing minimal streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-minimal-jfif", StreamType.MINIMAL)
 }

 "parsing an MPEG4 stream" should {
  "produce at least two valid MPEG frames" in {
   val url = new URL("file:testdata/192-168-106-206-binary-mp4")
   val connection = url.openConnection
   var numValidFrames = 0
   var numInvalidFrames = 0
   var index = 0

   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, new StreamHandler {
    def jfif(buffer: ByteBuffer) = ()
    def dataArrived(buffer: ByteBuffer, metadata: StreamMetadata) = ()
    def mpeg4(packet: MPEG4Packet) = {
     val isIFrame: Boolean = {
      var foundVOP = false
      try {
       while (!foundVOP) {
        val anInt = packet.getData.getInt
        if (anInt == 0x000001B6) {
         foundVOP = true
        }
       }
       packet.getData.get
       packet.getData.getShort
       ((packet.getData.get >> 6) & 0xFF) == 0
      } catch { case e: BufferUnderflowException => false }
     }

     if (isIFrame)
      numValidFrames += 1

     index += 1
    }
    def info(buffer: ByteBuffer) = buffer.position(buffer.limit() - 1)
   })
   
   numValidFrames >= 2 must beTrue                                                       
  }
 }

 "parsing a binary stream containing JPEGs" should {
  "produce identical JFIFs to GenericVideoHeader" in {
   val url = new URL("file:testdata/192-168-106-204-binary-jpeg")
   val connection = url.openConnection
   var index = 0
   var success = 0
   var fail = 0  

   ParserFactory parserFor StreamType.BINARY parse (connection.getInputStream, new StreamHandler {
    def jfif(buffer: ByteBuffer) = {
     if (diff(buffer, mapFile("testdata/expected-192-168-106-204-binary-jpeg/" + index + ".jpg")))
      fail += 1
     else
      success += 1
     index += 1
    }

    def dataArrived(buffer: ByteBuffer, metadata: StreamMetadata) = ()
    def info(buffer: ByteBuffer) = ()
    def mpeg4(packet: MPEG4Packet) = ()
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

