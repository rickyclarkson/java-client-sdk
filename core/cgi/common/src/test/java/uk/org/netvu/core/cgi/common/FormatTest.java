package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class FormatTest
{
    @Test
    public void testOneOf()
    {
        final Random random = new Random( 0 );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final Format one = Format.oneOf( random );
            assertTrue( one.toString().equals( one.toString().toLowerCase() ) );
            assertTrue( Format.fromString.convert( one.toString() ) == one );
        }
    }
}
