package uk.org.netvu.protocol

import _root_.org.specs._
import _root_.org.specs.util._
import _root_.org.specs.runner._

class VariableTest extends JUnit4(new Specification {
 import Variable._
 "toString" should {
  "give the name of the variable in lowercase plus [] for an array" in {
   OUTPUT_TITLES.toString mustEqual "output_titles[]"
   UTC_OFFSET.toString mustEqual "utc_offset"
  }
 }
})

class VariableTypeSecondTest extends JUnit4(new Specification {
 import VariableType._
 "fromStringFunction" should { "give an empty Option when passed an invalid String" in {
  fromStringFunction()("foo").isEmpty must beTrue } } })

class VariableCGITest extends JUnit4(new Specification {
 import VariableCGI._
 "fromURL" should {
  "throw an IllegalArgumentException when passed an invalid String" in {
   fromURL("foo") must throwA(new IllegalArgumentException)
   fromURL("foo=bar") must throwA(new IllegalArgumentException)
  }
 }
 "Calling build() on a VariableCGI.Builder twice" should { 
  "cause an IllegalStateException" in {
   val builder = new VariableCGI.Builder() variable Variable.UTC_OFFSET
   builder.build
   builder.build must throwA(new IllegalStateException)
  }
 }

 "Setting parameters on a built VariableCGI.Builder" should {
  "cause an IllegalStateException" in {
   val builder = new VariableCGI.Builder() variable Variable.UTC_OFFSET
   builder.build
   builder `type` VariableType.HTTP must throwA(new IllegalStateException)
  }
 }

 "Parsing a URL into a VariableCGI then generating a URL" should {
  "yield the original URL" in {
    List("/variable.cgi?variable=c_title[]", "/variable.cgi?variable=c_title[]&type=include") foreach {
     s => VariableCGI.fromURL(s).toString mustEqual s
    }
  }
 }

 "A programmatically-built VariableCGI" should {
  "have the values supplied to it" in {
   val cgi = new VariableCGI.Builder().`type`(VariableType.INCLUDE).variable(Variable.C_TITLE).build
   cgi.getType mustEqual VariableType.INCLUDE
   cgi.getVariable mustEqual Variable.C_TITLE
  }
 }

 "Building a VariableCGI with no 'variable' parameter" should {
  "cause an IllegalStateException" in {
   new VariableCGI.Builder().build must throwA(new IllegalStateException)
  }
 }

 "A VariableCGI produced by parsing a URL" should {
  "have the values present in the URL" in {
   VariableCGI.fromURL("foo/variable.cgi?variable=c_title[]").getVariable mustEqual Variable.C_TITLE
   VariableCGI.fromURL("foo/variable.cgi?variable=c_title[]&type=http").getType mustEqual VariableType.HTTP
   VariableCGI.fromURL("foo/variable.cgi?variable=has_rtc").getVariable mustEqual Variable.HAS_RTC
  }
 }
})
