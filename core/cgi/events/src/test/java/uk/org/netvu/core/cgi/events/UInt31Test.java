package uk.org.netvu.core.cgi.events;

import java.util.Iterator;
import java.util.Random;

import uk.org.netvu.core.cgi.common.Generators;
import uk.org.netvu.core.cgi.common.UInt31;

import junit.framework.TestCase;

/**
 * A set of tests that verify the implementation of UInt31.
 */
public class UInt31Test extends TestCase
{
    /**
     * Given a positive int, UInt31 should accept it and give back the same
     * value when toInt() is called.
     */
    public void testPositives()
    {
        assertEquals( new UInt31( 100 ).toInt(), 100 );
    }

    /**
     * Given zero, UInt31 should accept it and give back 0 when toInt() is
     * called.
     */
    public void testZero()
    {
        assertEquals( new UInt31( 0 ).toInt(), 0 );
    }

    /**
     * Given a negative int, UInt31 should throw an IllegalArgumentException in
     * construction.
     */
    public void testNegatives()
    {
        try
        {
            new UInt31( -1 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            // pass
        }
    }

    /**
     * If two UInt31s have the same value they should be equal to each other and
     * have equal hashcodes.
     */
    public void testEqualsAndHashcode()
    {
        assertEquals( new UInt31( 2 ), new UInt31( 2 ) );
        assertEquals( new UInt31( 2 ).hashCode(), new UInt31( 2 ).hashCode() );
        assertFalse( new UInt31( 2 ).equals( new UInt31( 3 ) ) );
        assertFalse( new UInt31( 2 ).equals( "2" ) );
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
  
    /**
     * Tests the Comparable implementation in UInt31.
     */
    public void testComparisons()
    {
        assertTrue(new UInt31(5).compareTo(new UInt31(4))>0);
        assertTrue(new UInt31(5).compareTo(new UInt31(6))<0);
        assertTrue(new UInt31(5).compareTo(new UInt31(5))==0);
    }
    
    /**
     * Tests that a UInt31's toString() is the same as for the value it stores.
     */
    public void testToString()
    {
        Random random=new Random();
        for (int a=0;a<Generators.LIMIT;a++)
        {
            int i=random.nextInt(Integer.MAX_VALUE);
            assertTrue(Integer.toString(i).equals(new UInt31(i).toString()));
        }
    }
    
}
