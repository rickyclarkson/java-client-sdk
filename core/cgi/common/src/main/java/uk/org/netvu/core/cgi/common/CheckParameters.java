package uk.org.netvu.core.cgi.common;

/**
 * For internal use only! A utility class for checking constraints about method
 * parameters.
 */
public final class CheckParameters
{
    /**
     * Checks that all the arguments passed are non-null.
     * Fails on the first null argument it encounters.
     * 
     * @param args
     *        the arguments to check.
     * @throws NullPointerException
     *         if any of the arguments passed are null, or if the varargs array itself is null.
     */
    public static void areNotNull( final Object... args )
            throws NullPointerException
    {
        int a = 0;
        for ( final Object o : args )
        {
            if ( o == null )
            {
                throw new NullPointerException( "Argument number " + a
                        + " was null." );
            }
            a++;
        }
    }

    /**
     * This is private to prevent instantiation.
     */
    private CheckParameters()
    {
    }
}
