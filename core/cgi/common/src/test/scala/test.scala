package uk.org.netvu.core.cgi.common

import _root_.org.{specs, scalacheck}

import specs.{Specification, Scalacheck}
import specs.util.DataTables
import specs.runner.JUnit4
import scalacheck.Prop.property

import java.util.{Arrays, Random, List => JavaList, ArrayList => JavaArrayList, TreeMap => JavaTreeMap}
import java.lang.{Boolean => JavaBoolean}

import ParameterMap.Validator

object Implicits {
 implicit def function1ToConversion[T, R](f: T => R) = new Conversion[T, R] { def convert(t: T) = f(t) }
 implicit def reductionToFunction2(r: Reduction[String, String]) = (o: String, n: String) => r.reduce(n, o)
 implicit def conversionToFunction1[T, R](f: Conversion[T, R]): T => R = t => f.convert(t)

 implicit def function2ToReduction[T, R](r: (T, R) => R): Reduction[T, R] =
  new Reduction[T, R] { override def reduce(newValue: T, original: R) = r(newValue, original) }

 implicit def listToJavaList[T](list: List[T]): JavaList[T] = new JavaArrayList[T] { for (t <- list) add(t) }
 implicit def pairToTuple[T, U](pair: Pair[T, U]) = (pair.getFirstComponent, pair.getSecondComponent)
 implicit def tupleToPair[T, U](tuple: (T, U)) = new Pair(tuple._1, tuple._2)
}

import Implicits._

class URLEncoderTest extends JUnit4(new Specification with Scalacheck {
 "Values encoded with URLBuilder" should {
  "be unaffected when decoded with the same encoding (UTF-8)" in {
   property { x: String => java.net.URLDecoder.decode(new URLEncoder().convert(x), "UTF-8") == x } must pass
  }
 }
})

class ReductionTest extends JUnit4(new Specification {
 "Interspersing 1, 2, 3, 4, 5 with ':'" should {
  "produce 1:2:3:4:5" in {
   List("1", "2", "3", "4", "5") reduceLeft Reduction.intersperseWith(":") mustEqual "1:2:3:4:5"
  }
 }
})
    
class ParameterDescriptionTest extends JUnit4(new Specification with Scalacheck {
 import ParameterDescription.{parameterWithBounds, parameterWithDefault, sparseArrayParameter}

 def to[T] = new Conversion[T, Option[String]] { def convert(t: T) = Option.getFullOption("foo") }
 def from[T] = new Conversion[String, Option[T]] {
  def convert(s: String): Option[T] = Option.getEmptyOption[T]("Unsupported")
 }

 val parameterDescription = parameterWithDefault("foo", 3, StringConversion.partial(from, to))
 
 "Supplying a value to an ordinary ParameterDescription twice" should {
  "cause an IllegalStateException" in {
   new ParameterMap set (parameterDescription, 4) set (parameterDescription, 5) must throwA(new IllegalStateException)
  }
 }

 "Converting a Parameter to a URL" should {
  "give an Option holding the name of the Parameter when the parameter has a non-default value" in {
   parameterDescription.toURLParameter(new ParameterMap().set(parameterDescription, 3)).get must include("foo")
  }
  "give an Option holding an empty String when the parameter has its default value" in {
   parameterDescription.toURLParameter(new ParameterMap).get mustEqual ""
  }
 }

 "Converting a URL to a Parameter" should {
  "succeed" in {
   val p = parameterWithDefault[Integer]("foo", 3, StringConversion.partial(Conversion.getStringToIntConversion, to))
   ParameterDescription.parameterDisallowing[Integer, Integer](4, p).fromURLParameter(new URLParameter("foo", "8")).get mustEqual 8
  }
 }

 "A bound Parameter" should {
  val boundParam = parameterWithBounds(1, 10, parameterWithDefault("foo", 3, StringConversion.partial(from, to)))
  "accept values inside its bounds" in {
   new ParameterMap().set[Integer, Integer](boundParam, 4) get boundParam mustEqual 4
  }
  "reject values outside its bounds" in {
   new ParameterMap().set[Integer, Integer](boundParam, 140) must throwA(new IllegalArgumentException)
   new ParameterMap().set[Integer, Integer](boundParam, 0) must throwA(new IllegalArgumentException)
  }
 }

 "A 'not' Parameter" should {
  val nested = parameterWithDefault("foo", 3, StringConversion.partial(from, to))
  val theNot = ParameterDescription.parameterDisallowing(5, nested)
  "allow unbanned values" in { new ParameterMap set (theNot, 2) get theNot mustEqual 2 }
  "reject banned values" in { new ParameterMap set (theNot, 5) must throwA(new IllegalArgumentException) }
  "delegate toURLParameter() calls to the nested Parameter" in {
   theNot.toURLParameter(6).get mustEqual nested.toURLParameter(6).get
  }
 }

 import scala.collection.jcl.Conversions.{convertList, convertMap}
 import java.util.TreeMap
 
 "A sparse array Parameter" should {
  val sparse = sparseArrayParameter[Integer]("foo", StringConversion.integer)
  "be convertible from URL parameters" in {
   convertList(sparse.fromURLParameter(new URLParameter("foo[2]", "3,6,9")).get) must {
    haveSameElementsAs(List[Pair[Integer, Integer]](new Pair(2,3), new Pair(3,6), new Pair(4, 9)))
   }
  }
  "be convertible to URL parameters" in {
   val map: TreeMap[java.lang.Integer, Integer] = new TreeMap[java.lang.Integer, Integer] {
    put(2, 3)
    put(3, 4)
   }
   sparse.toURLParameter(map).get mustEqual "foo[2]=3&foo[3]=4"
  }
 }
})

class OptionTest extends JUnit4(new Specification with Scalacheck {
 import Option.{getConversionToEmptyOption, getConversionToFullOption, getEmptyOption, getFullOption, toPartialConversion}

 "getConversionToEmptyOption" should {
  "give a Conversion that always produces an empty Option" in {
   getConversionToEmptyOption[Int, String]("foo").convert(5).isEmpty must beTrue
  }
 }

 "toPartialConversion" should {
  "give a Conversion that always produces an Option containing one element" in {
   toPartialConversion(Conversion.getObjectToStringConversion[Int]).convert(5).isEmpty must beFalse
  }
 }

 "getEmptyOption" should {
  "give an empty Option when mapped over" in {
   getEmptyOption[Int]("foo" ).map(new Conversion[Int, Int] { def convert(x: Int) = x * 2 } ).isEmpty must beTrue
  }
  "give an empty Option on 'bind'" in {
   import Implicits.function1ToConversion
   getEmptyOption[Int]("foo").bind{ x: Int => getFullOption(x * 2) }.isEmpty must beTrue
  }
 }

 "toString" should {
  "throw an UnsupportedOperationException" in {
   getEmptyOption[Int]("foo").toString must throwA(new UnsupportedOperationException)
   getFullOption(5).toString must throwA(new UnsupportedOperationException)
  }
 }

 "reason" should {
  "throw an IllegalStateException" in { getFullOption(5).reason must throwA(new IllegalStateException) }
  "give a String" in { getEmptyOption("foo").reason mustEqual "foo" }
 }

 "equals" should {
  "cause an UnsupportedOperationException" in {
   getFullOption(5) == getFullOption(5) must throwA(new UnsupportedOperationException)
  }
 }

 "hashCode" should {
  "cause an UnsupportedOperationException" in {
   getFullOption(5).hashCode must throwA(new UnsupportedOperationException)
  }
 }

 "get" should {
  "give the stored value for an Option that contains a value" in { getFullOption(5).get mustEqual 5 }
  "throw an IllegalStateException for an empty Option" in {
   getEmptyOption[Integer]("foo").get must throwA(new IllegalStateException)
  }
 }

 "isEmpty" should {
  "be true for an empty Option" in { getEmptyOption("foo").isEmpty mustEqual true }
  "be false for an Option that contains a value" in { getFullOption(5).isEmpty mustEqual false }
 }
})

class StringsTest extends JUnit4(new Specification with Scalacheck {
 import Strings.{ fromFirst, removeSurroundingQuotesLeniently, intersperse,
                  afterFirstLeniently, splitIgnoringQuotedSections }

 "afterfirstLeniently" should {
  "give the part of a String after the first instance of a separator, "+
  "or the whole String if there is no such instance" in {
   afterFirstLeniently("hello", "-") mustEqual "hello"
   afterFirstLeniently("", "-") mustEqual ""
   afterFirstLeniently("hello - world", "-") mustEqual " world"
   afterFirstLeniently("hello - world - spam", "-") mustEqual " world - spam"
  }
 }

 """fromFirst('l', "hello")""" should { "give lo" in { Strings.fromFirst('l', "hello") mustEqual "lo" } }

 "Interspersing a list from 1 to 5 with a comma" should {
  "give 1,2,3,4,5" in {
   intersperse(",", new java.util.ArrayList[String] { for (a <- 1 to 5) add(a.toString) } ) mustEqual "1,2,3,4,5"
  }
 }

 "removeSurroundingQuotesLeniently" should {
  "not modify a part-quoted String or an unquoted String" in {
   for (s <- List("\"foo", "foo\"", "foo")) Strings.removeSurroundingQuotesLeniently(s) mustEqual s
  }
  "remove the quotes from a quoted String" in {
   removeSurroundingQuotesLeniently("\"foo\"" ) mustEqual "foo"
  }
 }

 "surroundWithQuotes" should {
  "surround a string with \"s" in {
   Strings.surroundWithQuotes.convert("bob") mustEqual("\"bob\"")
  }
 }

 "split" should {
  "split a comma-separated String into an array of Strings, ignoring whitespace after commas" in {
   Arrays.asList(Strings.splitCSV("oh, my,word")) mustEqual Arrays.asList(Array("oh", "my", "word"))
  }
 }
 "splitIgnoringQuotedSections with a comma separator" should {
  "split \"one,two,three\" into 3 Strings" in {
   splitIgnoringQuotedSections("one,two,three", ',').size mustEqual 3
  }
  "split \"one,two \\\" and a third, a fourth and perhaps, \\\",a fifth\" into 3 Strings" in {
   splitIgnoringQuotedSections("one,two \" and a third, a fourth and perhaps, \",a fifth", ',').size mustEqual 3
  }
 }
})

class ListsTest extends JUnit4(new Specification with Scalacheck {
 import java.util.ArrayList
 "zip" should {
  "take the minimum of the two lists" in {
   val one = new ArrayList[Int] { for (i <- 1 to 10) add(i) }
   val two = new ArrayList[Int] { for (i <- 1 to 20) add(i) }

   Lists.zip(one, two).size mustEqual 10
   Lists.zip(two, one).size mustEqual 10
  }
 }

 "filter" should {
  "return an empty list when given an empty list" in {
   import Implicits.function1ToConversion
   Lists.filter(new ArrayList[Int], (_ > 0): Int => java.lang.Boolean).size mustEqual 0
  }
  "give all the elements that the predicate returns true for" in {
   val list = Lists.filter(new ArrayList[Int] { for (a <- 1 to 5) add(a) },
                           new Conversion[Int, java.lang.Boolean] { def convert(x: Int) = x % 2 == 0 })
   list.size mustEqual 2
   list.get(0) mustEqual 2
   list.get(1) mustEqual 4
  }
 }

 import Implicits.function2ToReduction

 "reduce" should {
  "be able to sum a List containing 1, 2 and 3 to yield 6" in {
   Lists.reduce[Integer](Arrays.asList(Array[Integer](1, 2, 3)),
                         ((x: Integer), (y: Integer)) => Integer.valueOf(x.intValue + y.intValue)) mustEqual 6
  }
 }
 
 "remove" should {
  "be able to remove 3 from the list 1, 2, 3" in {
   Lists.remove[Integer](Arrays.asList(Array[Integer](1, 2, 3)), 3) == Arrays.asList(Array[Integer](1, 2))
  }
 }

 "removeIndices" should {
  "be able to remove the elements with indices 1 and 3 from 0, 1, 2, 3, 4" in {
   Lists.removeByIndices(Arrays.asList(Array[Integer](0, 1, 2, 3, 4)), Array(1, 3)) == Arrays.asList(Array[Integer](0, 2, 4))
  }
 }
})

class FormatTest extends JUnit4(new Specification with Scalacheck {
 import scalacheck.{Gen, Arbitrary}

 implicit val arbFormat: Arbitrary[Format] =
  Arbitrary(Gen.choose(1, 1000000).map(i => Format.oneOf(new Random(i))))

 "Format.fromString" should {
  "give an empty Option when supplied with 'foo'" in {
   Format.fromString.convert("foo").isEmpty mustEqual true
  }
 }

 "Formats" should {
  "have a lowercase String representation" in {
   property { f: Format => f.toString == f.toString.toLowerCase }
  }
  "be retrievable by their String representation" in {
   property { f: Format => Format.fromString.convert(f.toString).get == f }
  }
 }
})

class ConversionTest extends JUnit4(new Specification with Scalacheck {
 "Conversion.andThen" should {
  "result in a Conversion that behaves as if the value had been explicitly passed through each Conversion" in {
   val doubleIt: Conversion[Integer, Integer] = { x: Integer => Integer.valueOf(x.intValue * 2) }
   doubleIt andThen Conversion.getIntToHexStringConversion convert 127 mustEqual "fe"
  }
 }

 "Conversion.getStringToIntConversion" should {
  "give an empty Option when supplied with 'foo'" in {
   Conversion.getStringToIntConversion.convert("foo").isEmpty mustEqual true
  }
  "correctly convert Strings to ints" in { Conversion.getStringToIntConversion.convert("465").get mustEqual 465 }
 }

 "Conversion.getStringToLongConversion" should {
  "give an empty Option when supplied with \"foo\"" in {
   Conversion.getStringToIntConversion.convert("foo").isEmpty mustEqual true
  }
  "correctly convert Strings to longs" in { Conversion.getStringToIntConversion.convert("465").get mustEqual 465 }
 }

 "Conversion.getStringToBooleanConversion" should {
  "give true when given \"true\"" in { Conversion.getStringToBooleanConversion.convert("true").get mustEqual true }
  "give false when given \"false\"" in { Conversion.getStringToBooleanConversion.convert("false").get mustEqual false }
  "give an empty Option when given \"foo\"" in {
   Conversion.getStringToBooleanConversion.convert("foo").isEmpty mustEqual true
  }
 }

 "Conversion.getHexStringToIntConversion" should {
  "give an empty Option when given \"foo\"" in { Conversion.getHexStringToIntConversion.convert("foo").isEmpty mustEqual true }
  "correctly convert hex Strings to ints" in { Conversion.getHexStringToIntConversion.convert("Fe").get mustEqual 254 }
 }

 "Conversion.getHexStringtoLongConversion" should {
  "give an empty Option when given \"foo\"" in {
   Conversion.getHexStringToLongConversion.convert("foo").isEmpty mustEqual true
  }
  "correctly convert hex Strings to longs" in { Conversion.getHexStringToLongConversion.convert("Fe").get mustEqual 254 }
 }

 "Conversion.getIdentityConversion" should {
  "give the value passed to it" in { Conversion.getIdentityConversion[Boolean].convert(true) mustEqual true }
 }

 "Conversion.getIntToHexStringConversion" should {
  "correctly convert ints to hexadecimal Strings" in { Conversion.getIntToHexStringConversion.convert(255) mustEqual "ff" }
 }

 "Conversion.getLongToHexStringConversion" should {
  "correctly convert longs to hexadecimal Strings" in { Conversion.getLongToHexStringConversion.convert(255L) mustEqual "ff" }
 }

 "Conversion.equal(\"foo\")" should {
  "give true when given \"foo\"" in { Conversion.equal("foo").convert("foo") mustEqual true }
  "give false when given \"bar\"" in { Conversion.equal("foo").convert("bar") mustEqual false }
 }

 "Conversion.fromBoolean" should {
  "return its first parameter if the boolean is true" in { Conversion.fromBoolean(4, 10).convert(true) mustEqual 4 }
  "return its second parameter if the boolean if false" in { Conversion.fromBoolean(4, 10).convert(false) mustEqual 10 }
 }
})

class URLParameterTest extends JUnit4(new Specification with DataTables {
 "equals" should {
  "be false with a String" in { new URLParameter("foo", "bar") == "hi" mustEqual false }

  "be false with an unequivalent URLParameter" in {
   "name" | "value" |>
   "foo"  ! "baz"   |
   "baz"  ! "bar"   |
   "bar"  ! "foo"   | {
    (name, value) => new URLParameter("foo", "bar") == new URLParameter(name, value) mustEqual false
   }
  }

  "be true with an equivalent URLParameter" in {
   new URLParameter("foo", "bar") mustEqual new URLParameter("foo", "bar")
  }
 }

 "hashCode" should {
  "be equal with equal objects" in {
   new URLParameter("foo", "bar").hashCode mustEqual new URLParameter("foo", "bar").hashCode
  }
 }
})

class StringConversionTest extends JUnit4(new Specification {
 import StringConversion.{total, convenientTotal}
 import Implicits.function1ToConversion

 "total" should {
  "reject null parameters" in { total[Int](null, null) must throwA(new NullPointerException)
                                total[Int](null, String valueOf (_: Int)) must throwA(new NullPointerException)
                                total[Int](Integer parseInt (_: String), null) must throwA(new NullPointerException) }
  "give a non-empty Option in both directions" in { val conversions = total( { x: String => 5 }, { x: Int => "foo" })
                                                    conversions.fromString("bah").isEmpty mustEqual false
                                                    conversions.toString(10).isEmpty mustEqual false } }
 "convenientTotal" should {
  "reject null parameters" in { convenientTotal[Int](null) must throwA(new NullPointerException) }
  "be able to convert from a String using the specified conversion" in {
   convenientTotal[Int]{ x: String => 5 }.fromString("foo").get mustEqual 5 }
  "be able to convert to a String using Object's toString()" in {
   val obj = new Object
   convenientTotal[Object]{ x: String => x }.toString(obj).get mustEqual obj.toString } } })

class PairTest extends JUnit4(new Specification {
 "new Pair(t, u)" should {
  "retain its values" in {
   new Pair(3, 4).getFirstComponent mustEqual 3
   new Pair(3, 4).getSecondComponent mustEqual 4
  }
 }
})

class ValidatorTest extends JUnit4(new Specification {
 val nameParam = ParameterDescription.parameterWithoutDefault("name", StringConversion.string)
 val addressParam = ParameterDescription.parameterWithoutDefault("address", StringConversion.string)
 val validator = Validator.mutuallyExclusive(Arrays.asList(Array(nameParam, addressParam)))

 "mutually exclusive parameters" should {
  "really be mutually exclusive" in {
   val map = new ParameterMap(validator).set(nameParam, "bob")
   map.set(addressParam, "here") must throwA(new IllegalStateException)
  }
 }
})

class ParameterMapTest extends JUnit4(new Specification {
 import ParameterDescription.{parameterWithoutDefault, nonNegativeParameter, parameterWithDefault, sparseArrayParameter}

 "ParameterMap.fromStrings" should {
  "store the passed-in values in each Parameter" in {
   val forename = parameterWithDefault("name", "Bob", StringConversion.string)
   val surname = parameterWithDefault("surname", "Hope", StringConversion.string)
   val params: java.util.List[ParameterDescription[_, _]] = Arrays.asList(Array(forename, surname))

   ParameterMap.fromStrings(params, Arrays.asList(Array[String]("John", "Major"))).get.get(surname) mustEqual "Major"
  }
 }
 
 val param = parameterWithoutDefault("blah", StringConversion.string)
 val time = nonNegativeParameter( parameterWithoutDefault("time", StringConversion.integer))
 val range = parameterWithoutDefault("range", StringConversion.integer)

 "A value stored in a ParameterMap" should {
  "get converted according to the Parameter's rules" in {
   new ParameterMap().set(param, "10").get(param).get mustEqual "10"
  }
 }

 "Parsing a URL" should {
  "result in a ParameterMap holding the correct values" in {
   val params: JavaList[ParameterDescription[_, _]] = List(time, range)
   val parameterMap = ParameterMap.fromURL("time=10&range=40", params).get
   parameterMap.get(time).get mustEqual 10
   parameterMap.get(range).get mustEqual 40
   parameterMap.toURLParameters(params) mustEqual "time=10&range=40"
  }
 }

 "Parameter.isDefault" should {
  "give 'true' for a Parameter that only has its default value" in {
   new ParameterMap().isDefault(time) mustEqual true
   new ParameterMap().set[Integer, Option[Integer]](time, 40).isDefault(time) mustEqual false
  }
 }

 "Setting the same once-only Parameter twice" should {
  "result in an IllegalStateException" in {
   new ParameterMap().set(param, "10").set(param, "10") must throwA(new IllegalStateException)
  }
 }

 "Populating a sparse array Parameter and then reading back its values" should {
  "yield the original values" in {
   val p: ParameterDescription[JavaList[Pair[Integer, String]], JavaTreeMap[Integer, String]] =
    sparseArrayParameter[String]("foo", StringConversion.partial(Option.getConversionToFullOption[String],
                                                                 Option.getConversionToEmptyOption("Conversion not supported")))

   def convert(list: List[(Int, String)]): JavaList[Pair[Integer, String]] =
    listToJavaList(list map (tuple => new Pair(tuple._1, tuple._2)))

   val list1 = List(4 -> "bar", 5 -> "foo")
   val list2 = List(2 -> "baz", 10 -> "spam")

   val temp = new ParameterMap().set(p, convert(List(4 -> "bar", 5 -> "foo")))
   temp.set(p, convert(List(2 -> "baz", 10 -> "spam"))).get(p).get(2) mustEqual "baz"
  }
 }

 "Giving an invalid combination of Parameter values" should {
  "cause an IllegalStateException" in {
   new ParameterMap(new Validator {
    override def isValid(parameterMap: ParameterMap) = {
     val oTime = parameterMap.get(time)
     val oRange = parameterMap.get(range)
   
     oTime.fold(true, {
      time: Integer => oRange.fold(true, { range: Integer => time.intValue + range.intValue >= 0 })
     })
    }
   }).set[Integer, Option[Integer]](time, 2000000000).set[Integer, Option[Integer]](range, 2000000000) must {
    throwA(new IllegalStateException)
   }
  }
 }

 import java.lang.Integer
 val param2 = ParameterDescription.parameterWithoutDefault[Integer]("foo", StringConversion.integer)
/* "setter" should {
  "be able to convert a ParameterMap into another with the supplied Parameter and value" in {
   ParameterMap.setter[Integer](param2, 3).convert(new ParameterMap).get(param2).get mustEqual 3
  }
 }*/
})

class URLExtractorTest extends JUnit4(new Specification {
 import URLExtractor.{nameValuePairs, parameters, queryName}

 "nameValuePairs" should {
  "give an empty list for an empty String" in { nameValuePairs("").isEmpty mustEqual true }
  "parse foo and bar from foo=bar" in {
   nameValuePairs("foo=bar") mustEqual listToJavaList(List(new URLParameter("foo", "bar")))
  }
  "parse foo, bar, baz and spam from foo=bar&baz=spam" in {
   nameValuePairs("foo=bar&baz=spam") mustEqual listToJavaList(List(new URLParameter("foo", "bar"),
                                                                    new URLParameter("baz", "spam")))
  }
  "parse foo, \"bar=baz\", spam and \"eggs\"" in {
   nameValuePairs("foo=\"bar=baz\"&spam=\"eggs\"") mustEqual listToJavaList(List(new URLParameter("foo", "\"bar=baz\""),
                                                                                 new URLParameter("spam", "\"eggs\"")))
  }
 }

 "parameters" should {
  "give a list of size 1 for an empty String" in { parameters("").size mustEqual 1 }
  "give a list of size 1 for foo=bar" in { parameters("foo=bar").size mustEqual 1 }
  "give a list of size 2 for foo=bar&baz=spam where the second element is baz=spam" in {
   parameters("foo=bar&baz=spam").size mustEqual 2
   parameters("foo=bar&baz=spam").get(1) mustEqual "baz=spam"
  }
  "give a list of size 2 for foo=\"bar=baz\"&spam=\"eggs\"" in {
   parameters("foo=\"bar=baz\"&spam=\"eggs\"").size mustEqual 2
  }
 }
 "queryName" should {
  "give an empty String for an empty String" in { queryName("") mustEqual "" }
  "give foo for foo" in { queryName("foo") mustEqual "foo" }
  "give bar for foo/bar" in { queryName("foo/bar") mustEqual "bar" }
  "give bar.cgi for foo/bar.cgi?blah" in { queryName("foo/bar.cgi?blah") mustEqual "bar.cgi" }
  "give an empty String for ?blah" in { queryName("?blah") mustEqual "" }
  "give foo for foo?bar?" in { queryName("foo?bar?") mustEqual "foo" }
 }
})

class NullTest extends JUnit4(new Specification {
 def fromFunction2[T, U, R](f: (T, U) => R)(implicit t: T, u: U) = List({ tt: T => f(tt, u) }, { uu: U => f(t, uu) })

 def fromFunction2YieldingConversion
  [T, U, R1, R2](f: (T, U) => Conversion[R1, R2])(implicit t: T, u: U): List[_ => _] =
   f(t, u) :: fromFunction2(f)

 import java.util.ArrayList
 import java.lang.Boolean.{valueOf => toJavaBoolean}

 implicit val string = "foo"
 implicit val integer: Integer = 5
 implicit val twoWayConversion = StringConversion.integer
 implicit def list[T]: java.util.List[T] = java.util.Collections.emptyList[T]
 implicit def conversion[T, U] = new Conversion[T, U] { override def convert(t: T) = throw new UnsupportedOperationException }

 implicit def reduction[T, R] =
  new Reduction[T, R] { override def reduce(newValue: T, original: R) = throw new UnsupportedOperationException }

 implicit val anInt = 5
 implicit val intArray = Array(1, 2, 3)
 implicit val aParameterDescription = ParameterDescription.parameterWithoutDefault(string, twoWayConversion)
 implicit val unit = ()

 def notAcceptNull[T <: AnyRef, R] = new specs.matcher.Matcher[T => R] {
  def apply(f: => T => R) = try {
   f(null.asInstanceOf[T])
   (false, "should not be seen", "the method accepts null")
  } catch {
   case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen")
  }
 }

 def notAcceptNulls[T <: AnyRef, R] = new specs.matcher.Matcher[Conversion[T, R]] {
  def apply(conversion: => Conversion[T, R]) =
   try {
    conversion.convert(null.asInstanceOf[T])
    (false, "should not be seen", "the method accepts null")
   } catch {
    case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen")
   }
 }

 "CheckParameters.areNotNull" should { "not accept null" in { CheckParameters.areNotNull _ must notAcceptNull[Nothing, Unit] } }

 def noNull[T <: AnyRef, R](f: T => R, desc: String) =
  "desc" should { "not accept null" in { f must notAcceptNull[T, R] } }

 def noNull[T <: AnyRef, U <: AnyRef, R](f: (T, U) => R, desc: String)(implicit t: T, u: U): Unit = {
  noNull( { tt: T => f(tt, u) }, desc)
  noNull( { uu: U => f(t, uu) }, desc) }

 def noNull[T <: AnyRef, U <: AnyRef, V <: AnyRef, R]
 (f: (T, U, V) => R, desc: String)(implicit t: T, u: U, v: V): Unit = {
  noNull( { tt: T => f(tt, u, v) }, desc)
  noNull( { uu: U => f(t, uu, v) }, desc)
  noNull( { vv: V => f(t, u, vv) }, desc) }

 val conversions = Map[Conversion[_ <: AnyRef, _], String](
  Conversion.getStringToBooleanConversion -> "Conversion.getStringToBooleanConversion",
  Conversion.getStringToIntConversion -> "Conversion.getStringtoIntConversion",
  Conversion.getHexStringToIntConversion -> "Conversion.getHexStringToIntConversion",
  Conversion.getHexStringToLongConversion -> "Conversion.getHexStringToLongConversion",
  Conversion.getStringToLongConversion -> "Conversion.getStringToLongConversion",
  Conversion.getLongToHexStringConversion -> "Conversion.getLongToHexStringConversion",
  Conversion.getIntToHexStringConversion -> "Conversion.getIntToHexStringConversion",
  Conversion.equal("foo") -> "Conversion.equal(\"foo\")",
  Conversion.getIdentityConversion[Integer]() -> "Conversion.getIdentityConversion",
  Conversion.getObjectToStringConversion[Integer]() -> "Conversion.getObjectToStringConversion")

 for ((m, name) <- conversions) {
  noNull(conversionToFunction1(m), name)
  noNull[Integer, Integer](
   conversionToFunction1(new Conversion[Integer, Integer] { def convert(i: Integer) = i } andThen m),
   "Chained Conversions"
  )
 }                                  

 noNull(Conversion.fromBoolean[String] _, "Conversion.fromBoolean")
 noNull(Conversion.equal[String] _, "Conversion.equal")

 noNull(Lists.filter _, "Lists.filter")
 noNull(Lists.map _, "Lists.map")
 noNull(Lists.reduce[Integer] _, "Lists.reduce")

// noNull(Lists.remove[Unit] _, "Lists.remove") -- see line below
// noNull(Lists.removeIndices[Integer] _, "Lists.removeIndices") -- this line produces a compiler exception, need to try a later version of Scala.

 noNull(Lists.zip[Integer, String] _, "Lists.zip")
 
 noNull(Option.getEmptyOption[Integer] _, "Option.getEmptyOption")

 for (o <- List[Option[Integer]](Option.getEmptyOption[Integer]("because"), Option.getFullOption(5))) {
  noNull(o.fold[Integer] _, "Option.fold")
  noNull(o.map[Integer] _, "Option.map")
  noNull(o.bind[Integer] _, "Option.bind")
  "hack"
 }

 noNull(Option.getConversionToEmptyOption _, "Option.getConversionToEmptyOption")
 noNull(Option.getFullOption[Integer] _, "Option.getFullOption")
 noNull(Option.getConversionToFullOption[Integer](), "Option.getConversionToFullOption")
 
 noNull({ (x: Integer, y: Integer) => new Pair(x, y) }, "new Pair")

 noNull(ParameterDescription.parameterWithBounds(3, 5, (_: ParameterDescription[Integer, String])), "ParameterDescription.bound")
 noNull(ParameterDescription.parameterDisallowing[Integer, Option[Integer]] _, "ParameterDescription.not")
 noNull(ParameterDescription.nonNegativeParameter[Option[Integer]] _, "ParameterDescription.notNegative")
 noNull(ParameterDescription.parameterWithoutDefault[Integer] _, "ParameterDescription.parameter")

 noNull(ParameterDescription.parameterWithDefault[Integer] _, "ParameterDescription.parameterWithDefault")
 noNull(ParameterDescription.sparseArrayParameter[Integer] _, "ParameterDescription.sparseArrayParam")

 noNull({ x: Validator => new ParameterMap(x) }, "new ParameterMap(Validator)")
 noNull(Reduction.intersperseWith _, "Reduction.intersperseWith")
 noNull(Reduction.intersperseWith(":").reduce _, "Reduction.intersperseWith(\":\").reduce")
 noNull(Strings.surroundWithQuotes, "Strings.surroundWithQuotes")
 noNull({ s: String => Strings.fromFirst('c', s)}, "Strings.fromFirst('c', x)")
 noNull(Strings.intersperse _, "Strings.intersperse")
 noNull(Strings.splitCSV _, "Strings.splitCSV")
 noNull({ s: String => Strings.splitIgnoringQuotedSections(s, 'c') }, "Strings.splitIgnoringQuotedSections")
 noNull(Strings.afterFirstLeniently _, "Strings.afterFirstLeniently")
 noNull(Strings.afterLastLeniently _, "Strings.afterLastLeniently")
 noNull(Strings.beforeFirstLeniently _, "Strings.beforeFirstLeniently")
 noNull(Strings.partition('c').convert _, "Strings.partition('c').convert")
 noNull(Strings.removeSurroundingQuotesLeniently _, "Strings.removeSurroundingQuotesLeniently")

 def twoWay[T](twoWay: StringConversion[T], desc: String) = {
  noNull(twoWay.fromString _, desc+".conversionFromString")
  noNull({ t: T => twoWay.toString(t) }, desc+".conversionToString")
 }

 twoWay(StringConversion.integer, "StringConversion.integer")
 twoWay(StringConversion.string, "StringConversion.string")
 twoWay(StringConversion.bool, "StringConversion.bool")
 twoWay(StringConversion.hexLong, "StringConversion.hexLong")
 twoWay(StringConversion.hexInt, "StringConversion.hexInt")
 
 noNull(StringConversion.convenientPartial _, "StringConversion.convenientPartial")
 noNull(StringConversion.convenientTotal _, "StringConversion.convenientTotal")
 noNull(StringConversion.partial _, "StringConversion.partial")
 noNull(StringConversion.total _, "StringConversion.total")

 noNull({ x: String => new URLEncoder().convert(x) }, "URLEncoder")

 noNull(URLExtractor.nameValuePairs _, "URLExtractor.nameValuePairs")
 noNull(URLExtractor.parameters _, "URLExtractor.parameters")
 noNull(URLExtractor.queryName _, "URLExtractor.queryName")

 noNull(URLParameter.fromPair, "URLParameter.fromPair")
 noNull((x: String, y: String) => new URLParameter(x, y), "new URLParameter(x, y)")

 noNull(Validator.ACCEPT_ALL.isValid _, "Validator.ACCEPT_ALL.isValid")
 noNull(Validator.mutuallyExclusive _, "Validator.mutuallyExclusive")

 noNull(Validator.mutuallyExclusive(new java.util.ArrayList[ParameterDescription[_, _]]).isValid _,
        "Validator.mutuallyExclusive(new ArrayList<ParameterDescription<?, ?>>().isValid")
})
