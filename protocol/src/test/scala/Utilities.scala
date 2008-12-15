package uk.org.netvu.protocol

import _root_.org.specs
import specs.matcher.Matcher
import specs.Specification

object Utilities {
  def notAcceptNull[T <: AnyRef, R] = new Matcher[T => R] {
    def apply(f: => T => R) = try {
      f(null.asInstanceOf[T])
      (false, "should not be seen", "the method accepts null")
    } catch {
      case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen")
    }
  }

  def notAcceptNulls[T <: AnyRef, R] = new Matcher[Function[T, R]] {
    def apply(conversion: => Function[T, R]) =
      try {
        conversion(null.asInstanceOf[T])
        (false, "should not be seen", "the method accepts null")
      } catch {
        case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen")
      }
  }
}

object BuildersTests {
 def testBuilder[I, B <: { def build(): I }](builder: => B, completeBuilder: => B, setters: List[B => B], theName: String) =
  new Specification {
   "Setting a value twice" should {
    ("cause an IllegalStateException: " + theName) in {
     setters foreach { setter => setter(setter(builder)) must throwA[IllegalStateException] }
    }
   }
   "Setting a value after calling build()" should {
    "cause an IllegalStateException" in {
     setters foreach {
      setter => {
       val b = completeBuilder
       b.build
       setter(b) must throwA[IllegalStateException]
      }
     }
    }
   }
  }
}
