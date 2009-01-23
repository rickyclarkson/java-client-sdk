package uk.org.netvu.protocol

import _root_.org.{specs, scalacheck}
import specs.{Specification, ScalaCheck}
import specs.runner.JUnit4

class DisplayPicCGITest extends JUnit4(new Specification {
 import DisplayPicCGI._
 "Building a DisplayPicCGI" should {
  "give a DisplayPicCGI containing the correct values" in {
   val builder = new Builder camera 4 fieldCount 5
   val url = "/display_pic.cgi?cam=4&fields=5"
   builder.build.toString mustEqual url
  }
 }

 "Parsing a display_pic.cgi request" should {
  "give a DisplayPicCGI containing the correct values" in {
   val cgi = fromString("/display_pic.cgi?cam=4&fields=5")
   cgi.getCamera mustEqual 4
   cgi.getFieldCount mustEqual 5
  }
 }

 "Parsing an empty String" should {
  "cause an IllegalArgumentException" in {
   fromString("") must throwAn[IllegalArgumentException]
  }
 }
   
 new Builder audioChannel -1 must throwAn[IllegalArgumentException]

 val setters = List[Builder => Builder](_ camera 4, _ fieldCount 5, _ resolution "hi", _ cameraSequenceMask "6", _ dwellTime 7, _ connectionID 8, _ preselector 3, _ channel 1,
                                        _ maximumTransmitRate 12, _ quantisationFactor 13, _ duration 14, _ bufferCount 15, _ quantisationFactorForTelemetryImages 16, _ packetSize 17, _ udpPort 18,
                                        _ audioChannel 1, _ format VideoFormat.MP4, _ audioMode AudioMode.INLINE, _ transmissionMode TransmissionMode.BINARY, _ picturesPerSecond 19, _ mp4Bitrate 20,
                                        _ slaveIP new IPAddress(63 << 24 + 48 << 16 + 22 << 8 + 123), _ outputChannel 21, _ proxyMode ProxyMode.PERSISTENT, _ proxyPriority 22, _ proxyRetries 23)
 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[DisplayPicCGI, Builder](new Builder, new Builder, setters, "DisplayPicCGITest")

 val cgi = new Builder().build
 import cgi._
 getCamera mustEqual 1
 getFieldCount mustEqual 1
 getResolution mustEqual "med"
 getCameraSequenceMask mustEqual "0"
 getDwellTime mustEqual 0
 getConnectionID mustEqual 0
 getPreselector mustEqual 0
 getChannel mustEqual -1
 getMaximumTransmitRate mustEqual 0
 getQuantisationFactor mustEqual 0
 getDuration mustEqual 0
 getBufferCount mustEqual 0
 getQuantisationFactorForTelemetryImages mustEqual -1
 getPacketSize mustEqual 0
 getUdpPort mustEqual 0
 getAudioChannel mustEqual 0
 getFormat mustEqual VideoFormat.JFIF
 getAudioMode mustEqual AudioMode.UDP
 getTransmissionMode mustEqual TransmissionMode.MIME
 getPicturesPerSecond mustEqual 0
 getMp4Bitrate mustEqual 0
 getSlaveIP mustEqual IPAddress.fromString("0.0.0.0").get
 getOutputChannel mustEqual -1
 getProxyMode mustEqual ProxyMode.TRANSIENT
 getProxyPriority mustEqual 1
 getProxyRetries mustEqual 0

 for (format <- List("mp4", "jfif", "jpeg"))
  DisplayPicCGI.fromString("/display_pic.cgi?format="+format).getFormat.toString.toLowerCase mustEqual format

 DisplayPicCGI.fromString("/display_pic.cgi?format=msdos") must throwAn[IllegalArgumentException]

 for (audioMode <- List("udp", "inline"))
  DisplayPicCGI.fromString("/display_pic.cgi?audmode="+audioMode).getAudioMode.toString.toLowerCase mustEqual audioMode

 DisplayPicCGI.fromString("/display_pic.cgi?audmode=fast") must throwAn[IllegalArgumentException]

 for (transmissionMode <- List("mime", "binary", "minimal"))
  DisplayPicCGI.fromString("/display_pic.cgi?txmode=" + transmissionMode).getTransmissionMode.toString.toLowerCase mustEqual transmissionMode

 DisplayPicCGI.fromString("/display_pic.cgi?txmode=backwards") must throwAn[IllegalArgumentException]

 for (proxyMode <- List("transient", "persistent"))
  DisplayPicCGI.fromString("/display_pic.cgi?proxymode=" + proxyMode).getProxyMode.toString.toLowerCase mustEqual proxyMode

 DisplayPicCGI.fromString("/display_pic.cgi?proxymode=tor") must throwAn[IllegalArgumentException]

 DisplayPicCGI.fromString("/display_pic.cgi?slaveip=192.168.0.1").getSlaveIP mustEqual IPAddress.fromString("192.168.0.1").get
 DisplayPicCGI.fromString("/display_pic.cgi?slaveip=19222.168.0.1") must throwAn[IllegalArgumentException]
 DisplayPicCGI.fromString("/display_pic.cgi?slaveip=-21.3.4.5") must throwAn[IllegalArgumentException]
 DisplayPicCGI.fromString("/display_pic.cgi?slaveip=1.2.3.4").getSlaveIP.rawValue mustEqual 1 << 24 | 2 << 16 | 3 << 8 | 4
})
