package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

/**
 * Tests the methods in Checks.
 */
public class ChecksTest
{
    /**
     * if (x!=null) notNull(x, y).equals(x) else notNull(x, y) throws
     * NullPointerException
     */
    @Test
    public void testNotNull()
    {
        final Random random = new Random( 0 );
        final Iterator<Object> objectsAndNull = Generators.objectsAndNull( random );

        final Iterator<String> stringsAndNull = Iterables.prepend(
                Arrays.asList( (String) null ), Generators.strings( random ) ).iterator();

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final Object o = objectsAndNull.next();

            if ( o == null )
            {
                try
                {
                    Checks.notNull( o, stringsAndNull.next() );
                    fail();
                }
                catch ( final NullPointerException e )
                {
                    // pass
                }
            }
            else
            {
                Checks.notNull( o, stringsAndNull.next() );
            }
        }
    }

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
