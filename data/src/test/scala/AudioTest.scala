package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

import java.io.FileInputStream

class AudioTest extends JUnit4(new Specification {
 "Parsing a stream containing ADPCM data" should {
  "parse out at least two ADPCM frames" in {
   var validPackets = 0
   ParserFactory parserFor StreamType.MIME parse (new FileInputStream("testdata/express_stv-mime-adpcm"), new StreamHandler {
    def audioDataArrived(packet: Packet) = validPackets += 1
    def infoArrived(packet: Packet) = ()
    def jpegFrameArrived(packet: Packet) = ()
    def mpeg4FrameArrived(packet: Packet) = ()
    def unknownDataArrived(packet: Packet) = ()
   })

   (validPackets>=2) mustEqual true
  }
 }
})