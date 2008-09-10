package uk.org.netvu.core.cgi.common;

/**
 * An infinite data generator.
 * 
 * @param <T>
 *        the type of the data.
 */
public interface Generator<T>
{
    /**
     * @return the next item from this Generator.
     */
    T next();
}
