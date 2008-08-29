package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

/**
 * Tests the methods in Checks.
 */
public class ChecksTest
{
    /**
     * checks(x,?).notLessThan(y,?) throws IllegalArgumentException if x<y else
     * returns x
     */
    @Test
    public void testNotLessThan()
    {
        final Random random = new Random( 0 );
        final Iterator<String> stringsAndNull = Generators.stringsAndNull( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final int x = random.nextInt();
            final int y = random.nextInt();
            if ( x < y )
            {
                try
                {
                    Checks.checks( x, stringsAndNull.next() ).notLessThan( y,
                            stringsAndNull.next() );
                    fail();
                }
                catch ( final IllegalArgumentException e )
                {
                    // pass
                }
            }
            else
            {
                assertTrue( Checks.checks( x, stringsAndNull.next() ).notLessThan(
                        y, stringsAndNull.next() ).done() == x );
            }
        }
    }

    /**
     * checks(x,?).notGreaterThan(y,?) throws IllegalArgumentException if x>y
     * else returns x
     */
    @Test
    public void testNotGreaterThan()
    {
        final Random random = new Random( 0 );
        final Iterator<String> stringsAndNull = Generators.stringsAndNull( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final int x = random.nextInt();
            final int y = random.nextInt();

            if ( x > y )
            {
                try
                {
                    Checks.checks( x, stringsAndNull.next() ).notGreaterThan(
                            y, stringsAndNull.next() );
                    fail();
                }
                catch ( final IllegalArgumentException e )
                {
                    // pass
                }
            }
            else
            {
                assertTrue( Checks.checks( x, stringsAndNull.next() ).notGreaterThan(
                        y, stringsAndNull.next() ).done() == x );
            }
        }
    }
}
