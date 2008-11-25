package uk.org.netvu.util

import _root_.org.{junit, specs}
import specs.runner.JUnit4
import specs.Specification
import Utilities.notAcceptNull

/**
 * Specification-based unit tests for the CheckParameters class.
 */
class CheckParametersTest extends JUnit4(new Specification {
    /**
     * Tests that CheckParameters.areNotNull does not accept null input values.
     */
  "CheckParameters.areNotNull" should {
    "not accept null" in { CheckParameters.areNotNull _ must notAcceptNull[Nothing, CheckParameters] }
  }
})
