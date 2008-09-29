package uk.org.netvu.core.cgi.common

import _root_.org.{specs, scalacheck}

import specs.{Specification, Scalacheck}
import specs.util.DataTables
import specs.runner.JUnit4
import scalacheck.Prop.property

import java.util.Random

object Implicits {
 implicit def function1ToConversion[T, R](f: T => R) = new Conversion[T, R] { def convert(t: T) = f(t) }
 implicit def reductionToFunction2(r: Reduction[String, String]) = (o: String, n: String) => r.reduce(n, o) }

class URLBuilderTest extends JUnit4(new Specification with Scalacheck {
 "Values encoded with URLBuilder" should {
  "be unaffected when decoded with the same encoding (UTF-8)" in {
   import java.net.URLDecoder.decode
   import URLBuilder.encode.convert
   property { x: String => decode(convert(x), "UTF-8") == x } must pass } } } )

class ReductionTest extends JUnit4(new Specification {
 import Implicits.reductionToFunction2

 "Interspersing 1, 2, 3, 4, 5 with ':'" should {
  "produce 1:2:3:4:5" in {
   List("1", "2", "3", "4", "5").reduceLeft{ Reduction.intersperseWith(":") } mustEqual "1:2:3:4:5" } } } )

class GeneratorsTest extends JUnit4(new Specification with Scalacheck {
 import Generators.{strings, stringsAndNull, nonNegativeInts}

 "strings" should { "give the same values on each call" in { def it = strings(new Random(0)).next
                                                             it mustEqual it } } 
 "stringsAndNull" should {
  "start with null then continue with non-nulls." in { val gen = stringsAndNull(new Random(0))
                                                       gen.next mustBe null
                                                       gen.next mustNotBe null } }
 "nonNegativeInts" should {
  "give the same values on each call" in { def it = nonNegativeInts(new Random(0)).next
                                           it mustEqual it }
  "give non-negative ints" in { implicit def integer2int(x: java.lang.Integer) = x.intValue
                                val gen = Generators.nonNegativeInts(new Random(0))
                                for (i <- 0 to 99) gen.next.intValue must beGreaterThan(0) } }

 "All the methods" should {
  val methods = List[Random => Generator[_]](nonNegativeInts _, stringsAndNull _, strings _)
  "throw a NullPointerException if given a null Random" in {
   for (m <- methods) m(null).asInstanceOf[Any] must throwA(new NullPointerException) }
  "give the same values on each call" in {
   for (m <- methods) { def it = m(new Random(0)).next
                        it mustEqual it } } } } )
    
class ParametersSecondTest extends JUnit4(new Specification with Scalacheck {
 "Supplying a null Conversion to a Parameter" should {
  "cause a NullPointerException" in {
   Parameter.param("foo", "bar", 3, null) must throwA(new NullPointerException)
   Parameter.param4("foo", "bar", 3, null, to) must throwA(new NullPointerException)
   Parameter.param4("foo", "bar", 3, from, null) must throwA(new NullPointerException) } }

 def to[T] = new Conversion[T, Option[String]] { def convert(t: T) = Option.some("foo") }
 def from[T] = new Conversion[String, Option[T]] { def convert(s: String): Option[T] = Option.none[T] }

 val params = List(Parameter.param("foo", "bar", 3, from), Parameter.param4("foo", "bar", 3, from, to))
 
 "Supplying a value to an ordinary Parameter twice" should {
  "cause an IllegalStateException" in {
   for (p <- params) new ParameterMap set (p, 4) set (p, 5) must throwA(new IllegalStateException) } }

 "Converting a Parameter to a URL" should {
  "give an Option holding the name of the Parameter when the Parameter has a non-default value" in {
   for { p <- params map (p => Parameter.not(4, p)) }
    p.toURLParameter(new ParameterMap().set(p, 3)).get must include("foo") }
  "give an Option holding an empty String when the Parameter has a default value" in {
   for (p <- params) p.toURLParameter(new ParameterMap).get mustEqual "" } }

 "Converting a URL to a Parameter" should {
  "succeed" in {
   val p = Parameter.param4[Integer]("foo", "bar", 3, Conversion.stringToInt, to)
   Parameter.not[Integer, Integer](4, p).fromURLParameter(new URLParameter("foo", "8")).get mustEqual 8 } }

 "A bound Parameter" should {
  val bound = Parameter.bound(1, 10, Parameter.param("foo", "bar", 3, from))
  "accept values inside its boupnds" in {
   new ParameterMap().set[Integer, Integer](bound, 4) get bound mustEqual 4 }
  "reject values outside its bounds" in {
   new ParameterMap().set[Integer, Integer](bound, 140) must throwA(new IllegalArgumentException)
   new ParameterMap().set[Integer, Integer](bound, 0) must throwA(new IllegalArgumentException) } }

 "A 'not' Parameter" should {
  val theNot = Parameter.not(5, Parameter.param("foo", "bar", 3, from))
  "allow unbanned values" in { new ParameterMap set (theNot, 2) get theNot mustEqual 2 }
  "reject banned values" in { new ParameterMap set (theNot, 5) must throwA(new IllegalArgumentException) } }

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
   import Implicits.function1ToConversion
   Option.none[Int].bind{ x: Int => Option.some(x * 2) }.isNone must beTrue } }
 "Option.toString" should { "throw an UnsupportedOperationException" in {
  Option.none[Int].toString must throwA(new UnsupportedOperationException)
  Option.some(5).toString must throwA(new UnsupportedOperationException) } } })

class StringsSecondTest extends JUnit4(new Specification with Scalacheck {
 import Strings.{ fromFirst, reversibleReplace, removeSurroundingQuotesLeniently, intersperse }

 """fromFirst('l', "hello")""" should { "give lo" in { Strings.fromFirst('l', "hello") mustEqual("lo") } }
 "Interspersing a list from 1 to 5 with a comma" should { "give 1,2,3,4,5" in {
  intersperse(",",
              new java.util.ArrayList[String] { for (a <- 1 to 5) add(a.toString) } ) mustEqual "1,2,3,4,5" } }
 "removeSurroundingQuotesLeniently" should {
  "not modify a part-quoted String or an unquoted String" in {
   for (s <- List("\"foo", "foo\"", "foo")) Strings.removeSurroundingQuotesLeniently(s) mustEqual s }
  "remove the quotes from a quoted String" in {
   removeSurroundingQuotesLeniently("\"foo\"" ) mustEqual "foo" } }

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
   import Implicits.function1ToConversion
   Lists.filter(new ArrayList[Int], (_ > 0): Int => java.lang.Boolean).size mustEqual 0 }
  "give all the elements that the predicate returns true for" in {
   val list = Lists.filter(new ArrayList[Int] { for (a <- 1 to 5) add(a) },
                           new Conversion[Int, java.lang.Boolean] { def convert(x: Int) = x % 2 == 0 })
   list.size mustEqual 2
   list.get(0) mustEqual 2
   list.get(1) mustEqual 4 } } })

class FormatSecondTest extends JUnit4(new Specification with Scalacheck {
 "Format.fromString" should { "give an empty Option when supplied with 'foo'" in {
  Format.fromString.convert("foo").isNone mustEqual true } } })

class ConversionSecondTest extends JUnit4(new Specification with Scalacheck {
 "Conversion.stringToInt" should { "give an empty Option when supplied with 'foo'" in {
  Conversion.stringToInt.convert("foo").isNone mustEqual true } } })

class ParameterMapSecondTest extends JUnit4(new Specification {
 import java.lang.Integer
 val parameter = Parameter.param[Integer]("foo", "bar", Conversion.stringToInt)
 "ParameterMap.set" should { "give a NullPointerException when supplied with a null" in {
  new ParameterMap().set(null, 3) must throwA(new NullPointerException)
  new ParameterMap().set(parameter, null) must throwA(new NullPointerException)
  new ParameterMap().set(null, null) must throwA(new NullPointerException) } }
 "setter" should { "be able to convert a ParameterMap into another with the supplied Parameter and value" in {
  ParameterMap.setter[Integer](parameter, 3).convert(new ParameterMap).get(parameter).get mustEqual 3 } } })

class URLParameterSecondTest extends JUnit4(new Specification with DataTables {
 "equals" should {
  "be false with a String" in { new URLParameter("foo", "bar") == "hi" mustEqual false }

  "be false with an unequivalent URLParameter" in {
   "name" | "value" |>
   "foo"  ! "baz"   |
   "baz"  ! "bar"   |
   "bar"  ! "foo"   | {
    (name, value) => new URLParameter("foo", "bar") == new URLParameter(name, value) mustEqual false } }

  "be true with an equivalent URLParameter" in {
   new URLParameter("foo", "bar") mustEqual new URLParameter("foo", "bar") } }

 "hashCode" should { "be equal with equal objects" in {
  new URLParameter("foo", "bar").hashCode mustEqual new URLParameter("foo", "bar").hashCode } } })
