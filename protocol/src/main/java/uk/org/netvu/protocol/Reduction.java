package uk.org.netvu.protocol;

import uk.org.netvu.util.CheckParameters;

/**
 * A transformation from 2 values to 1 value, that can be used across many
 * values to 'reduce' n values to 1. For example, a Reduction that takes two
 * Integers and returns their sum can be used to sum a collection of Integers.
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
    public static final Reduction<String, String> intersperseWith( final String separator )
    {
        CheckParameters.areNotNull( separator );

        return new Reduction<String, String>()
        {
            @Override
            public String reduce( final String newValue, final String original )
            {
                CheckParameters.areNotNull( newValue, original );

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
