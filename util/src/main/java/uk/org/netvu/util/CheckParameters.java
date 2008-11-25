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
     * To bounds-check integers, use CheckParameters.from(lower).to(upper).bounds(the, integers, here);
     * @param lower the lower bound.
     * @return a From to call .to on.
     */
    public static From from( final int lower )
    {
        return new From( lower );
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

    /**
     * The second step in a fluent interface for bounds-checking integers.
     * @see CheckParameters#from(int)
     */
    public static final class From
    {
        /**
         * The lower bound.
         */
        private final int lower;

        /**
         * Constructs a From with the specified lower bound.
         * @param lower the lower bound.
         */
        From( final int lower )
        {
            this.lower = lower;
        }

        /**
         * Constructs a FromTo ready to bounds-check integers.
         * @param upper the upper bound.
         */
        public FromTo to( final int upper )
        {
            return new FromTo( lower, upper );
        }
    }

    /**
     * The final step in a fluent interface for bounds-checking integers.
     * @see CheckParameters#from(int)
     */
    public static final class FromTo
    {
        /**
         * The lower bound.
         */
        private final int lower;

        /**
         * The upper bound.
         */
        private final int upper;

        /**
         * Constructs a FromTo with the specified lower and upper bounds.
         */
        public FromTo( final int lower, final int upper )
        {
            this.lower = lower;
            this.upper = upper;
        }

        /**
         * Asserts that this FromTo bounds all the specified ints.
         * @param ints the ints to check.
         * @throws IllegalArgumentException if any of the ints are outside the valid range.
         * @throws NullPointerException if ints is null.
         */
        public void bounds( final int... ints )
        {
            areNotNull(ints);

            for ( final int i : ints )
            {
                if ( i < lower || i > upper )
                {
                    throw new IllegalArgumentException( i + " is outside the valid range (" + lower + " to " + upper
                            + ")" );
                }
            }
        }
    }
}
