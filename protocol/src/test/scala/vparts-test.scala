package uk.org.netvu.protocol

import _root_.org.{scalacheck, specs, junit}
import specs.runner.JUnit4
import specs.{Specification, Scalacheck}

class VPartsCGISecondTest extends JUnit4(new Specification with Scalacheck {
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
   builder.mode(VPartsCGI.Mode.PROTECT) must throwA(new IllegalStateException)
  }
 }

 "Parsing a VPartsCGI.DirectoryPathFormat from an invalid String" should {
  "yield an empty Option" in {
   VPartsCGI.DirectoryPathFormat.fromStringFunction()("foo").isEmpty mustEqual true
  }
 }

 "Parsing a VPartsCGI.Mode from an invalid String" should {
  "yield an empty Option" in {
   VPartsCGI.Mode.fromStringFunction()("foo").isEmpty mustEqual true
  }
 }
})

class VPartsCGIResultSecondTest extends JUnit4(new Specification with Scalacheck {
 "An end time less than the start time" should {
  "cause an IllegalStateException when build() is called" in {
   var builder = new VPartsCGIResult.Builder() index 5 directory "dir" filename "name" startTime 100 endTime 50
   builder = builder expiryTime 1000 numberOfEntries 20 camMask 4
   builder.build must throwA(new IllegalStateException)
  }
 }
})
