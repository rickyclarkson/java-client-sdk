package uk.org.netvu.core.cgi.common

import _root_.org.{specs, scalacheck}

import specs.{Specification, Scalacheck}
import specs.util.DataTables
import specs.runner.JUnit4
import scalacheck.Prop.property

import java.util.Random

object Implicits {
 implicit def function1ToConversion[T, R](f: T => R) = new Conversion[T, R] { def convert(t: T) = f(t) }
 implicit def reductionToFunction2(r: Reduction[String, String]) = (o: String, n: String) => r.reduce(n, o)
 implicit def conversionToFunction1[T, R](f: Conversion[T, R]): T => R = t => f.convert(t)
 implicit def function2ToReduction[T, R](r: (T, R) => R): Reduction[T, R] =
  new Reduction[T, R] { override def reduce(newValue: T, original: R) = r(newValue, original) } }

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
   Parameter.parameterWithDefault("foo", 3, TwoWayConversion.partial(null, to)) must throwA(new NullPointerException)
   Parameter.parameterWithDefault("foo", 3, TwoWayConversion.partial(from, null)) must throwA(new NullPointerException)
   Parameter.parameterWithDefault("foo", 3, null) must throwA(new NullPointerException) } }

 def to[T] = new Conversion[T, Option[String]] { def convert(t: T) = Option.some("foo") }
 def from[T] = new Conversion[String, Option[T]] { def convert(s: String): Option[T] = Option.none[T]("Unsupported") }

 val param = Parameter.parameterWithDefault("foo", 3,
                                            TwoWayConversion.partial(from, to))
 
 "Supplying a value to an ordinary Parameter twice" should {
  "cause an IllegalStateException" in {
   new ParameterMap set (param, 4) set (param, 5) must throwA(new IllegalStateException) } }

 "Converting a Parameter to a URL" should {
  "give an Option holding the name of the Parameter when the Parameter has a non-default value" in {
   param.toURLParameter(new ParameterMap().set(param, 3)).get must include("foo") }
  "give an Option holding an empty String when the Parameter has a default value" in {
   param.toURLParameter(new ParameterMap).get mustEqual "" } }

 "Converting a URL to a Parameter" should {
  "succeed" in {
   val p = Parameter.parameterWithDefault[Integer]("foo", 3, TwoWayConversion.partial(Conversion.stringToInt, to))
   Parameter.not[Integer, Integer](4, p).fromURLParameter(new URLParameter("foo", "8")).get mustEqual 8 } }

 "A bound Parameter" should {
  val bound = Parameter.bound(1, 10, Parameter.parameterWithDefault("foo", 3, TwoWayConversion.partial(from, to)))
  "accept values inside its bounds" in {
   new ParameterMap().set[Integer, Integer](bound, 4) get bound mustEqual 4 }
  "reject values outside its bounds" in {
   new ParameterMap().set[Integer, Integer](bound, 140) must throwA(new IllegalArgumentException)
   new ParameterMap().set[Integer, Integer](bound, 0) must throwA(new IllegalArgumentException) } }

 import Pair.pair

 "A 'not' Parameter" should {
  val nested = Parameter.parameterWithDefault("foo", 3, TwoWayConversion.partial(from, to))
  val theNot = Parameter.not(5, nested)
  "allow unbanned values" in { new ParameterMap set (theNot, 2) get theNot mustEqual 2 }
  "reject banned values" in { new ParameterMap set (theNot, 5) must throwA(new IllegalArgumentException) }
  "delegate toURLParameter() calls to the nested Parameter" in {
   theNot.toURLParameter(pair("foo", 6)).get mustEqual nested.toURLParameter(pair("foo", 6)).get } }

 import scala.collection.jcl.Conversions.{convertList, convertMap}
 import java.util.TreeMap
 
 "A sparse array Parameter" should {
  val sparse = Parameter.sparseArrayParam[Integer]("foo", TwoWayConversion.integer)
  "be convertible from URL parameters" in {
   convertList(sparse.fromURLParameter(new URLParameter("foo[2]", "3,6,9")).get) must {
    haveSameElementsAs(List[Pair[Integer, Integer]](pair(2,3), pair(3,6), pair(4, 9))) } }
  "be convertible to URL parameters" in {
   val map: TreeMap[java.lang.Integer, Integer] = new TreeMap[java.lang.Integer, Integer] { put(2, 3)
                                                                                            put(3, 4) }
   sparse.toURLParameter(pair("foo", map)).get mustEqual "foo[2]=3&foo[3]=4" } } })

class OptionSecondTest extends JUnit4(new Specification with Scalacheck {
 import Option.{noneRef, someRef, none, some}

 "noneRef" should { "give a Conversion that always produces an empty Option" in {
  noneRef[Int, String]("foo").convert(5).isNone must beTrue } }

 "someRef" should { "give a Conversion that always produces an Option containing one element" in {
  someRef(Conversion.objectToString[Int]).convert(5).isNone must beFalse } }

 "none" should {
  "give an empty Option when mapped over" in {
   none[Int]("foo" ).map(new Conversion[Int, Int] { def convert(x: Int) = x * 2 } ).isNone must beTrue }
  "do nothing on 'then'" in { { none[Int]("foo").then(new Action[Int] { def invoke(x: Int) = throw null })
                                true } must beTrue }
  "give an empty Option on 'bind'" in {
   import Implicits.function1ToConversion
   none[Int]("foo").bind{ x: Int => some(x * 2) }.isNone must beTrue } }

 "toString" should { "throw an UnsupportedOperationException" in {
  none[Int]("foo").toString must throwA(new UnsupportedOperationException)
  some(5).toString must throwA(new UnsupportedOperationException) } }

 "reason" should {
  "throw a NullPointerException" in { some(5).reason must throwA(new NullPointerException) }
  "give a String" in { none("foo").reason mustEqual "foo" } }

 "equals" should { "cause an UnsupportedOperationException" in {
  some(5) == some(5) must throwA(new UnsupportedOperationException) } }

 "hashCode" should { "cause an UnsupportedOperationException" in {
  some(5).hashCode must throwA(new UnsupportedOperationException) } } })

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
  Conversion.stringToInt.convert("foo").isNone mustEqual true } }
 "Conversion.stringToBoolean" should {
  "give true when given \"true\"" in { Conversion.stringToBoolean.convert("true").get mustEqual true }
  "give false when given \"false\"" in { Conversion.stringToBoolean.convert("false").get mustEqual false }
  "give an empty Option when given \"foo\"" in { Conversion.stringToBoolean.convert("foo").isNone mustEqual true } }
 "Conversion.hexStringToInt" should { "give an empty Option when given \"foo\"" in {
  Conversion.hexStringToInt.convert("foo").isNone mustEqual true } }
 "Conversion.hexStringtoLong" should { "give an empty Option when given \"foo\"" in {
  Conversion.hexStringToLong.convert("foo").isNone mustEqual true } }
 "Conversion.equal(\"foo\")" should {
  "give true when given \"foo\"" in { Conversion.equal("foo").convert("foo") mustEqual true }
  "give false when given \"bar\"" in { Conversion.equal("foo").convert("bar") mustEqual false } }
 "Conversion.fromBoolean" should {
  "return its first parameter if the boolean is true" in { Conversion.fromBoolean(4, 10).convert(true) mustEqual 4 }
  "return its second parameter if the boolean if false" in { Conversion.fromBoolean(4, 10).convert(false) mustEqual 10 } } })

class ParameterMapSecondTest extends JUnit4(new Specification {
 import java.lang.Integer
 val parameter = Parameter.parameter[Integer]("foo", TwoWayConversion.integer)
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

class TwoWayConversionTest extends JUnit4(new Specification {
 import TwoWayConversion.{total, convenientTotal}
 import Implicits.function1ToConversion

 "total" should {
  "reject null parameters" in { total[Int, Int](null, null) must throwA(new NullPointerException)
                                total[Int, Int](null, ((_: Int) * 2)) must throwA(new NullPointerException)
                                total[Int, Int](((_: Int) / 2), null) must throwA(new NullPointerException) }
  "give a non-empty Option in both directions" in { val conversions = total( { x: Int => x * 2 }, { x: Int => x / 2 })
                                                    conversions.a2b.convert(5).isNone mustEqual false
                                                    conversions.b2a.convert(10).isNone mustEqual false } }
 "convenientTotal" should {
  "reject null parameters" in { convenientTotal[Int](null) must throwA(new NullPointerException) }
  "be able to convert from a String using the specified conversion" in {
   convenientTotal[Int]{ x: String => 5 }.a2b.convert("foo").get mustEqual 5 }
  "be able to convert to a String using Object's toString()" in {
   val obj = new Object
   convenientTotal[Object]{ x: String => x }.b2a.convert(obj).get mustEqual obj.toString } } })

/*class NullTest extends JUnit4(new Specification {
 import Implicits.conversionToFunction1
 import Implicits.function2ToReduction

 def fromFunction2[T, U, R](f: (T, U) => R)(implicit t: T, u: U) = List({ tt: T => f(tt, u) }, { uu: U => f(t, uu) })

 def fromFunction2YieldingConversion[T, U, R1, R2](f: (T, U) => Conversion[R1, R2])(implicit t: T, u: U): List[_ => _] =
  f(t, u) :: fromFunction2(f)

 def fromFunction3[T, U, V, R](f: (T, U, V) => R)(implicit t: T, u: U, v: V) = List({ tt: T => f(tt, u, v) },
                                                                                    { uu: U => f(t, uu, v) },
                                                                                    { vv: V => f(t, u, vv) })

 import java.util.{ArrayList, Arrays}
 import Implicits.function1ToConversion
 import java.lang.Boolean.{valueOf => toJavaBoolean}
 import java.lang.{UnsupportedOperationException => Unsupported}

 implicit val string = "foo"
 implicit val integer: Integer = 5
 implicit val twoWayConversion = TwoWayConversion.integer
 implicit def list[T]: java.util.List[T] = java.util.Collections.emptyList[T]
 implicit def conversion[T, U] = new Conversion[T, U] { override def convert(t: T) = throw new Unsupported }
 implicit def reduction[T, R] = new Reduction[T, R] { override def reduce(newValue: T, original: R) = throw new Unsupported }
 implicit val anInt = 5
 implicit val intArray = Array(1, 2, 3)
 implicit val aParameter = Parameter.parameter(string, twoWayConversion)
 implicit val unit = ()

 def notAcceptNull[T <: AnyRef, R] = new specs.matcher.Matcher[T => R] { def apply(f: => T => R) =
  try { f(null.asInstanceOf[T])
        (false, "should not be seen", "the method accepts null") } catch {
         case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen") } }

 def notAcceptNulls[T <: AnyRef, R] = new specs.matcher.Matcher[Conversion[T, R]] { def apply(conversion: Conversion[T, R]) =
  try { conversion.convert(null.asInstanceOf[T])
        (false, "should not be seen", "the method accepts null") } catch {
         case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen") } }

 "Checks.notNull" should { "not accept null" in { Checks.notNull _ must notAcceptNull[Nothing, Unit] } }
 "Conversion.stringToBoolean" should { "not accept null" in {
  Conversion.stringToBoolean must notAcceptNulls } }

/* List[_ => _](Checks.notNull _, Conversion.stringToBoolean, Conversion.stringToInt,
               Conversion.hexStringToInt, Conversion.hexStringToLong, Conversion.stringToLong, Conversion.longToHexString,
               Conversion.intToHexString, Conversion.equal _, Conversion.equal("foo")) foreach { method =>
                method must notAcceptNull }*/

 val methods: List[Function1[_ <: AnyRef, _]] = 
  List[_ => _](Checks.notNull _, Conversion.stringToBoolean, Conversion.stringToInt,
               Conversion.hexStringToInt, Conversion.hexStringToLong, Conversion.stringToLong, Conversion.longToHexString,
               Conversion.intToHexString, Conversion.equal _, Conversion.equal("foo")) ++
  fromFunction2YieldingConversion(Conversion.fromBoolean[String]) ++
  List[_ => _](Conversion.identity[Int], Conversion.objectToString[Int],
               new Conversion[Int, Int] { def convert(i: Int) = i * i }.andThen _,
               Format.fromString, Format.oneOf _, Generators.nonNegativeInts _,
               Generators.strings _, Generators.stringsAndNull _) ++
  fromFunction2(Lists.filter _) ++
  fromFunction2(Lists.map _) ++
  fromFunction2(Lists.reduce _) ++
  fromFunction2(Lists.remove[Unit] _) ++
  fromFunction2(Lists.removeIndices _) ++
  List(Lists.zipWithIndex _) ++
  fromFunction2(Lists.zip _) ++
  List(Option.none _) ++
  (List[Option[Int]](Option.none[Int]("because"), Option.some(5)) flatMap {
   o: Option[Int] => fromFunction2(o.fold[Int] _) ++
   List(o.map _, o.bind _, o.then _) }) ++
  List(Option.noneRef _, Option.some.convert _, { x: Int => Option.some(x) },
       Option.someRef _) ++
  fromFunction2(Pair.pair[Int, Int] _) ++
  List(Parameter.bound(3, 5, (_: Parameter[Integer, String]))) ++
  fromFunction2(Parameter.not[Integer, Option[Integer]] _) ++
  List(Parameter.notNegative[Option[Integer]] _) ++
  fromFunction2(Parameter.parameter[Integer] _) ++
  fromFunction3[String, Integer, TwoWayConversion[String, Integer], Parameter[Integer, Integer]](
   Parameter.parameterWithDefault[Integer] _) ++
  fromFunction2[String, TwoWayConversion[String, Integer], Parameter[java.util.List[Pair[Integer, Integer]], java.util.TreeMap[Integer, Integer]]](Parameter.sparseArrayParam[Integer] _) ++
  List({ x: Validator => new ParameterMap(x) })
 def ex[T <: AnyRef](method: T => _) = method(null.asInstanceOf[T])

 "Calling a method with a null parameter" should {
  "cause a NullPointerException" in {
   for (method <- methods) ex(method) must throwA(new NullPointerException) } } })
                               
                            
                            
*/
