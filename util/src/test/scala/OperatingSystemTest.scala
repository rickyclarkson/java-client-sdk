package uk.org.netvu.util

import _root_.org.junit.Test
import _root_.org.junit.Assert.{assertSame, assertTrue, assertFalse, fail}

import _root_.org.specs._
import _root_.org.specs.runner.JUnit4
import _root_.org.specs.util.DataTables

/**
 * Specification based unit tests for the OperatingSystem enum.
 */
class OperatingSystemSpecsTest extends JUnit4(new Specification with DataTables {
    /**
     * Tests the getOperatingSystem() method against all the major operating system names that we
     * support, may have supported in the past, or may support in the future.
     */
  "'getOperatingSystem()' must return the correct enum value" in {
    "os.name"       | "OperatingSystem"            |>
    "Linux"         ! OperatingSystem.Unknown      |
    "Mac OS"        ! OperatingSystem.Unknown      |
    "Mac OS X"      ! OperatingSystem.MacOSX       |
    "Solaris"       ! OperatingSystem.Unknown      |
    "SunOS"         ! OperatingSystem.Unknown      |
    "Windows 2000"  ! OperatingSystem.Windows200X  |
    "Windows 2003"  ! OperatingSystem.Windows200X  |
    "Windows 95"    ! OperatingSystem.Unknown      |
    "Windows 98"    ! OperatingSystem.Unknown      |
    "Windows CE"    ! OperatingSystem.Unknown      |
    "Windows Me"    ! OperatingSystem.Unknown      |
    "WindowsNT"     ! OperatingSystem.Unknown      |
    "Windows Vista" ! OperatingSystem.WindowsVista |
    "Windows XP"    ! OperatingSystem.WindowsXP    | { (osName, os) =>
      System.setProperty("os.name", osName)
      OperatingSystem.getOperatingSystem() mustBe os
    }
  }
})
