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
     * Constructs a URLParameter with the specified name and value.
     * 
     * @param name
     *        the name of this parameter.
     * @param value
     *        the value of this parameter.
     */
    public URLParameter( final String name, final String value )
    {
        this.name = name;
        this.value = value;
    }

    static final Conversion<Pair<String, String>, URLParameter> fromPair = new Conversion<Pair<String, String>, URLParameter>()
    {
        @Override
        public URLParameter convert( final Pair<String, String> pair )
        {
            return new URLParameter( pair.first(), pair.second() );
        }
    };

    @Override
    public boolean equals( final Object other )
    {
        return other instanceof URLParameter
                && ( (URLParameter) other ).name.equals( name )
                && ( (URLParameter) other ).value.equals( value );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode() + 45723 * value.hashCode();
    }
}
