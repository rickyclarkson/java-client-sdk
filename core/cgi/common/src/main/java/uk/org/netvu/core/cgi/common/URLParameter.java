package uk.org.netvu.core.cgi.common;

public class URLParameter
{
    public final String name;
    public final String value;

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
