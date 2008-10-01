package uk.org.netvu.core.cgi.common;

/**
 * An object that can perform a replacement on a String, and undo that
 * replacement. For internal use only.
 */
public interface ReversibleReplace
{
    /**
     * Performs a replacement on the given String, giving a String that
     * can be passed to undo to get the original String back.
     *
     * @param s
     *        the String to operate on.
     * @return the String after replacement.
     */
    String replace( String s );

    /**
     * Undoes the replacement performed by undo.
     *
     * @param s
     *        the String to operate on.
     * @return the String after the undo replacement.
     */
    String undo( String s );
}
