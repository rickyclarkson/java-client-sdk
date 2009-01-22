package uk.org.netvu.protocol;

import static java.util.Arrays.asList;

import java.util.List;

/**
 * An IPv4 address.
 */
public final class IPAddress
{
    /**
     * This IPAddress as an int.
     */
    public final int rawValue;

    /**
     * Constructs an IPAddress with the given int as its value.
     * 
     * @param rawValue
     *        the raw int value to use for the constructed IPAddress.
     */
    public IPAddress( final int rawValue )
    {
        this.rawValue = rawValue;
    }

    /**
     * Constructs a dotted-quad representation of this IPAddress, e.g.,
     * 10.2.3.4.
     * 
     * @return a dotted-quad representation of this IPAddress.
     */
    @Override
    public String toString()
    {
        return ( rawValue >>> 24 ) + "." + ( ( rawValue & 0x00FF0000 ) >>> 16 ) + "."
                + ( ( rawValue & 0x0000FF00 ) >>> 8 ) + "." + ( rawValue & 0xFF );
    }

    /**
     * Converts a String containing an IP address in dotted-quad format to an
     * IPAddress, returning an Option containing an IPAddress, or an empty
     * Option if the String could not be converted into an IPAddress.
     * 
     * @param s
     *        the String to parse.
     * @return an Option containing an IPAddress if the String was parsed
     *         successfully, or an empty Option otherwise.
     */
    static Option<IPAddress> fromString( final String s )
    {
        final List<String> parts = asList( s.split( "\\." ) );

        List<Integer> intParts = Lists.map( parts, new Function<String, Integer>()
        {
            @Override
            public Integer apply( final String s )
            {
                return Integer.parseInt( s );
            }
        } );
        intParts = Lists.filter( intParts, new Function<Integer, Boolean>()
        {
            @Override
            public Boolean apply( final Integer i )
            {
                return i >= 0 && i <= 255;
            }
        } );

        if ( intParts.size() != 4 )
        {
            return Option.getEmptyOption( s + " is not a valid IP address." );
        }

        return Option.getFullOption( new IPAddress( intParts.get( 0 ) << 24 | intParts.get( 1 ) << 16
                | intParts.get( 2 ) << 8 | intParts.get( 3 ) ) );
    }

    /**
     * A Function that delegates to fromString(String).
     */
    static final Function<String, Option<IPAddress>> fromString = new Function<String, Option<IPAddress>>()
    {
        @Override
        public Option<IPAddress> apply( final String s )
        {
            return fromString( s );
        }
    };

    /**
     * Identifies whether this IPAddress is equal to some other object.
     * 
     * @return true if the other object is an IPAddress and has the same
     *         rawValue, false otherwise.
     */
    @Override
    public boolean equals( final Object other )
    {
        return other instanceof IPAddress && ( (IPAddress) other ).rawValue == rawValue;
    }

    /**
     * Gives a hash code consistent with the equals implementation.
     * 
     * @return a hash code consistent with the equals implementation.
     */
    @Override
    public int hashCode()
    {
        return rawValue;
    }
}
