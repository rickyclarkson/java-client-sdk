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

 import ArrayOrScalar.{ARRAY, SCALAR}
 val fooArray = new Variable("foo", ARRAY)
 val fooScalar = new Variable("foo", SCALAR)
 val barArray = new Variable("bar", ARRAY)
 val barScalar = new Variable("bar", SCALAR)

 "equals" should {
  "be true for equal Variables, and false otherwise" in {
   fooArray mustEqual fooArray
   fooArray == fooScalar must beFalse
   fooArray == barArray must beFalse
   fooArray == barScalar must beFalse
  }
  "be false for objects that aren't Variables" in {
   fooArray == "hello" must beFalse
  }
 }

 "hashCode" should {
  "be equal for equal Variables" in {
   List(fooArray, fooScalar, barArray, barScalar) foreach {
    v => v.hashCode mustEqual new Variable(v.getName(), if (v.isArray) ARRAY else SCALAR).hashCode
   }
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
   fromURL("foo") must throwA[IllegalArgumentException]
   fromURL("foo=bar") must throwA[IllegalArgumentException]
  }
 }
 "Calling build() on a VariableCGI.Builder twice" should { 
  "cause an IllegalStateException" in {
   val builder = new VariableCGI.Builder() variable Variable.UTC_OFFSET
   builder.build
   builder.build must throwA[IllegalStateException]
  }
 }

 "A VariableCGI built from a URL" should {
  "contain the URL's values" in {
   val v1 = VariableCGI.fromURL("/variable.cgi?variable=c_title[]")
   v1.getVariable.getName mustEqual "c_title"
   v1.getVariable.isArray must beTrue
   val v2 = VariableCGI.fromURL("/variable.cgi?variable=c_title[]&type=include")
   v2.getVariable.getName mustEqual "c_title"
   v2.getVariable.isArray must beTrue
   v2.getType mustEqual VariableType.INCLUDE
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
   new VariableCGI.Builder().build must throwA[IllegalStateException]
  }
 }

 "A VariableCGI produced by parsing a URL" should {
  "have the values present in the URL" in {
   VariableCGI.fromURL("foo/variable.cgi?variable=c_title[]").getVariable mustEqual Variable.C_TITLE
   VariableCGI.fromURL("foo/variable.cgi?variable=c_title[]&type=http").getType mustEqual VariableType.HTTP
   VariableCGI.fromURL("foo/variable.cgi?variable=has_rtc").getVariable mustEqual Variable.HAS_RTC
  }
 }

 import VariableCGI.Builder
 val setters = List[Builder => Builder](_ variable Variable.LIVE_CAM, _ `type` VariableType.INCLUDE)

 "Setting a Builder's values after it has been built" should {
  "cause an IllegalStateException" in {
   def builder = {
    val b = new Builder variable Variable.UTC_OFFSET
    b.build
    b
   }
                   
   setters foreach { setter => setter(builder) must throwA[IllegalStateException] }
  }
 }

 "Setting a Builder's value twice" should {
  "cause an IllegalStateException" in {
   setters foreach { setter => setter(setter(new Builder)) must throwA[IllegalStateException] }
  }
 }
})
