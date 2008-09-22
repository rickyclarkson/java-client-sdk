package uk.org.netvu.core.cgi.events

import _root_.org.scalacheck.{Gen, Prop}
import _root_.org.scalacheck._
import Prop.{property, extendedBoolean}
import Prop._
import Gen.{parameterized, value}
import _root_.org.junit.{Test, Assert}
import Assert.assertTrue

class AlarmTypeTest {
 import EventsCGIResult.AlarmType.find

 val valid = List(0, 1, 2, 4, 8, 16, 32, 64)
 @Test def validValues = assertTrue(valid forall (x => find(x).value == x))

 @Test def mixedValues =
  assertTrue((1 to 64).toList -- valid forall (x => try { find(x)
                                                          false } catch {
                                                           case e: IllegalArgumentException => true
                                                           case _ => false } ))

 @Test def otherValues = {
  import Integer.{MAX_VALUE => max, MIN_VALUE => min}
  assertTrue(List(-1, max, max - 1, min, min + 1) ++ (65 to 512) forall (
   x => try { find(x)
              false } catch { case e: IllegalArgumentException => true
                              case _ => false } )) } }

import _root_.org.specs.runner.JUnit4
import _root_.org.specs.runner._
import _root_.org.specs.{Specification, Scalacheck}
import _root_.org.specs._
import _root_.org.specs.specification._
import Sugar._

class SecondTest extends JUnit4(new Specification with Scalacheck {
 import EventsCGIResult.AlarmType
 import Integer.{MIN_VALUE => MIN_INT, MAX_VALUE => MAX_INT}

 val values = List(0, 1, 2, 4, 8, 16, 32, 64)
 "A value in the set "+values.mkString should {
  "be accepted by EventsCGIResult.AlarmType.find." in {
   Gen.elements(values: _*) must pass { x: Int => AlarmType.find(x).value == x } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   Gen.choose(3, 63) suchThat { x: Int => !values.contains(x) } must pass {
    x: Int => try { AlarmType.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   Gen.choose(MIN_INT.toLong, MAX_INT.toLong).map(_.toInt) suchThat (x => x<0 || x>64) must pass {
    x: Int => try { AlarmType.find(x)
                    false } catch { case e: IllegalArgumentException => true
                                    case _ => false } } } } } )

import cgi.common.Format
class EventsCGISecondTest extends JUnit4(EventsCGISecondTestObject)
object EventsCGISecondTestObject extends Specification with Scalacheck {
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

 implicit val arbLong: Arbitrary[Long] = Arbitrary(parameterized(params => {
  def aInt = params.rand.choose(1, Integer.MAX_VALUE) - params.rand.choose(0, 1) * Integer.MAX_VALUE
  value(aInt.toLong << 32 + aInt) } ) ) 

 "The built EventsCGI" should {
  "have the values supplied to the Builder" in {
   property((a: Int) => new Builder().alarmMask(a).build.getAlarmMask == a) must pass
   property((a: Int) => a >= 0 ==> (new Builder().range(a).build.getRange == a)) must pass
   property((a: Long) => new Builder().cameraMask(a).build.getCameraMask == a) must pass
   property((a: Int) => new Builder().gpsMask(a).build.getGpsMask == a) must pass
   property((a: Int) => new Builder().length(a).build.getMaxLength == a) must pass
   property((a: Int) => new Builder().systemMask(a).build.getSystemMask == a) must pass
   property((a: String) => new Builder().text(a).build.getText == a) must pass
   property((a: Int) => a >= 0 ==> (new Builder().time(a).build.getTime == a)) must pass
   property((a: Long) => new Builder().videoMotionDetectionMask(a).build.getVideoMotionDetectionMask == a) must pass } }

 "Null values" should {
  "be rejected" in {
   new Builder().format(null) must throwA(new NullPointerException)
   new Builder().text(null) must throwA(new NullPointerException)
   EventsCGI.fromString(null) must throwA(new NullPointerException) } } }
