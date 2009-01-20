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

 "Parsing a display_pic.cgi request" should {
  "give a DisplayPicCGI containing the correct values" in {
   val cgi = fromString("/display_pic.cgi?cam=4&fields=5")
   cgi.getCam mustEqual 4
   cgi.getFields mustEqual 5
  }
 }
   
 new Builder audio "none" must throwA[IllegalStateException]

 val setters = List[Builder => Builder](_ cam 4, _ fields 5, _ res "hi", _ seq 6, _ dwell 7, _ id 8, _ dIndex 9, _ presel 3, _ channel 1, _ rate 12, _ forcedQ 13, _ duration 14, _ nBuffers 15, _ telemQ 16, _ pktSize 17, _ udpPort 18, _ audio "none", _ format Format.MP4, _ audioMode AudioMode.INLINE, _ transmissionMode TransmissionMode.BINARY, _ pps 19, _ mp4Rate 20, _ slaveIP new IPAddress(63 << 24 + 48 << 16 + 22 << 8 + 123), _ opChan 21, _ proxyMode ProxyMode.PERSISTENT, _ proxyPri 22, _ proxyRetry 23)
 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[DisplayPicCGI, Builder](new Builder, new Builder, setters, "DisplayPicCGITest")

 val cgi = new Builder().build
 import cgi._
 getCam mustEqual 1
 getFields mustEqual 1
 getRes mustEqual "med"
 getSeq mustEqual 0
 getDwell mustEqual 0
 getId mustEqual 0
 getDIndex mustEqual 0
 getPresel mustEqual 0
 getChannel mustEqual -1
 getRate mustEqual 0
 getForcedQ mustEqual 0
 getDuration mustEqual 0
 getNBuffers mustEqual 0
 getTelemQ mustEqual -1
 getPktSize mustEqual 0
 getUdpPort mustEqual 0
 getAudio mustEqual "0"
 getFormat mustEqual Format.JFIF
 getAudioMode mustEqual AudioMode.UDP
 getTransmissionMode mustEqual TransmissionMode.MIME
 getPPS mustEqual 0
 getMp4Rate mustEqual 0
 getSlaveIP mustEqual IPAddress.fromString("0.0.0.0").get
 getOpChan mustEqual -1
 getProxyMode mustEqual ProxyMode.TRANSIENT
 getProxyPri mustEqual 1
 getProxyRetry mustEqual 0
})
