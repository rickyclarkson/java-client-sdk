package uk.org.netvu.protocol

import _root_.org.{specs, scalacheck}
import specs.{Specification, ScalaCheck}
import specs.runner.JUnit4

class ReplayPicCGITest extends JUnit4(new Specification {
 import ReplayPicCGI._
 "Building a ReplayPicCGI" should {
  "give a ReplayPicCGI containing the correct values" in {
   val builder = new Builder cam 4 fields 5
   val url = "/replay_pic.cgi?cam=4&fields=5"
   builder.build.toString mustEqual url
  }
 }

 "Parsing a replay_pic.cgi request" should {
  "give a ReplayPicCGI containing the correct values" in {
   val cgi = fromString("/replay_pic.cgi?cam=4&fields=5")
   cgi.getCam mustEqual 4
   cgi.getFields mustEqual 5
  }
 }
   
 val setters = List[Builder => Builder](_ cam 4, _ fields 5, _ seq 6, _ id 7, _ control Control.FFWD, _ time 8, _ local 9, _ rate 10, _ text "bob", _ timeRange 11, _ audioOn OnOrOff.ON, _ fastForwardMultiplier 12, _ duration 13, _ res "hi", _ pktSize 110, _ refresh 14, _ format Format.MP4, _ transmissionMode TransmissionMode.BINARY, _ slaveIP IPAddress.fromString("192.168.0.1").get, _ opChan 15, _ proxyMode ProxyMode.PERSISTENT, _ proxyRetry 16)
 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[ReplayPicCGI, Builder](new Builder, new Builder, setters, "ReplayPicCGITest")

 val cgi = new Builder().build
 import cgi._
 getCam mustEqual 1
 getFields mustEqual 1
 getSeq mustEqual 0
 getId mustEqual 0
 getControl mustEqual Control.STOP
 getTime mustEqual 0
 getLocal mustEqual 0
 getRate mustEqual 0
 getText mustEqual ""
 getTimeRange mustEqual 0
 isAudioOn mustEqual OnOrOff.OFF
 getFastForwardMultiplier mustEqual 0
 getDuration mustEqual 0
 getRes mustEqual "med"
 getPktSize mustEqual 0
 getUdpPort mustEqual 0
 getRefresh mustEqual 0
 getFormat mustEqual Format.JFIF
 getTransmissionMode mustEqual TransmissionMode.MIME
 getSlaveIP mustEqual IPAddress.fromString("0.0.0.0").get
 getOpChan mustEqual -1
 getProxyMode mustEqual ProxyMode.TRANSIENT
 getProxyRetry mustEqual 0
})
