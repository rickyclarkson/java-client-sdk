package uk.org.netvu.core.cgi.common;

/**
 * An infinite data generator. For internal use only.
 * 
 * @param <T>
 *        the type of the data.
 */
public interface Generator<T>
{
    /**
     * Gives the next item from this Generator.
     * 
     * @return the next item from this Generator.
     */
    T next();
}
