package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

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
 }

 "IO.expectIntFromRestOfLine" should {
  "return an int when the rest of the line contains an int" in {
   IO.expectIntFromRestOfLine(asInputStream(List("123", "bar"))) mustEqual 123
  }
  "cause an IllegalStateException when the rest of the line does not contain an int" in {
   IO.expectIntFromRestOfLine(asInputStream(List("23a", "bar"))) must throwA(new IllegalStateException)
  }
 }
})

import java.net.URL
import java.nio.ByteBuffer

class ParseBinaryStreamsTest extends JUnit4(new Specification {
 "reading a binary stream containing JPEGs" should {
  "parse out at least two valid JPEG images" in {
   val url = new URL("file:testdata/192-168-106-204-binary-jfif")
   val connection = url.openConnection
   var numValidFrames = 0
   var numInvalidFrames = 0

   ParserFactory parserFor DataType.BINARY parse (connection.getInputStream, new StreamHandler {
    def jfif(packet: JPEGPacket) = {
     println("Receiving a JPEG")
     val buffer = packet.byteBuffer
     if (buffer.position != 0) throw null
     val (a, b, c) = (buffer.get & 0xFF, buffer.get & 0xFF, buffer.get & 0xFF)
     println(a + " should be " + 0xFF + ", " + b + " should be " + 0xD8 + ", " + c + " should be " + 0xFF)
     if (a == 0xFF && b == 0xd8 && c == 0xFF)
      numValidFrames += 1
     else
      numInvalidFrames += 1
    }

    def unknown(data: ByteBuffer, metadata: StreamMetadata) = ()
   })

   println(numValidFrames+", "+numInvalidFrames)
   numValidFrames >= 2 must beTrue
   numInvalidFrames == 0 must beTrue
  }
 }

 "reading a mime stream containing JPEGs" should {
  "parse out at least two valid JPEG images" in {
   val url = new URL("file:testdata/192-168-106-204")
   val connection = url.openConnection
   var numValidFrames = 0
   var numInvalidFrames = 0
   
   ParserFactory parserFor DataType.MIME parse (connection.getInputStream, new StreamHandler {
    def jfif(packet: JPEGPacket) = {
     val buffer = packet.byteBuffer
     if ((buffer.get & 0xFF) == 0xFF && (buffer.get & 0xFF) == 0xD8 && (buffer.get & 0xFF) == 0xFF)
      numValidFrames += 1
     else
      numInvalidFrames += 1
    }

    def unknown(data: ByteBuffer, metadata: StreamMetadata) = ()
   })

   numValidFrames >= 2 must beTrue
   numInvalidFrames == 0 must beTrue
  }
 }
})
