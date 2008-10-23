package uk.org.netvu.protocol

import _root_.org.{scalacheck, specs, junit}
import specs.runner.JUnit4
import specs.{Specification, Scalacheck}
import scalacheck.{Arbitrary,Prop,Shrink}
import Prop.{property, extendedBoolean}
import java.util.EnumSet
import scala.collection.jcl.Conversions.convertSet

class VPartsCGITest extends JUnit4(new Specification with Scalacheck {
 import VPartsCGI.Mode
 "Parsing an invalid URL" should {
  "cause an IllegalArgumentException" in {
   VPartsCGI.fromURL("vparts.cgi?foo=bar") must throwA(new IllegalArgumentException)
  }
 }
 "Building a VPartsCGI.Builder twice" should {
  "cause an IllegalStateException" in {
   val builder = new VPartsCGI.Builder()
   builder.build
   builder.build must throwA(new IllegalStateException)
  }
 }
 "Setting a parameter on a VPartsCGI.Builder after it has been built" should {
  "cause an IllegalStateException" in {
   val builder = new VPartsCGI.Builder()
   builder.build
   builder.mode(Mode.PROTECT) must throwA(new IllegalStateException)
  }
 }

 "Parsing a VPartsCGI.DirectoryPathFormat from an invalid String" should {
  "yield an empty Option" in {
   VPartsCGI.DirectoryPathFormat.fromStringFunction()("foo").isEmpty mustEqual true
  }
 }

 "Parsing a Mode from an invalid String" should {
  "yield an empty Option" in {
   Mode.fromStringFunction()("foo").isEmpty mustEqual true
  }
 }

 "Setting the format to HTML" should {
  "cause an IllegalArgumentException" in {
   new VPartsCGI.Builder().format(Format.HTML).build must throwA(new IllegalArgumentException)
  }
 }

 "Parsing a URL" should {
  "give a VPartsCGI containing the values held in the URL" in {
   val url = "/vparts.cgi?format=csv&mode=protect&time=958038820&range=120&pathstyle=long"
   val cgi = VPartsCGI.fromURL(url)
   cgi.getFormat mustEqual Format.CSV
   cgi.getMode mustEqual Mode.PROTECT
   cgi.getTime mustEqual 958038820
   cgi.getRange mustEqual 120
   cgi.getPathStyle mustEqual VPartsCGI.DirectoryPathFormat.LONG
  }
 }

 "Setting the time to a negative value" should {
  "cause an IllegalArgumentException" in {
   new VPartsCGI.Builder() time -10 must throwA(new IllegalArgumentException)
  }
 }

 "Setting the watermark step to an invalid value" should {
  "cause an IllegalArgumentException" in {
   new VPartsCGI.Builder() watermarkStep 1000 must throwA(new IllegalArgumentException)
  }
 }

 "A built VPartsCGI" should {
  "contain the values passed to it" in {
   property { x: Int => x >= 0 ==> { new VPartsCGI.Builder().expiry(x).mode(Mode.PROTECT).build.getExpiry == x } } must pass

   EnumSet.allOf(classOf[Mode]) forall { mode => new VPartsCGI.Builder().mode(mode).build.getMode mustEqual mode }

   property { x: Int => x >= 0 ==> { new VPartsCGI.Builder().time(x).build.getTime mustEqual x } } must pass

   property { x: Int => x >= 0 ==> { new VPartsCGI.Builder().range(x).build.getRange mustEqual x } } must pass

   List(false, true) forall { wm => new VPartsCGI.Builder().watermark(wm).build.getWatermark mustEqual wm }

   property {
    x: Int => (x >= 1 && x <= 256) ==> {
     new VPartsCGI.Builder().watermarkStep(x).watermark(true).build.getWatermarkStep mustEqual x
    }
   } must pass

   import EnumSet.{complementOf, of}
   complementOf(of(Format.HTML)) forall { f => new VPartsCGI.Builder().format(f).build.getFormat mustEqual f }

   property { x: Int => new VPartsCGI.Builder().listLength(x).build.getListLength mustEqual x } must pass

   EnumSet.allOf(classOf[VPartsCGI.DirectoryPathFormat]) forall {
    f => new VPartsCGI.Builder().pathStyle(f).build.getPathStyle mustEqual f
   }
  }
 }

 "Building a VPartsCGI with expiry set but not protect set" should {
  "cause an IllegalStateException" in {
   new VPartsCGI.Builder().expiry(4).build must throwA(new IllegalStateException)
   List(Mode.READ, Mode.REINITIALISE) foreach {
    mode => new VPartsCGI.Builder().expiry(4).mode(mode).build must throwA(new IllegalStateException)
   }
  }
 }

 "Building a VPartsCGI with watermark set but not read set" should {
  "cause an IllegalStateException" in {
   List(Mode.PROTECT, Mode.REINITIALISE) foreach {
    mode => new VPartsCGI.Builder().watermark(true).mode(mode).build must throwA(new IllegalStateException)
   }
  }
 }

 "Building a VPartsCGI with the watermark step set but without watermark set" should {
  "cause an IllegalStateException" in {
   new VPartsCGI.Builder().watermarkStep(4).build must throwA(new IllegalStateException)
  }
 }
})

class VPartsCGIResultTest extends JUnit4(new Specification with Scalacheck {
 "An end time less than the start time" should {
  "cause an IllegalStateException when build() is called" in {
   var builder = new VPartsCGIResult.Builder() index 5 directory "dir" filename "name" startTime 100 endTime 50
   builder = builder expiryTime 1000 numberOfEntries 20 camMask 4
   builder.build must throwA(new IllegalStateException)
  }
 }

 "Parsing valid CSV then generating CSV from the resulting object" should {
  "yield the same CSV" in {
   val csv = "2, C:\\VIDEO, VID02495.VID, 958038878, 958038962, 0, 519, 15"
   VPartsCGIResult.fromCSV(csv).toCSV mustEqual csv
  }
 }

 "Constructing an incomplete VPartsCGIResult" should {
  "cause an IllegalStateException" in {
   new VPartsCGIResult.Builder().build must throwA(new IllegalStateException)
  }
 }

 "A built VPartsCGIResult" should {
  "contain the values given to it" in {
   case class NonNegativeInt(i: Int) { assert(i >= 0) }

   import scalacheck.Gen
   implicit val nonNegativeArb = Arbitrary { Gen.choose(0, Integer.MAX_VALUE - 1).map(NonNegativeInt(_)) }

   //these chained properties should not be required when we upgrade ScalaCheck; we sent in a patch which was applied.
   property ((startTime: NonNegativeInt, numberOfEntries: Int) => 
    property ((index: Int, camMask: Int, directory: String, endTime: NonNegativeInt, expiryTime: NonNegativeInt,
               filename: String) =>
     endTime.i >= startTime.i ==> {
      var builder = new VPartsCGIResult.Builder().index(index).camMask(camMask).directory(directory).endTime(endTime.i)
      builder = builder.expiryTime(expiryTime.i).filename(filename).numberOfEntries(numberOfEntries)
      builder = builder.startTime(startTime.i)
      val result = builder.build
      var partialPass = result.getIndex == index && result.getCamMask == camMask && result.getDirectory == directory
      partialPass &&= (result.getEndTime == endTime.i && result.getExpiryTime == expiryTime.i)
      partialPass &&= (result.getFilename == filename && result.getNumberOfEntries == numberOfEntries)
      partialPass && startTime.i == result.getStartTime
     })) must pass
  }
 }

 import VPartsCGIResult.Builder
 val setters = List[Builder => Builder](_ startTime 4, _ numberOfEntries 4, _ index 4, _ camMask 4,
                                        _ directory "4", _ endTime 4, _ expiryTime 4, _ filename "4")
 "Setting the same value twice" should {
  "cause an IllegalStateException" in {
   setters foreach { setter => setter(setter(new Builder)) must throwA(new IllegalStateException) }
  }
 }

 "Setting a Builder's values after it has been built" should {
  "cause an IllegalStateException" in {
   def builder = {
    val b = new Builder
    b.build
    b
   }

   setters foreach { setter => setter(builder) must throwA(new IllegalStateException) }
  }
 }
})
