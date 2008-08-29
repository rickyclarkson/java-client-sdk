package uk.org.netvu.core.cgi.common;

import java.util.Iterator;
import java.util.Random;

/**
 * An immutable unsigned integer with 31 bits (0..Integer.MAX_VALUE).
 */
public final class UInt31 implements Comparable<UInt31>
{
    public static final Conversion<String, UInt31> fromString = new Conversion<String, UInt31>()
    {
        @Override
        public UInt31 convert( final String t )
        {
            return new UInt31( Integer.parseInt( t ) );
        }
    };

    /**
     * The wrapped value, a positive int.
     */
    private final int value;

    /**
     * @param value
     *        must be >= 0
     * @throws IllegalArgumentException
     *         if value < 0
     */
    public UInt31( final int value )
    {
        if ( value < 0 )
        {
            throw new IllegalArgumentException();
        }

        this.value = value;
    }

    /**
     * Gives the value of this UInt31 as a positive int.
     * 
     * @return the value of this UInt31 as a positive int.
     */
    public int toInt()
    {
        return value;
    }

    /**
     * Compares this object and another for equality.
     * 
     * @return true if the object being compared to is also a UInt31 with the
     *         same value, false otherwise.
     */
    @Override
    public boolean equals( final Object other )
    {
        return other instanceof UInt31 ? ( (UInt31) other ).toInt() == toInt()
                : false;
    }

    /**
     * Gives a hashcode for this object that satisfies the requirement that if
     * a.equals(b) then a.hashCode()==b.hashCode().
     * 
     * @return a hashcode for this object.
     */
    @Override
    public int hashCode()
    {
        return value;
    }

    /**
     * Compares this UInt31 and another numerically.
     */
    public int compareTo( final UInt31 o )
    {
        return o.value > value ? -1 : o.value < value ? 1 : 0;
    }

    /**
     * Gives the value held by this UInt31 as a String.
     */
    @Override
    public String toString()
    {
        return Integer.toString( value );
    }

    /**
     * An infinite series of randomly-generated UInt31s.
     * 
     * @param random
     *        the random number generator to use.
     */
    public static Iterator<UInt31> uint31s( final Random random )
    {
        return new Iterator<UInt31>()
        {
            public boolean hasNext()
            {
                return true;
            }
    
            public UInt31 next()
            {
                return new UInt31( random.nextInt( Integer.MAX_VALUE ) );
            }
    
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
