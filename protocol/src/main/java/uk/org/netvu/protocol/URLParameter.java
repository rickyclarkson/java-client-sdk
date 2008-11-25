package uk.org.netvu.protocol;

import uk.org.netvu.util.CheckParameters;

/**
 * A representation of a URL Parameter (foo=bar).
 */
final class URLParameter
{
    /**
     * The name of this parameter.
     */
    public final String name;

    /**
     * The value of this parameter.
     */
    public final String value;

    /**
     * A Function that, given a Pair of Strings, produces a URLParameter where
     * the first String corresponds to the key, and the second String
     * corresponds to the value.
     */
    static final Function<Pair<String, String>, URLParameter> fromPair =
            new Function<Pair<String, String>, URLParameter>()
            {
                @Override
                public URLParameter apply( final Pair<String, String> pair )
                {
                    return new URLParameter( pair.getFirstComponent(), pair.getSecondComponent() );
                }
            };

    /**
     * Constructs a URLParameter with the specified name and value.
     * 
     * @param name
     *        the name of this parameter.
     * @param value
     *        the value of this parameter.
     * @throws NullPointerException
     *         if name or value are null.
     */
    public URLParameter( final String name, final String value )
    {
        CheckParameters.areNotNull( name, value );
        this.name = name;
        this.value = value;
    }

    /**
     * This URLParameter is equal to another object if the other object is a
     * URLParameter holding an equal name and value.
     * 
     * @param other
     *        the object to compare this URLParameter to.
     * @return true if the specified object is a URLParameter holding an equal
     *         name and value, false otherwise.
     */
    @Override
    public boolean equals( final Object other )
    {
        return other instanceof URLParameter && ( (URLParameter) other ).name.equals( name )
                && ( (URLParameter) other ).value.equals( value );
    }

    /**
     * Computes a hashcode using the name and value.
     * 
     * @return a hashcode computed using the name and value.
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + 45723 * value.hashCode();
    }
}
