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
  "give an Option holding a String containing the name of the Parameter when the Parameter has a non-default value" in {
   for { p <- params map (p => Parameter.not(4, p)) }
    p.toURLParameter(new ParameterMap().`with`(p, 3)).get must include("foo") }
  "give an Option holding an empty String when the Parameter has a default value" in {
   for (p <- params) p.toURLParameter(new ParameterMap).get mustEqual "" } }

 "Converting a URL to a Parameter" should {
  "succeed" in {
   Parameter.not[Integer, Integer](4, Parameter.param4[Integer]("foo", "bar", 3, Conversion.stringToInt, to)).fromURLParameter(new URLParameter("foo", "8")).get mustEqual 8 } }

 "A bound Parameter" should {
  val bound = Parameter.bound(1, 10, Parameter.param("foo", "bar", 3, from))
  "accept values inside its boupnds" in { new ParameterMap().`with`[Integer, Integer](bound, 4) get bound mustEqual 4 }
  "reject values outside its bounds" in {
   new ParameterMap().`with`[Integer, Integer](bound, 140) must throwA(new IllegalArgumentException)
   new ParameterMap().`with`[Integer, Integer](bound, 0) must throwA(new IllegalArgumentException) } }

 "A 'not' Parameter" should {
  val theNot = Parameter.not(5, Parameter.param("foo", "bar", 3, from))
  "allow unbanned values" in { new ParameterMap `with` (theNot, 2) get theNot mustEqual 2 }
  "reject banned values" in { new ParameterMap `with` (theNot, 5) must throwA(new IllegalArgumentException) } }

 import scala.collection.jcl.Conversions.{convertList, convertMap}
 import Pair.pair
 import java.util.TreeMap
 
 "A sparse array Parameter" should {
  val sparse = Parameter.sparseArrayParam[Integer]("foo", "bar", Conversion.stringToInt, Conversion.objectToString[Integer] andThen Option.some[String])
  "be convertible from URL parameters" in {
   convertList(sparse.fromURLParameter(new URLParameter("foo[2]", "3,6,9")).get) must {
    haveSameElementsAs(List[Pair[Integer, Integer]](pair(2,3), pair(3,6), pair(4, 9))) } }
  "be convertible to URL parameters" in {
   val map: TreeMap[java.lang.Integer, Integer] = new TreeMap[java.lang.Integer, Integer] { put(2, 3)
                                                                                            put(3, 4) }
   sparse.toURLParameter(pair("foo", map)).get mustEqual "foo[2]=3&foo[3]=4" } } })

class OptionSecondTest extends JUnit4(new Specification with Scalacheck {
 "noneRef" should { "give a Conversion that always produces an empty Option" in {
  Option.noneRef[Int, String].convert(5).isNone must beTrue } }
 "someRef" should { "give a Conversion that always produces an Option containing one element" in {
  Option.someRef(Conversion.objectToString[Int]).convert(5).isNone must beFalse } }
 "Option.none" should {
  "give an empty Option when mapped over" in {
   Option.none[Int].map(new Conversion[Int, Int] { def convert(x: Int) = x * 2 } ).isNone must beTrue }
  "do nothing on 'then'" in {
   { Option.none[Int].then(new Action[Int] { def invoke(x: Int) = throw null })
     true } must beTrue }
  "give an empty Option on 'bind'" in {
   Option.none[Int].bind(new Conversion[Int, Option[Int]] { def convert(x: Int) = Option.some(x*2) } ).isNone must beTrue } }
 "Option.toString" should { "throw an UnsupportedOperationException" in {
  Option.none[Int].toString must throwA(new UnsupportedOperationException)
  Option.some(5).toString must throwA(new UnsupportedOperationException) } } })

class StringsSecondTest extends JUnit4(new Specification with Scalacheck {
 """fromFirst('l', "hello")""" should { "give lo" in { Strings.fromFirst('l', "hello") mustEqual("lo") } }
 "Interspersing a list from 1 to 5 with a comma" should { "give 1,2,3,4,5" in {
  Strings.intersperse(",",
                      new java.util.ArrayList[String] { for (a <- 1 to 5) add(a.toString) } ) mustEqual "1,2,3,4,5" } }
 "removeSurroundingQuotesLeniently" should {
  "not modify a part-quoted String or an unquoted String" in {
   for (s <- List("\"foo", "foo\"", "foo")) Strings.removeSurroundingQuotesLeniently(s) mustEqual s }
  "remove the quotes from a quoted String" in { Strings.removeSurroundingQuotesLeniently("\"foo\"" ) mustEqual "foo" } }
 "surroundWithQuotes" should { "surround a string with \"s" in {
  Strings.surroundWithQuotes.convert("bob") mustEqual("\"bob\"") } }
 "reversibleReplace" should {
  val replacer = Strings.reversibleReplace("l", "m")
  "be able to replace the 'll' in \"hello\" with 'mm'" in { replacer.replace("hello") mustEqual "hemmo" }
  "and back" in { replacer.undo("hemmo") mustEqual "hello" } } } )

class ListsSecondTest extends JUnit4(new Specification with Scalacheck {
 import java.util.ArrayList
 "zip" should { "take the minimum of the two lists" in {
  val one = new ArrayList[Int] { for (i <- 1 to 10) add(i) }
  val two = new ArrayList[Int] { for (i <- 1 to 20) add(i) }

  Lists.zip(one, two).size mustEqual 10
  Lists.zip(two, one).size mustEqual 10 } }
 "zipWithIndex" should {
  "work for an empty list" in { Lists.zipWithIndex(new ArrayList[Int]).size mustEqual 0 }
  "work for a single-element list" in {
   val list = Lists.zipWithIndex(new ArrayList[Int] { add(3) })
   list.size mustEqual 1
   list.get(0) mustEqual Pair.pair(3, 0) }
  "work for a 2-element list" in {
   val list = Lists.zipWithIndex(new ArrayList[Int] { add(3)
                                                      add(4) })
   list.size mustEqual 2
   list.get(0) mustEqual Pair.pair(3, 0)
   list.get(1) mustEqual Pair.pair(4, 1) } }
 "filter" should {
  "return an empty list when given one" in {
   Lists.filter(new ArrayList[Int], new Conversion[Int, java.lang.Boolean] { def convert(x: Int) = x > 0 }).size mustEqual 0 }
  "give all the elements that the predicate returns true for" in {
   val list = Lists.filter(new ArrayList[Int] { for (a <- 1 to 5) add(a) },
                           new Conversion[Int, java.lang.Boolean] { def convert(x: Int) = x % 2 == 0 })
   list.size mustEqual 2
   list.get(0) mustEqual 2
   list.get(1) mustEqual 4 } } })
