package uk.org.netvu.protocol

import _root_.org.{specs, scalacheck}
import specs.{Specification, ScalaCheck}
import specs.runner.JUnit4

class DisplayPicCGITest extends JUnit4(new Specification {
 import DisplayPicCGI._
 "Building a DisplayPicCGI" should {
  "give a DisplayPicCGI containing the correct values" in {
   val builder = new Builder cam 4 fields 5
   val url = "/display_pic.cgi?cam=4&fields=5"
   builder.build.toString mustEqual url
  }
 }

 "DEBUG: setting res twice" should { "fail" in { new Builder res "lo" res "lo" must throwA[IllegalStateException] } }
 "DEBUG: setting pktSize twice" should { "fail" in { new Builder pktSize 17 pktSize 17 must throwA[IllegalStateException] } }
 "DEBUG: setting audio twice" should { "fail" in { new Builder audio "none" audio "none" must throwA[IllegalStateException] } }

 "Parsing a display_pic.cgi request" should {
  "give a DisplayPicCGI containing the correct values" in {
   val cgi = fromString("/display_pic.cgi?cam=4&fields=5")
   cgi.getCam mustEqual 4
   cgi.getFields mustEqual 5
  }
 }
   
 val setters = List[Builder => Builder](_ cam 4, _ fields 5, _ res "hi", _ seq 6, _ dwell 7, _ id 8, _ dIndex 9, _ presel 3, _ channel 1, _ rate 12, _ forcedQ 13, _ duration 14, _ nBuffers 15, _ telemQ 16, _ pktSize 17, _ udpPort 18, _ audio "none", _ format Format.MP4, _ audioMode AudioMode.INLINE, _ txMode TransmissionMode.BINARY, _ pps 19, _ mp4Rate 20, _ slaveIP new IPAddress(63 << 24 + 48 << 16 + 22 << 8 + 123), _ opChan 21, _ proxyMode ProxyMode.PERSISTENT, _ proxyPri 22, _ proxyRetry 23)
 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[DisplayPicCGI, Builder](new Builder, new Builder, setters, "DisplayPicCGITest")
})
