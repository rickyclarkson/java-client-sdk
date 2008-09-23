package uk.org.netvu.core.cgi.common

import _root_.org.specs.{Specification, Scalacheck}
import _root_.org.specs.runner.JUnit4
import _root_.org.scalacheck.Prop.property

import java.util.Random

class URLBuilderTest extends JUnit4(new Specification with Scalacheck {
 "Values encoded with URLBuilder" should {
  "be unaffected when decoded with the same encoding (UTF-8)" in {
   property { x: String => java.net.URLDecoder.decode(URLBuilder.encode.convert(x), "UTF-8") == x } must pass } } } )

class ReductionTest extends JUnit4(new Specification {
 "Interspersing 1, 2, 3, 4, 5 with ':'" should {
  "produce 1:2:3:4:5" in {
   List("1", "2", "3", "4", "5").reduceLeft{ (s, t) => Reduction.intersperseWith(":").reduce(t, s) } mustEqual "1:2:3:4:5" } } } )

class GeneratorsTest extends JUnit4(new Specification with Scalacheck {
 "Generators.strings" should {
  "throw a NullPointerException if given a null Random" in { Generators.strings(null) must throwA(new NullPointerException) }
  "give the same values on each call" in { def it = Generators.strings(new Random(0)).next
                                           it mustEqual it } } 
 "Generators.stringsAndNull" should {
  "throw a NullPointerException if given a null Random" in { Generators.stringsAndNull(null) must throwA(new NullPointerException) }
  "start with null then continue with non-nulls." in { val gen = Generators.stringsAndNull(new Random(0))
                                                       gen.next mustBe null
                                                       gen.next mustNotBe null } } } )
  
