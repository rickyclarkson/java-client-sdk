package uk.org.netvu.core.cgi.variable

import _root_.org.specs._
import _root_.org.specs.util._
import _root_.org.specs.runner._

class VariableSecondTest extends JUnit4(new Specification {
 import Variable._
 "fromString" should { "give an empty Option when passed an invalid String" in {
  fromString("foo").isEmpty must beTrue } }
 "toString" should { "give the name of the variable in lowercase plus [] for an array" in {
  OUTPUT_TITLES.toString mustEqual "output_titles[]"
  UTC_OFFSET.toString mustEqual "utc_offset" } } })

class VariableTypeSecondTest extends JUnit4(new Specification {
 import VariableType._
 "fromString" should { "give an empty Option when passed an invalid String" in {
  fromString("foo").isEmpty must beTrue } } })
