package uk.org.netvu.core.cgi.common;

/**
 * An operation that takes a value of type T, a value of type R, and produces a
 * value of type R. It can be used in general algorithms to 'reduce' a list to
 * one value. For internal use only.
 * 
 * @param <T>
 *        the type of the new value to 'add' to the original.
 * @param <R>
 *        the type of the accumulator.
 */
public abstract class Reduction<T, R>
{
    /**
     * A Reduction that, given two Strings, constructs a new String containing
     * the two Strings with the given separator between them. Applied across a
     * List it can be used to comma-separate the List, etc.
     * 
     * @param separator
     *        the String to use to separate each pair of Strings received.
     * @return a Reduction that constructs a new String containing two Strings
     *         with the given separator between them.
     */
    public static final Reduction<String, String> intersperseWith(
            final String separator )
    {
        Checks.notNull( separator );

        return new Reduction<String, String>()
        {
            @Override
            public String reduce( final String newValue, final String original )
            {
                Checks.notNull( newValue, original );

                return original + separator + newValue;
            }
        };
    }

    /**
     * Reduces a T and an R to an R.
     * 
     * @param newValue
     *        the value of type T, the value to 'add' to the original.
     * @param original
     *        the value of type R, the accumulator.
     * @return a value of type R.
     */
    public abstract R reduce( T newValue, R original );
}
