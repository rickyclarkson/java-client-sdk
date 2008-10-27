package uk.org.netvu.protocol

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4

class DecoderCGITest extends JUnit4(new Specification {
 "A DecoderCGI with output titles" should {
  "be properly converted to URL parameters" in {
   var cgi = new DecoderCGI() outputTitles(Array("foo", "bar", "baz")) command(2, "blah")
   val url = "decoder.var?layouts[1]=1&output_titles=\"foo\",\"bar\",\"baz\"&commands[2]=%22blah%22"
   cgi.layout(1, DecoderCGI.Layout.FOUR_WAY).toURLParameters mustEqual url
  }
 }

 "Parsing a Decoder request" should {
  "give a DecoderCGI containing the correct values" in {
   import DecoderCGI.{fromURL, Layout}
   val cgi = fromURL("decoder.var?layouts[2]=1&output_titles=\"one\",\"two\",\"three\"&commands[3]=%22blah blah%22&connections[4]=slaveip=foo,cam=2")
   val layouts = cgi.getLayouts
   layouts.size mustEqual 1
   layouts.get(2) mustEqual Layout.FOUR_WAY
   val outputTitles = cgi.getOutputTitles
   outputTitles.length mustEqual 3

   import Implicits._

   for ((item, index) <- List("one", "two", "three") zipWithIndex) {
    outputTitles(index) mustEqual item
   }
  }
 }

 "Parsing a Decoder request whose output titles are not quoted" should {
  "cause an IllegalArgumentException" in {
   for {
    varOrFrm <- List("var","frm")
    item <- List("foo", "\"foo", "bar\"","\"\"")
   } {
    DecoderCGI.fromURL("decoder." + varOrFrm + "?output_titles=" + item) must throwA(new IllegalArgumentException)
   }
  }
 }

 "A built DecoderCGI" should {
  "have the values given to it" in {
   import DecoderCGI.Connection
   new DecoderCGI().connection(1, new Connection().cam(2)).getConnections.get(1).getCam mustEqual 2
   new DecoderCGI().command(1, "foo").getCommands.get(1) mustEqual "foo"
  }
 }
})

class ConnectionTest extends JUnit4(new Specification {
 import DecoderCGI.Connection
 val camSetter: Connection => Connection = _ cam 2
 val seqSetter: Connection => Connection = _ seq 4
 val dwellSetter: Connection => Connection = _ dwell 10

 "cam and seq" isSpecifiedBy mutuallyExclusive(new Connection, List(camSetter, seqSetter))
 "cam and dwell" isSpecifiedBy mutuallyExclusive(new Connection, List(camSetter, dwellSetter))

 "parsing a URL into a Connection" should {
  "give the values from the URL" in {
   val url = "slaveip=192.168.1.10&cam=2"
   val connection = new DecoderCGI.Connection.FromURLToConnection()(url)
   connection.getSlaveIP mustEqual "192.168.1.10"
   connection.getCam mustEqual 2
  }

  "give the values from the URL" in {
   val url = "slaveip=192.168.1.10&dwell=4&seq=3"
   val connection = new DecoderCGI.Connection.FromURLToConnection()(url)
   connection.getDwell mustEqual 4
   connection.getSeq mustEqual 3
  }
 }

 "A Connection constructed with a particular slaveIP" should {
  "contain that slaveIP" in {
   new DecoderCGI.Connection().slaveIP("foo").getSlaveIP mustEqual "foo"
  }
 }

 "A Connection constructed with a particular audio channel" should {
  "contain that audio channel" in {
   new Connection().audioChannel(4).getAudioChannel mustEqual 4
  }
 }

 "URL-encoding a Connection with cam=2 and slaveIP='foo'" should {
  val result = "\"slaveip=foo,cam=2\""
  "give "+result in {
   import java.net.URLDecoder
   URLDecoder decode (Connection urlEncode (new Connection cam 2 slaveIP "foo"), "UTF-8") mustEqual result
  }
 }

 "Parsing an invalid URL" should {
  "cause an IllegalArgumentException" in {
   new DecoderCGI.Connection.FromURLToConnection()("dwell=foo") must throwA(new IllegalArgumentException)
  }
 }

 def mutuallyExclusive[T](t: => T, setters: List[T => T]) = new Specification {
  "the parameters" should {
   "be mutually exclusive" in {
    for { one <- setters
          two <- setters
          if one != two } {
           val connection = t
           val part1 = one(connection)
           two(part1) must throwA(new IllegalStateException)
          }
   }
  }
 }
})

class LayoutTest extends JUnit4(new Specification {
 import DecoderCGI.Layout
 import scala.collection.jcl.Conversions.convertSet
 "Converting a Layout to a String and back" should {
  "give the original Layout" in {
   val fromURL = Layout.fromURLFunction
   import java.util.EnumSet
   EnumSet.allOf(classOf[Layout]) forall { layout => fromURL(String.valueOf(layout.value)) mustEqual layout }
  }
 }

 "Invalid values in Layout.fromURLFunction" should {
  "cause an IllegalArgumentException" in {
   Layout.fromURLFunction()("10") must throwA(new IllegalArgumentException)
  }
 }
})
