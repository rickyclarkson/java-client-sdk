package uk.org.netvu.core.cgi.common;

/**
 * An object that can perform a replacement on a String, and undo that
 * replacement.
 */
public interface ReversibleReplace
{
    /**
     * @param s
     *        the String to operate on.
     * @return the String after replacement.
     */
    String replace( String s );

    /**
     * @param s
     *        the String to operate on.
     * @return the String after the undo replacement.
     */
    String undo( String s );
}
