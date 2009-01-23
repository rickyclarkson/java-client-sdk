package uk.org.netvu.protocol

import _root_.org.{specs, scalacheck}
import specs.{Specification, ScalaCheck}
import specs.runner.JUnit4

class ReplayPicCGITest extends JUnit4(new Specification {
 import ReplayPicCGI._
 "Building a ReplayPicCGI" should {
  "give a ReplayPicCGI containing the correct values" in {
   val builder = new Builder camera 4 fields 5
   val url = "/replay_pic.cgi?cam=4&fields=5"
   builder.build.toString mustEqual url
  }
 }

 "Parsing a replay_pic.cgi request" should {
  "give a ReplayPicCGI containing the correct values" in {
   val cgi = fromString("/replay_pic.cgi?cam=4&fields=5")
   cgi.getCamera mustEqual 4
   cgi.getFields mustEqual 5

   fromString("/replay_pic.cgi?time=10:20:30:10:11:09") match { case cgi => cgi.getGMTTime > 0 mustEqual true }
   fromString("/replay_pic.cgi?time=100") match { case cgi => cgi.getGMTTime mustEqual 100 }
   fromString("/replay_pic.cgi?time=tomorrow") must throwAn[IllegalArgumentException]
  }
 }

 "The default transmission mode for JFIF" should {
  "be MIME" in {
   fromString("/replay_pic.cgi?format=jfif").getTransmissionMode mustEqual TransmissionMode.MIME
  }
 }
  
 "The default transmission mode for MP4 and JPEG" should {
  "be MINIMAL" in {
   for (format <- List("mp4", "jpeg"))
    fromString("/replay_pic.cgi?format=mp4").getTransmissionMode mustEqual TransmissionMode.MINIMAL
  }
 }

 "Parsing an empty String" should {
  "cause an IllegalArgumentException" in {
   fromString("") must throwAn[IllegalArgumentException]
  }
 }
   
 val setters = List[Builder => Builder](_ camera 4, _ fields 5, _ cameraSequenceMask 6, _ connectionID 7, _ control Control.FFWD, _ gmtTime 8, _ localTime 9, _ maximumTransmitRate 10, _ text "bob", _ timeRange 11, _ audioOn OnOrOff.ON, _ fastForwardMultiplier 12, _ duration 13, _ resolution "hi", _ packetSize 110, _ udpPort 6, _ refresh 14, _ format Format.MP4, _ transmissionMode TransmissionMode.BINARY, _ slaveIP IPAddress.fromString("192.168.0.1").get, _ outputChannel 15, _ proxyMode ProxyMode.PERSISTENT, _ proxyRetries 16)
 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[ReplayPicCGI, Builder](new Builder, new Builder, setters, "ReplayPicCGITest")

 val cgi = new Builder().build
 import cgi._
 getCamera mustEqual 1
 getFields mustEqual 1
 getCameraSequenceMask mustEqual 0
 getConnectionID mustEqual 0
 getControl mustEqual Control.STOP
 getGMTTime mustEqual 0
 getLocalTime mustEqual 0
 getMaximumTransmitRate mustEqual 0
 getText mustEqual ""
 getTimeRange mustEqual 0
 isAudioOn mustEqual OnOrOff.OFF
 getFastForwardMultiplier mustEqual 0
 getDuration mustEqual 0
 getResolution mustEqual "med"
 getPacketSize mustEqual 0
 getUdpPort mustEqual 0
 getRefresh mustEqual 0
 getFormat mustEqual Format.JFIF
 getTransmissionMode mustEqual TransmissionMode.MIME
 getSlaveIP mustEqual IPAddress.fromString("0.0.0.0").get
 getOutputChannel mustEqual -1
 getProxyMode mustEqual ProxyMode.TRANSIENT
 getProxyRetries mustEqual 0

 for (format <- List("mp4", "jfif", "jpeg"))
  ReplayPicCGI.fromString("/replay_pic.cgi?format="+format).getFormat.toString.toLowerCase mustEqual format

 ReplayPicCGI.fromString("/replay_pic.cgi?format=msdos") must throwAn[IllegalArgumentException]
 
 for (control <- List("PLAY", "FFWD", "RWND", "STOP"))
  ReplayPicCGI.fromString("/replay_pic.cgi?control="+control).getControl.toString.toUpperCase mustEqual control

 ReplayPicCGI.fromString("/replay_pic.cgi?control=gofaster") must throwAn[IllegalArgumentException]

 for (txMode <- List("mime", "binary", "minimal"))
  ReplayPicCGI.fromString("/replay_pic.cgi?txmode="+txMode).getTransmissionMode.toString.toLowerCase mustEqual txMode

 ReplayPicCGI.fromString("/replay_pic.cgi?txmode=magic") must throwAn[IllegalArgumentException]

 for (proxyMode <- List("transient", "persistent"))
  ReplayPicCGI.fromString("/replay_pic.cgi?proxymode="+proxyMode).getProxyMode.toString.toLowerCase mustEqual proxyMode

 ReplayPicCGI.fromString("/replay_pic.cgi?proxymode=tor") must throwAn[IllegalArgumentException]

 for (onOrOff <- List("on", "off"))
  ReplayPicCGI.fromString("/replay_pic.cgi?audio="+onOrOff).isAudioOn.toString.toLowerCase mustEqual onOrOff

 ReplayPicCGI.fromString("/replay_pic.cgi?audio=whitenoise") must throwAn[IllegalArgumentException]
})
