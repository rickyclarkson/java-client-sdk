package uk.org.netvu.adffmpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer
import java.awt.Image
import uk.org.netvu.util.Buffers
import uk.org.netvu.codecs.VideoDecoder
import uk.org.netvu.codecs.VideoCodec
import uk.org.netvu.codecs.DecodingTests
import java.io.{File, FileInputStream, ByteArrayOutputStream}
import uk.org.netvu.util.Images
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class JPEGDecodingTest extends JUnit4(new Specification {
 val decoder = ADFFMPEGDecoders.getJPEGDecoder()
 decoder.toString + isSpecifiedBy(DecodingTests.validDecoder(decoder))
 decoder.dispose
})

class MPEG4DecodingTest extends JUnit4(new Specification {
 def asBufferedImage(image: Image) = {
  val result = new BufferedImage( image.getWidth( null ), image.getHeight( null ), BufferedImage.TYPE_INT_RGB )
  result.getGraphics.drawImage( image, 0, 0, null );
  result
 }

 def equal(one: Image, two: Image, tolerance: Int) = {
  val bi1 = asBufferedImage( one )
  val bi2 = asBufferedImage( two )
  val d1 = bi1.getData.getDataBuffer
  val d2 = bi2.getData.getDataBuffer
  d1.getSize == d2.getSize && !(0 until d1.getSize exists (a => {
   val e1 = d1 getElem a
   val e2 = d2 getElem a
   val r1 = e1 >> 16 & 0xFF
   val r2 = e2 >> 16 & 0xFF
   val g1 = e1 >> 8 & 0xFF
   val g2 = e2 >> 8 & 0xFF
   val b1 = e1 & 0xFF
   val b2 = e2 & 0xFF

   val rDiff = Math.abs( r2 - r1 )
   val gDiff = Math.abs( g2 - g1 )
   val bDiff = Math.abs( b2 - b1 )

   rDiff > tolerance || gDiff > tolerance || bDiff > tolerance }))
 }

 val decoder = ADFFMPEGDecoders.getMPEG4Decoder

 for (a <- 0 to 4) {
  val file = new File("testdata/mpeg4frames/" + a)
  val in = new FileInputStream(file)
  val out = new ByteArrayOutputStream
  var b = in.read
  while (b != -1) {
   out write b
   b = in.read
  }
  val bytes = out.toByteArray
  val buffer = ByteBuffer allocateDirect bytes.length
  buffer put bytes
  buffer position 0
  val image = decoder.decode(buffer)
  equal(image, ImageIO.read(new File("testdata/png/" + a + ".png")), 20) mustBe true
  image.getWidth(null) > 10 mustBe true
  image.getHeight(null) > 10 mustBe true
 }

 decoder.dispose
})
