package uk.org.netvu.core.cgi.variable;

/**
 * Specifies whether a system variable is an array or a scalar value.
 */
public enum ArrayOrScalar
{
    /**
     * Represents that the system variable is an array (0 or more values).
     */
    ARRAY,

    /**
     * Represents that the system variable is a scalar (1 value).
     */
    SCALAR
}
