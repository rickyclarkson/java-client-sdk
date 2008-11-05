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
  "throw an IllegalStateException when the rest of the line does not contain an int" in {
   IO.expectIntFromRestOfLine(asInputStream(List("23a", "bar"))) must throwA(new IllegalStateException)
  }
 }
})

import java.net.URL
import java.nio.ByteBuffer

class ParseBinaryStreamsTest extends JUnit4(new Specification {
 "parsing binary streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204-binary-jfif", DataType.BINARY)
 }

 "parsing mime streams containing JFIF" isSpecifiedBy {
  validlyParse("file:testdata/192-168-106-204", DataType.MIME)
 }

 def validlyParse(filename: String, dataType: DataType) = new Specification {
  "reading a " + dataType.toString.toLowerCase( java.util.Locale.ENGLISH ) + " stream containing JFIFs" should {
   "parse out at least two valid JFIF images" in {
    val url = new URL(filename)
    val connection = url.openConnection
    var numValidFrames = 0
    var numInvalidFrames = 0
    
    ParserFactory parserFor dataType parse (connection.getInputStream, new StreamHandler {
     def jfif(packet: JFIFPacket) = {
      val buffer = packet.byteBuffer
      def next = buffer.get & 0xFF
      if (next == 0xFF && next == 0xD8 && next == 0xFF)
       numValidFrames += 1
      else
       numInvalidFrames += 1
     }

     def dataArrived(byteBuffer: ByteBuffer, metadata: StreamMetadata) = ()
    })

    numValidFrames >= 2 must beTrue
    numInvalidFrames == 0 must beTrue
   }
  }
 }

 "parsing binary streams containing JPEG" should {
  "give at least two valid JPEG images" in {
   val url = new URL("file:testdata/192-168-106-204-binary-jpeg")
   val connection = url.openConnection
   var numValidFrames = 0
   var numInvalidFrames = 0

   var index = 0

   ParserFactory parserFor DataType.BINARY parse (connection.getInputStream, new StreamHandler {
    def jfif(packet: JFIFPacket) = {
     println("Does this ever happen?")
     val buffer = packet.byteBuffer
     def next = buffer.get & 0xFF
     val (a, b, c) = (next, next, next)
     println((a, b, c))
     if (a == 0xFF && b == 0xD8 && c == 0xFF)
      numValidFrames += 1
     else
      numInvalidFrames += 1
     
     buffer.position(0)
     import java.io.FileOutputStream
     val out = new FileOutputStream("/home/ricky/deleteme"+index+".jpg");
     index += 1
     out.getChannel.write(buffer);
     out.close
    }

    def dataArrived(data: ByteBuffer, metadata: StreamMetadata) = ()
   })

   numValidFrames >= 2 must beTrue
   numInvalidFrames == 0 must beTrue
  }
 }
})
