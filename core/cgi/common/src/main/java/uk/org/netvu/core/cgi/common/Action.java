package uk.org.netvu.core.cgi.common;

/**
 * An operation that can be done on a T. For internal use only.
 * 
 * @param <T>
 *        The type of object that this Action requires.
 */
public interface Action<T>
{
    /**
     * @param t
     *        the object to act upon.
     */
    void invoke( T t );
}
