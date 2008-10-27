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
})

class ConnectionTest extends JUnit4(new Specification {
 import DecoderCGI.Connection
 val camSetter: Connection => Connection = _ cam 2
 val seqSetter: Connection => Connection = _ seq 4
 val dwellSetter:Connection => Connection = _ dwell 10

 "cam and seq" isSpecifiedBy mutuallyExclusive(new Connection, List(camSetter, seqSetter))
 "cam and dwell" isSpecifiedBy mutuallyExclusive(new Connection, List(camSetter, dwellSetter))
 "parsing a URL into a Connection" should {
  "give the correct slave IP address" in {
   val url = "slaveip=192.168.1.10"
   new DecoderCGI.Connection.FromURLToConnection()(url).getSlaveIP mustEqual "192.168.1.10"
  }
 }

 "A Connection constructed with a particular slaveIP" should {
  "contain that slaveIP" in {
   new DecoderCGI.Connection().slaveIP("foo").getSlaveIP mustEqual "foo"
  }
 }

 "URL-encoding a Connection with cam=2 and slaveIP='foo'" should {
  val result = "\"slaveip=foo,cam=2\""
  "give "+result in {
   import java.net.URLDecoder
   URLDecoder.decode(Connection.urlEncode(new Connection cam 2 slaveIP "foo"), "UTF-8") mustEqual result
  }
 }

 def mutuallyExclusive[T](t: => T, setters: List[T => T]) = new Specification {
  "the parameters" should {
   "be mutually exclusive" in {
    for { one <- setters
          two <- setters
          if one != two } { val part1 = one(t)
                            two(t) must throwA(new IllegalStateException)
                          }
   }
  }
 }
})
