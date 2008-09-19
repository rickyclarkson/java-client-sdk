package uk.org.netvu.core.cgi.events

import _root_.org.scalacheck.Gen
import _root_.org.junit.{Test, Assert}
import Assert.assertTrue

class AlarmTypeTest {
 import EventsCGIResult.AlarmType.find

 val valid = List(0, 1, 2, 4, 8, 16, 32, 64)
 @Test def validValues = assertTrue(valid forall (x => find(x).get.value == x))
 @Test def mixedValues = assertTrue((1 to 64).toList -- valid forall (x => find(x).isNone))
 @Test def otherValues = { import Integer.{MAX_VALUE => max, MIN_VALUE => min}
                           assertTrue(List(-1, max, max - 1, min, min + 1) ++ (65 to 512) forall (x => find(x).isNone)) } }

import _root_.org.specs.runner.JUnit4
import _root_.org.specs.{Specification, Scalacheck}

class SecondTest extends JUnit4(new Specification with Scalacheck {
 import EventsCGIResult.AlarmType
 import Integer.{MIN_VALUE => MIN_INT, MAX_VALUE => MAX_INT}

 val values = List(0, 1, 2, 4, 8, 16, 32, 64)
 "A value in the set "+values.mkString should {
  "be accepted by EventsCGIResult.AlarmType.find." in {
   Gen.elements(values: _*) must pass { x: Int => { val alarmType = AlarmType.find(x)
                                                    !alarmType.isNone && alarmType.get.value == x } } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   Gen.choose(3, 63) suchThat { x: Int => !values.contains(x) } must pass { x: Int => AlarmType.find(x).isNone } }
  "be rejected by EventsCGIResult.AlarmType.find." in {
   Gen.choose(MIN_INT.toLong, MAX_INT.toLong).map(_.toInt) suchThat (x => x<0 || x>64) must pass {
    x: Int => AlarmType.find(x).isNone } } } } )
