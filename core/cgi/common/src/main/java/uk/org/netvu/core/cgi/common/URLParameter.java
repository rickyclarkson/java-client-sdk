package uk.org.netvu.core.cgi.common;

/**
 * A representation of a URL Parameter (foo=bar).
 */
public final class URLParameter
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
     * A Conversion that, given a Pair of Strings, produces a URLParameter where
     * the first String corresponds to the key, and the second String
     * corresponds to the value.
     */
    static final Conversion<Pair<String, String>, URLParameter> fromPair = new Conversion<Pair<String, String>, URLParameter>()
    {
        @Override
        public URLParameter convert( final Pair<String, String> pair )
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
     */
    public URLParameter( final String name, final String value )
    {
        Checks.notNull( name, value );
        this.name = name;
        this.value = value;
    }

    /**
     * This URLParameter is equal to another object if the other object is a
     * URLParameter holding an equal name and value.
     */
    @Override
    public boolean equals( final Object other )
    {
        return other instanceof URLParameter
                && ( (URLParameter) other ).name.equals( name )
                && ( (URLParameter) other ).value.equals( value );
    }

    /**
     * Computes a hashcode using the name and value.
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + 45723 * value.hashCode();
    }
}
