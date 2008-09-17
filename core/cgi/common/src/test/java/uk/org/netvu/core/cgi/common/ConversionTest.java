package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Conversion.
 */
public class ConversionTest
{
    /**
     * Tests that Conversion.identity returns a value given to it.
     */
    @Test
    public void identity()
    {
        assertTrue( Conversion.<Boolean> identity().convert( true ) == true );
    }

    /**
     * Tests that chaining 2 Conversion with andThen() yields the correct
     * result.
     */
    @Test
    public void andThen()
    {
        assertTrue( Conversion.hexStringToInt.andThen(
                new Conversion<Integer, Integer>()
                {
                    @Override
                    public Integer convert( final Integer i )
                    {
                        return i * 2;
                    }
                } ).convert( "FF" ) == 510 );
    }

    /**
     * Tests that Conversion.stringToBoolean correctly converts Strings to
     * booleans.
     */
    @Test
    public void stringToBoolean()
    {
        assertTrue( Conversion.stringToBoolean.convert( "true" ) );
        assertFalse( Conversion.stringToBoolean.convert( "false" ) );
    }

    /**
     * Tests that Conversion.stringToInt correctly converts Strings to ints.
     */
    @Test
    public void stringToInt()
    {
        assertTrue( Conversion.stringToInt.convert( "50" ).get() == 50 );
    }

    /**
     * Tests that Conversion.stringToLong correctly converts Strings to longs.
     */
    @Test
    public void stringToLong()
    {
        assertTrue( Conversion.stringToLong.convert( "50" ) == 50 );
    }

    /**
     * Tests that Conversion.hexStringToInt correctly converts hex Strings to
     * ints.
     */
    @Test
    public void hexStringToInt()
    {
        assertTrue( Conversion.hexStringToInt.convert( "ff" ) == 255 );
    }

    /**
     * Tests that Conversion.hexStringToLong correctly converts hex Strings to
     * longs.
     */
    @Test
    public void hexStringToLong()
    {
        assertTrue( Conversion.hexStringToLong.convert( "ff" ) == 255 );
    }

    /**
     * Tests that Conversion.longToHexString correctly converts longs to hex
     * Strings.
     */
    @Test
    public void longToHexString()
    {
        assertTrue( Conversion.longToHexString.convert( 255L ).equals( "ff" ) );
    }

    /**
     * Tests that Conversion.intToHexString correctly converts ints to hex
     * Strings.
     */
    @Test
    public void intToHexString()
    {
        assertTrue( Conversion.intToHexString.convert( 255 ).equals( "ff" ) );
    }
}
