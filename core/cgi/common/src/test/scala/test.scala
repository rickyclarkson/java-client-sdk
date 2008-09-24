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
  "give the same values on each call" in { def it = Generators.strings(new Random(0)).next
                                           it mustEqual it } } 
 "Generators.stringsAndNull" should {
  "start with null then continue with non-nulls." in { val gen = Generators.stringsAndNull(new Random(0))
                                                       gen.next mustBe null
                                                       gen.next mustNotBe null } }
 "Generators.nonNegativeInts" should {
  "give the same values on each call" in { def it = Generators.nonNegativeInts(new Random(0)).next
                                           it mustEqual it }
  "give non-negative ints" in {
   implicit def integer2int(x: java.lang.Integer) = x.intValue
   val gen = Generators.nonNegativeInts(new Random(0))
   for (i <- 0 to 99) gen.next.intValue must beGreaterThan(0) } }

 "All the methods" should {
  val methods = List[Random => Generator[_]](Generators.nonNegativeInts _, Generators.stringsAndNull _, Generators.strings _)
  "throw a NullPointerException if given a null Random" in {
   for (m <- methods) m(null).asInstanceOf[Any] must throwA(new NullPointerException) }
  "give the same values on each call" in {
   for (m <- methods) { def it = m(new Random(0)).next
                        it mustEqual it } } } } )
    
class ParametersSecondTest extends JUnit4(new Specification with Scalacheck {
 "Supplying a null toString to a Parameter" should {
  "cause a NullPointerException" in {
   Parameter.param("foo", "bar", 3, null) must throwA(new NullPointerException)
   Parameter.param4("foo", "bar", 3, null, to) must throwA(new NullPointerException)
   Parameter.param4("foo", "bar", 3, from, null) must throwA(new NullPointerException) } }

 def to[T] = new Conversion[T, Option[String]] { def convert(t: T) = Option.some("foo") }
 def from[T] = new Conversion[String, Option[T]] { def convert(s: String): Option[T] = Option.none[T] }

 val params = List(Parameter.param("foo", "bar", 3, from), Parameter.param4("foo", "bar", 3, from, to))
 
 "Supplying a value to an ordinary Parameter twice" should {
  "cause an IllegalStateException" in {
   for (p <- params) new ParameterMap `with` (p, 4) `with` (p, 5) must throwA(new IllegalStateException) } }

 "Converting a Parameter to a URL" should {
  "give an Option holding a String containing the name of the Parameter" in {
   for (p <- params) p.toURLParameter(Pair.pair("foo", 3)).get must include("foo") } }

 "A bound Parameter" should {
  val bound = Parameter.bound(1, 10, Parameter.param("foo", "bar", 3, from))
  "accept values inside its bounds" in { new ParameterMap().`with`[Integer, Integer](bound, 4) get bound mustEqual 4 }
  "reject values outside its bounds" in {
   new ParameterMap().`with`[Integer, Integer](bound, 140) must throwA(new IllegalArgumentException) } }

 "A 'not' Parameter" should {
  val theNot = Parameter.not(5, Parameter.param("foo", "bar", 3, from))
  "allow unbanned values" in { new ParameterMap `with` (theNot, 2) get theNot mustEqual 2 }
  "reject banned values" in { new ParameterMap `with` (theNot, 5) must throwA(new IllegalArgumentException) } }

 import scala.collection.jcl.Conversions.{convertList, convertMap}
 import Pair.pair
 import java.util.TreeMap
 "A sparse array Parameter" should {
  val sparse = Parameter.sparseArrayParam[Integer]("foo", "bar", Conversion.stringToInt, to)
  "be convertible from URL parameters" in {
   convertList(sparse.fromURLParameter(new URLParameter("foo[2]", "3,6,9")).get) must {
    haveSameElementsAs(List[Pair[Integer, Integer]](pair(2,3), pair(3,6), pair(4, 9))) } }
  "be convertible to URL parameters" in {
   val hack = new java.util.ArrayList[Pair[Integer, Integer]] { add(pair(2,3)) }
   convertMap(new ParameterMap().`with`(sparse, hack).get(sparse))(2) mustEqual 3 } } } ) 
