package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConversionTest
{
    @Test
    public void testIdentity()
    {
        assertTrue( Conversion.<Boolean> identity().convert( true ) == true );
    }

    @Test
    public void testAndThen()
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

    @Test(expected = UnsupportedOperationException.class)
    public void testThrowUnsupportedOperationException()
    {
        Conversion.throwUnsupportedOperationException().convert( new Object() );
    }

    @Test
    public void stringToBoolean()
    {
        assertTrue( Conversion.stringToBoolean.convert( "true" ) );
        assertFalse( Conversion.stringToBoolean.convert( "false" ) );
    }

    @Test
    public void stringToInt()
    {
        assertTrue( Conversion.stringToInt.convert( "50" ) == 50 );
    }

    @Test
    public void stringToLong()
    {
        assertTrue( Conversion.stringToLong.convert( "50" ) == 50 );
    }

    @Test
    public void hexStringToInt()
    {
        assertTrue( Conversion.hexStringToInt.convert( "ff" ) == 255 );
    }

    @Test
    public void hexStringToLong()
    {
        assertTrue( Conversion.hexStringToLong.convert( "ff" ) == 255 );
    }

    @Test
    public void longToHexString()
    {
        assertTrue( Conversion.longToHexString.convert( 255L ).equals( "ff" ) );
    }

    @Test
    public void intToHexString()
    {
        assertTrue( Conversion.intToHexString.convert( 255 ).equals( "ff" ) );
    }
}
