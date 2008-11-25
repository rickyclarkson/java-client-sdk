package uk.org.netvu.util;

/**
 * A utility class for checking constraints about method parameters.
 */
public final class CheckParameters
{
    /**
     * Checks that all the arguments passed are non-null. Fails on the first
     * null argument it encounters.
     * 
     * @param args
     *        the arguments to check.
     * @return a CheckParameters instance to call further checks on.
     * @throws NullPointerException
     *         if any of the arguments passed are null, or if the varargs array
     *         itself is null.
     */
    public static CheckParameters areNotNull( final Object... args )
    {
        int a = 0;
        for ( final Object o : args )
        {
            if ( o == null )
            {
                throw new NullPointerException( "Argument number " + a + " was null." );
            }
            a++;
        }

        return new CheckParameters();
    }

    /**
     * This is private to prevent uncontrolled instantiation.
     */
    private CheckParameters()
    {
    }

    /**
     * Checks that all the arguments passed are not negative. Fails on the first
     * negative argument it encounters.
     * 
     * @param args
     *        the arguments to check.
     * @return a CheckParameters instance to call further checks on.
     * @throws NullPointerException
     *         if the args varargs array is null.
     * @throws IllegalArgumentException
     *         if any of the arguments passed are negative.
     */
    public CheckParameters areNotNegative( final int... args )
    {
        CheckParameters.areNotNull( args );
        int a = 0;
        for ( final int i : args )
        {
            if ( i < 0 )
            {
                throw new IllegalArgumentException( "Argument number " + a + " (" + i + ") was negative" );
            }
            a++;
        }

        return this;
    }
}
