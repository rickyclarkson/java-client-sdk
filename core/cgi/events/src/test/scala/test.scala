package uk.org.netvu.core.cgi.events

import _root_.org.{scalacheck, specs, junit}
import junit.Test
import junit.Assert.assertTrue
import java.util.Random
import specs.runner.JUnit4
import specs.{Specification, Scalacheck}

class AlarmTypeTest extends JUnit4(new Specification with Scalacheck {
 import EventsCGIResult.AlarmType
 import Integer.{MIN_VALUE => MIN_INT, MAX_VALUE => MAX_INT}

 val values = List(0, 1, 2, 4, 8, 16, 32, 64)
 "A value in the set "+values.mkString should {
  "be accepted by EventsCGIResult.AlarmType.find." in {
   scalacheck.Gen.elements(values: _*) must pass { x: Int => AlarmType.find(x).value == x } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   scalacheck.Gen.choose(3, 63) suchThat { x: Int => !values.contains(x) } must pass {
    x: Int => try { AlarmType.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   scalacheck.Gen.choose(MIN_INT.toLong, MAX_INT.toLong).map(_.toInt) suchThat (x => x<0 || x>64) must pass {
    x: Int => try { AlarmType.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } } } } )

class StatusTest extends JUnit4(new Specification with Scalacheck {
  import EventsCGIResult.Status
 import Integer.{MIN_VALUE => MIN_INT, MAX_VALUE => MAX_INT}

 val values = List(0, 1, 2, 4, 8)
 "A value in the set "+values.mkString should {
  "be accepted by EventsCGIResult.Status.find." in {
   scalacheck.Gen.elements(values: _*) must pass { x: Int => Status.find(x).value == x } }
  "be rejected by EventsCGIResult.Status.find." in {
   scalacheck.Gen.choose(3, 7) suchThat { x: Int => !values.contains(x) } must pass {
    x: Int => try { Status.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } }
  "be rejected by EventsCGIResult.Status.find." in {
   scalacheck.Gen.choose(MIN_INT.toLong, MAX_INT.toLong).map(_.toInt) suchThat (x => x<0 || x>8) must pass {
    x: Int => try { Status.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } } } } )

import cgi.common.Format
import scalacheck.Arbitrary
import Arbitrary.arbitrary
import scalacheck.Prop.{property, extendedBoolean}
import scalacheck.Gen

class EventsCGITest extends JUnit4(new Specification with Scalacheck {
 import EventsCGI.Builder

 "The default values for EventsCGI.Builder" should {
  "be the same as in the specification." in {
   val events = new Builder().build
   events.getFormat mustEqual Format.CSV
   events.getTime mustEqual 0
   events.getRange mustEqual Integer.MAX_VALUE
   events.getMaxLength mustEqual 100
   events.getText mustEqual ""
   events.getCameraMask mustEqual 0
   events.getAlarmMask mustEqual 0
   events.getVideoMotionDetectionMask mustEqual 0
   events.getGpsMask mustEqual 0
   events.getSystemMask mustEqual 0 } }

 "Applying the time parameter more than once" should {
  "cause an IllegalStateException" in {
   new Builder() time 100 time 200 must throwA(new IllegalStateException) } }

 "Negative times" should {
  "cause an IllegalArgumentException" in {
   new Builder() time -100 must throwA(new IllegalArgumentException) } }

 "A zero time" should {
  "be accepted" in {
   new Builder().time(0).build.getTime mustEqual 0 } }

 "Supplying the text and gpsMask parameters together" should {
  "cause an IllegalStateException, as they are mutually exclusive." in {
   new Builder() text "foo" gpsMask 40 must throwA(new IllegalStateException) } }

 "Supplying any two of the alarm mask, vmd mask, gps mask and sys mask parameters" should {
  "cause an IllegalStateException, as they are mutually exclusive." in {
   val parameters = List[Builder => Unit](b => b alarmMask 4,
                                          b => b videoMotionDetectionMask 8,
                                          b => b gpsMask 32,
                                          b => b systemMask 23)
   for (first <- parameters; second <- parameters; if first != second) {
    val builder = new Builder
    first(builder)
    second(builder) must throwA(new IllegalStateException) } } }

 implicit val arbLong: scalacheck.Arbitrary[Long] = scalacheck.Arbitrary(scalacheck.Gen.parameterized(params => {
  def aInt = params.rand.choose(1, Integer.MAX_VALUE) - params.rand.choose(0, 1) * Integer.MAX_VALUE
  scalacheck.Gen.value(aInt.toLong << 32 + aInt) } ) )

 "The built EventsCGI" should {
  "have the values supplied to the Builder" in {
   property((a: Int) => new Builder().alarmMask(a).build.getAlarmMask == a) must pass
   property((a: Int) => a >= 0 ==> (new Builder().range(a).build.getRange == a)) must pass
   property((a: Long) => new Builder().cameraMask(a).build.getCameraMask == a) must pass
   property((a: Int) => new Builder().gpsMask(a).build.getGpsMask == a) must pass
   property((a: Int) => new Builder().maxLength(a).build.getMaxLength == a) must pass
   property((a: Int) => new Builder().systemMask(a).build.getSystemMask == a) must pass
   property((a: String) => new Builder().text(a).build.getText == a) must pass
   property((a: Int) => a >= 0 ==> (new Builder().time(a).build.getTime == a)) must pass
   property((a: Long) => new Builder().videoMotionDetectionMask(a).build.getVideoMotionDetectionMask == a) must pass } }

 "Null values" should {
  "be rejected" in {
   new Builder().format(null) must throwA(new NullPointerException)
   new Builder().text(null) must throwA(new NullPointerException)
   EventsCGI.fromCSV(null) must throwA(new NullPointerException) } }

 "Malformed URL parameters (invalid status)" should {
  "be rejected" in {
   val string = "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 3, 0, 3, 4"
   EventsCGIResult.fromString(string) must throwA(new IllegalArgumentException) } }

 "Malformed URL parameters (invalid alarm type)" should {
  "be rejected" in {
   val string = "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 3, 0, 2, 5"
   EventsCGIResult.fromString(string) must throwA(new IllegalArgumentException) } }

 "EventsCGIResult.toString()" should {
  "not be supported" in {
   val string = "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 3, 0, 2, 4"
   EventsCGIResult.fromString(string).toString must throwA(new UnsupportedOperationException) } }

 implicit val arbFormat: Arbitrary[Format] =
  Arbitrary { arbitrary[Int] map (x => Format.oneOf(new Random(x))) }

 implicit val arbString: Arbitrary[String] = Arbitrary { Gen.identifier }

 private def randomEventsCGIBuilder = {
  val builder = new EventsCGI.Builder
  def nonNegativeInt = arbitrary[Int] map (_.abs) sample
  def anyInt = arbitrary[Int] sample
  def anyLong = arbitrary[Long] sample
  def anyFormat = arbitrary[Format] sample
  def anyString = arbitrary[String] sample
  
  val methods = List(() => builder.alarmMask(anyInt.get),
                     () => builder.cameraMask(anyLong.get),
                     () => builder.format(anyFormat.get),
                     () => builder.gpsMask(anyInt.get),
                     () => builder.maxLength(anyInt.get),
                     () => builder.range(nonNegativeInt.get),
                     () => builder.systemMask(anyInt.get),
                     () => builder.text(anyString.get),
                     () => builder.time(nonNegativeInt.get),
                     () => builder.videoMotionDetectionMask(anyLong.get)
                    ).map(a => () => try { a() } catch { case e: RuntimeException => })

  for (i <- 0 to arbitrary[Int].sample.get.abs % 200)
   methods(Gen.choose(0, methods.size-1).sample.get)()

  builder
 }

 implicit val eventsBuilderGen: Arbitrary[EventsCGI.Builder] =
  Arbitrary { for (i <- arbitrary[Int]) yield randomEventsCGIBuilder }

 implicit val events: Arbitrary[EventsCGI] = Arbitrary { arbitrary[EventsCGI.Builder] map { b => b.build } }

 "Converting an EventsCGI to a String and back" should { "not be lossy" in {
//  val cgi = randomEventsCGIBuilder.build 
//  EventsCGI.fromString(cgi.toString).toString mustEqual cgi.toString
  property { e: EventsCGI => {
   EventsCGI.fromCSV(e.toString).toString == e.toString
  } } must pass
 } }

 "Parsing an invalid URL parameter" should { "cause an IllegalArgumentException" in {
  EventsCGI.fromCSV("?almmask=six") must throwA(new IllegalArgumentException)
 } }

 "Parsing an empty String" should { "cause an IllegalArgumentException" in {
  EventsCGI.fromCSV("") must throwA(new IllegalArgumentException)
 } }

 "Parsing real data stored from a server then generating a String from that" should { "give the same CSV as the original" in {
  val data = List("1, 0, System Startup, 1122030592, 3600, ,overwitten, 1, 10, 2, 0, 4, 8",
                  "2, 0, RTC reset (CBUS), 1122034260, 3600, (11:10:34 22/Jul/2005),overwitten, 2, 10, 2, 0, 4, 8",
                  "3, 0, RTC reset (CBUS), 1122035099, 3600, (13:25:52 22/Jul/2005),overwitten, 3, 10, 2, 0, 4, 8",
                  "4, 1, Camera fail, 1122035906, 3600, ,overwitten, 4, 1, 0, 0, 4, 8",
                  "5, 1, Camera Restored, 1122035915, 3600, ,overwitten, 5, 1, 0, 0, 4, 8",
                  "6, 0, System Startup, 1122035962, 3600, ,overwitten, 6, 10, 2, 0, 4, 8",
                  "7, 0, System Startup, 1122037538, 3600, ,overwitten, 7, 10, 2, 0, 4, 8",
                  "8, 0, System Startup, 1122037625, 3600, ,overwitten, 8, 10, 2, 0, 4, 8",
                  "9, 0, System Startup, 1122038376, 3600, ,overwitten, 9, 10, 2, 0, 4, 8",
                  "10, 0, RTC reset (CBUS), 1122463200, 3600, (11:19:58 27/Jul/2005),overwitten, 10, 10, 2, 0, 4, 8",
                  "11, 0, System Startup, 1122907601, 3600, ,overwitten, 11, 10, 2, 0, 4, 8",
                  "12, 0, RTC reset (CBUS), 1122907620, 3600, (14:46:57 01/Aug/2005),overwitten, 12, 10, 2, 0, 4, 8")

  def compare(input: String, output: EventsCGIResult) = {
   import common.Strings
   val fields = Strings.splitCSV(input)
   val others = Strings.splitCSV(output.toCSV(0))

   fields.zip(others).zipWithIndex forall { case ((f, o), i) => i == 0 || i == 7 || f == o }
  }
   
  for (line <- data) compare(line, EventsCGIResult.fromString(line)) mustEqual true
 } }

 "toString" should {
  "give a valid URL" in /*{ new java.net.URL("http://none" + randomEventsCGIBuilder.build.toString)!=null mustEqual true }*/{
   property { e: EventsCGI.Builder => new java.net.URL("http://none" + e.build.toString) != null } must pass }
  "not give a URL containing spaces" in { new EventsCGI.Builder().text("hello world").build.toString must notInclude(" ") }
 }
})
                                  
class EventsCGIResultTest extends JUnit4(new Specification with Scalacheck { 
 import EventsCGIResult.{Status, AlarmType, Builder}

 def randomEventsCGIResultBuilder(random: Random, alarm: String, file: String) = {
  def nonNegative = random.nextInt.abs
  val julianTime = nonNegative
  val setAlarmType = random.nextBoolean
  val setStatus = random.nextBoolean

  var b = new EventsCGIResult.Builder() alarm alarm archive nonNegative camera random.nextInt(65)
  b = b duration Math.max(0, nonNegative - julianTime.intValue) file file
  b = b julianTime julianTime.intValue offset random.nextInt(180000)-90000 preAlarm nonNegative
  b = b onDisk random.nextBoolean

  if (setStatus) b = b status oneStatus(random)
  if (setAlarmType) b = b alarmType oneAlarm(random)

  b
 }

 private def oneAlarm(random: Random) = AlarmType.values()(random.nextInt(AlarmType.values.length))
 private def oneStatus(random: Random) = Status.values()(random.nextInt(Status.values.length))

 implicit val eventsCGIResultBuilders: Arbitrary[EventsCGIResult.Builder] =
  Arbitrary { for { i <- arbitrary[Int]
                    alarm <- arbitrary[String]
                    file <- arbitrary[String] }
               yield randomEventsCGIResultBuilder(new Random(i), alarm, file) }

 implicit val eventsCGIResults: Arbitrary[EventsCGIResult] =
  Arbitrary { for (b <- arbitrary[EventsCGIResult.Builder]) yield b.build }

 "parsing an empty String" should { "cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("") must throwA(new IllegalArgumentException)
 } }

 "EventsCGIResults" should { "have a non-negative (time + duration)" in {
  property { e: EventsCGIResult => e.getJulianTime + e.getDuration >= 0 } must pass
 } }

 "A camera number that's too high" should { "cause an IllegalArgumentException" in {
  property { e: EventsCGIResult.Builder => try { e.camera(65).build
                                                 false } catch { case e: IllegalArgumentException => true }
  }
 } }

 "An incomplete EventsCGIResult" should { "not be buildable" in {
  new EventsCGIResult.Builder().build must throwA(new IllegalStateException)
 } }

 import scalacheck.Prop.exception

 "Offsets lower than -90000" should { "yield an IllegalArgumentException" in {
  property { e: EventsCGIResult.Builder => try { e.offset(-90001).build
                                                 false
                                               } catch { case e: IllegalArgumentException => true }
           } must pass
 } }

 "Offsets greater than 90000" should { "yield an IllegalArgumentException" in {
  property { e: EventsCGIResult.Builder => try { e.offset(90001).build
                                                 false
                                               } catch { case e: IllegalArgumentException => true }
           } must pass
 } }

 import java.lang.{IllegalArgumentException => IAE}
 "Parsing a line of CSV with a negative timestamp" should { "cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("1, 1, Y, -111488075, 3600, ,overwitten, 1, 10, 2, 0") must throwA(new IAE)
 } }

 "Parsing a line of CSV with not enough columns" should { "cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2") must throwA(new IAE)
 } }

 "Parsing a line of CSV with a malformed number" should { "cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("1, b, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0") must throwA(new IAE)
 } }

 "Parsing malformed CSV" should {"cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("3, 4, 5") must throwA(new IAE)
 } }

 "Values stored in an EventsCGIResult" should { "be retrievable" in {
  val result = (new EventsCGIResult.Builder() camera 1 alarm "test" julianTime 100 offset 5 file "ignore" onDisk true duration 40 preAlarm 1 archive 1 status EventsCGIResult.Status.NONE alarmType EventsCGIResult.AlarmType.CAMERA).build

  result.getCam mustEqual 1
  result.getAlarm mustEqual "test"
  result.getJulianTime mustEqual 100
  result.getOffset mustEqual 5
  result.getFile mustEqual "ignore"
  result.isOnDisk mustEqual true
  result.getDuration mustEqual 40
  result.getPreAlarm mustEqual 1
  result.getArchive mustEqual 1
  result.getStatus mustEqual EventsCGIResult.Status.NONE
  result.getAlarmType mustEqual EventsCGIResult.AlarmType.CAMERA
 } }

 "toCSV" should { "give a non-empty String" in {
  property { e: EventsCGIResult => e.toCSV(0).length>0 } must pass
 } }

 "Parsing a line of CSV with too many columns" should { "cause an IllegalArgumentException" in {
  EventsCGIResult.fromString("1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14") must throwA(new IAE)
 } }

 "Parsing a valid line of CSV" should { "succeed" in {
  EventsCGIResult.fromString("1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0")==null mustEqual false
  EventsCGIResult.fromString("1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0, 4, 8")==null mustEqual false
 } }
})
