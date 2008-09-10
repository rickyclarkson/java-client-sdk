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
