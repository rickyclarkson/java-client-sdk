package uk.org.netvu.jpeg

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4

import scala.collection.jcl.Conversions._

class DecodingTest extends JUnit4(new Specification {
 for { decoder <- SubBenchmark.decoders
       file <- Benchmark.sampleFiles } {
        ("Decoding " + file.filename) should {
         "give an Image whose width and height are the same as documented" in {
          { val image = JPEGDecoders.decodeJPEG(decoder.getFirstComponent).apply(SubBenchmark.bufferFor(file.filename))
           image.getWidth(null) mustEqual file.width
           image.getHeight(null) mustEqual file.height }
          { val image = JPEGDecoders.decodeJPEGFromArray(decoder.getSecondComponent).apply(SubBenchmark.byteArrayFor(file.filename))
           image.getWidth(null) mustEqual file.width
           image.getHeight(null) mustEqual file.height }
         }
        }
       }
} )
