package uk.org.netvu.protocol

import _root_.org.specs._
import _root_.org.specs.util._
import _root_.org.specs.runner._

class VariableSecondTest extends JUnit4(new Specification {
 import Variable._
 "toString" should { "give the name of the variable in lowercase plus [] for an array" in {
  OUTPUT_TITLES.toString mustEqual "output_titles[]"
  UTC_OFFSET.toString mustEqual "utc_offset" } } })

class VariableTypeSecondTest extends JUnit4(new Specification {
 import VariableType._
 "fromStringFunction" should { "give an empty Option when passed an invalid String" in {
  fromStringFunction()("foo").isEmpty must beTrue } } })

class VariableCGISecondTest extends JUnit4(new Specification {
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
})
