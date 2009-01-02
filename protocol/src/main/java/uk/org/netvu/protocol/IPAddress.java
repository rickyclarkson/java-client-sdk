package uk.org.netvu.protocol;

import static java.util.Arrays.asList;

import java.util.List;

public final class IPAddress
{
    public static Option<IPAddress> fromString( final String s )
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

    public final int rawValue;

    public static final Function<String, Option<IPAddress>> fromString = new Function<String, Option<IPAddress>>()
    {
        @Override
        public Option<IPAddress> apply( final String s )
        {
            return fromString( s );
        }
    };

    public IPAddress( final int rawValue )
    {
        this.rawValue = rawValue;
    }

    @Override
    public String toString()
    {
        return ( rawValue >>> 24 ) + "." + ( ( rawValue & 0x00FF0000 ) >>> 16 ) + "."
                + ( ( rawValue & 0x0000FF00 ) >>> 8 ) + "." + ( rawValue & 0xFF );
    }
}
