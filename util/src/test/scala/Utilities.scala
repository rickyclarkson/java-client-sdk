package uk.org.netvu.util

import _root_.org.specs.matcher.Matcher

object Utilities {
  def notAcceptNull[T <: AnyRef, R] = new Matcher[T => R] {
    def apply(f: => T => R) = try {
      f(null.asInstanceOf[T])
      (false, "should not be seen", "the method accepts null")
    } catch {
      case e: NullPointerException => (true, "the method doesn't accept null", "should not be seen")
    }
  }
}
