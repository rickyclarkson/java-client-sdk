package uk.org.netvu.core.cgi.common;

/**
 * A utility class for checking constraints about method parameters.
 */
public final class Checks
{
    private Checks()
    {
    }

    /**
     * Checks that all the arguments passed are non-null.
     * 
     * @param args
     *        the arguments to check.
     * @throws NullPointerException
     *         if any of the arguments passed are null.
     */
    public static void notNull( final Object... args )
            throws NullPointerException
    {
        int a = 0;
        for ( final Object o : args )
        {
            if ( o == null )
            {
                throw new NullPointerException( "Value number " + a
                        + " was null." );
            }
            a++;
        }
    }
}
