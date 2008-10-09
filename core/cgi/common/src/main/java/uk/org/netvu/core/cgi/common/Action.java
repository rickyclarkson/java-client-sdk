package uk.org.netvu.core.cgi.common;

/**
 * An operation that can be done using a T. For internal use only.
 * 
 * @param <T>
 *        The type of object that this Action requires.
 */
interface ActionDELETE<T>
{
    /**
     * Performs an operation using the supplied parameter.
     *
     * @param t
     *        the object to act upon.
     */
    void invoke( T t );
}
