package uk.org.netvu.util

import _root_.org.junit.Test
import _root_.org.junit.Assert.{assertSame, assertTrue, assertFalse, fail}

/**
 * Unit tests for the OperatingSystem enum.
 */
class OperatingSystemTest {
    /**
     * Tests the getOperatingSystem() method against all the major operating system names that we
     * support, may have supported in the past, or may support in the future.
     */
    @Test
    def getOperatingSystem() {
        System.setProperty( "os.name", "Linux" )
        assertSame( "Linux should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Mac OS" )
        assertSame( "Mac OS (pre OS X) should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Mac OS X" )
        assertSame( "Mac OS X should be a recognized operating system",
           OperatingSystem.MacOSX, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Solaris" )
        assertSame( "Solaris should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "SunOS" )
        assertSame( "Solaris should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows 2000" )
        assertSame( "Windows 2000 should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows 95" )
        assertSame( "Windows 95 should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows 98" )
        assertSame( "Windows 98 should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows CE" )
        assertSame( "Windows CE should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows Me" )
        assertSame( "Windows Me should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "WindowsNT" )
        assertSame( "Windows NT should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows 2003" )
        assertSame( "Windows Server 2003 should not be a recognized operating system",
           OperatingSystem.Unknown, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows Vista" )
        assertSame( "Windows Vista should be a recognized operating system",
            OperatingSystem.WindowsVista, OperatingSystem.getOperatingSystem())
        System.setProperty( "os.name", "Windows XP" )
        assertSame( "Windows XP should be a recognized operating system",
            OperatingSystem.WindowsXP, OperatingSystem.getOperatingSystem())
    }
    
    /**
     * Checks that each enum value returns the correct boolean result for isWindows(). This test 
     * will also fail if a new enum value is added and not accounted for here.
     */
    @Test
    def isWindows() {
        OperatingSystem.values().foreach { os =>
            os match {
                case OperatingSystem.WindowsXP => assertTrue("Windows XP is a Windows OS", 
                    OperatingSystem.WindowsXP.isWindows())
                case OperatingSystem.WindowsVista => assertTrue("Windows Vista is a Windows OS", 
                    OperatingSystem.WindowsVista.isWindows())
                case OperatingSystem.MacOSX => assertFalse("Mac OS X is not a Windows OS", 
                    OperatingSystem.MacOSX.isWindows())
                case OperatingSystem.Unknown => assertFalse("Unknown is not a Windows OS", 
                    OperatingSystem.Unknown.isWindows()) 
                case _ => fail("Not catered for " + os + " in isWindows() test!")
            }
        }
    }
}
