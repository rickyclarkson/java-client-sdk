package uk.org.netvu.util;

import java.util.Locale;

/**
 * Enumeration of supported operating systems.
 */
public enum OperatingSystem
{
    /**
     * The operating system is not recognized or supported by the Java Client SDK.
     */
    Unknown,
    /**
     * Windows Vista.
     */
    WindowsVista
    {
        /**
         * @inheritDoc
         */
        @Override
        public boolean isWindows()
        {
            return true;
        }
    },
    /**
     * Windows XP.
     */
    WindowsXP
    {
        /**
         * @inheritDoc
         */
        @Override
        public boolean isWindows()
        {
            return true;
        }
    },
    /**
     * Mac OS X.
     */
    MacOSX;
    
    /**
     * Returns the current {@code OperatingSystem} or {@code Unknown} if the operating system is not 
     * recognized or supported.
     *
     * @return the current {@code OperatingSystem} or {@code Unknown} if the operating system is not 
     * recognized or supported
     */
    public static OperatingSystem getOperatingSystem()
    {
        final String os = System.getProperty( "os.name" ).toLowerCase( Locale.ENGLISH );
        
        if ( os.startsWith( "windows xp" ) )
        {
            return WindowsXP;
        }
        else if ( os.startsWith( "windows vista" ) )
        {
            return WindowsVista;
        }
        else if ( os.startsWith( "mac os x" ) )
        {
            return MacOSX;
        }
        else
        {
            return Unknown;
        }
    }
    
    /**
     * Returns {@code true} if the operating system is in the Windows family of operating systems. 
     * Returns {@code false} otherwise.
     *
     * @return {@code true} if the operating system is in the Windows family of operating systems, 
     * {@code false} otherwise
     */    
    public boolean isWindows()
    {
        return false;
    }
}