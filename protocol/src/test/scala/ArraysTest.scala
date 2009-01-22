package uk.org.netvu.protocol

import _root_.org.specs
import specs.{Specification, ScalaCheck}
import specs.runner.JUnit4

class ArraysTest extends JUnit4(new Specification {
 import Arrays._
 contains(Array("foo", "bar", "baz"), "foo") mustEqual true
 contains(Array("foo", "bar", "baz"), "spam") mustEqual false
 contains(Array("foo"), new String("foo")) mustEqual true
})
