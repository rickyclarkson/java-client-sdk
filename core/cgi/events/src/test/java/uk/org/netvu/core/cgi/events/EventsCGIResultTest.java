package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import uk.org.netvu.core.cgi.events.EventsCGIResult.Builder;

/**
 * Tests EventsCGIResult implementations.
 */
public class EventsCGIResultTest
{
    /**
     * Generates an infinite series of events with random values.
     * 
     * @param random
     *        the random number generator
     * @return an Iterator producing random events.
     */
    public static final Iterator<EventsCGIResult> eventGenerator(
            final Random random )
    {
        final Iterator<EventsCGIResult.Builder> builders = eventBuilderGenerator( random );

        return new Iterator<EventsCGIResult>()
        {
            public void remove()
            {
                builders.remove();
            }

            public EventsCGIResult next()
            {
                return builders.next().build();
            }

            public boolean hasNext()
            {
                return builders.hasNext();
            }
        };

    }

    /**
     * Creates an infinite series of completed random EventCGIResult.Builders.
     */
    public static final Iterator<EventsCGIResult.Builder> eventBuilderGenerator(
            final Random random )
    {
        final Iterator<String> strings = Generators.strings( random ).iterator();

        return new Iterator<EventsCGIResult.Builder>()
        {
            public boolean hasNext()
            {
                return true;
            }

            public EventsCGIResult.Builder next()
            {
                final UInt31 julianTime = UInt31Test.uint31s( random ).next();

                return new EventsCGIResult.Builder().alarm( strings.next() ).archive(
                        random.nextInt() ).cam( random.nextInt() ).duration(
                        new UInt31( random.nextInt( Integer.MAX_VALUE
                                - julianTime.toInt() ) ) ).file( strings.next() ).julianTime(
                        julianTime ).offset( random.nextInt() ).preAlarm(
                        random.nextInt() ).onDisk( random.nextBoolean() ).status(
                        Status.oneOf( random ) ).alarmType(
                        AlarmType.oneOf( random ) );
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Tests that a EventsCGIResult has a valid duration.
     * 
     * @param event
     *        the event to test.
     * @return true if the EventsCGIResult has a valid duration, false
     *         otherwise.
     */
    public static boolean testEvent( final EventsCGIResult event )
    {
        return event.getJulianTime() + event.getDuration() >= 0
                && event.equals( event );
    }

    /**
     * Tests a number of generated events to see whether they meet the contracts
     * for EventsCGIResult.
     */
    @Test
    public void testGeneratedEvents()
    {
        final Iterator<EventsCGIResult> events = EventsCGIResultTest.eventGenerator( new Random(
                0 ) );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertTrue( testEvent( events.next() ) );
        }
    }

    /**
     * Tests that parsing a valid line of CSV succeeds.
     */
    @Test
    public void testValidParse()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0" );
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0, 4, 8" );
    }

    /**
     * Tests that parsing a line of CSV with not enough values throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNotEnoughColumns()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2" );

    }

    /**
     * Tests that parsing a line of CSV with too many values throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTooManyColumns()
    {
        EventsCGIResult.fromString( "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14" );
    }

    /**
     * Tests that parsing a line of CSV with a malformed number throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNumericError()
    {
        EventsCGIResult.fromString( "1, b, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0" );

    }

    /**
     * Tests that parsing a line of CSV with a negative timestamp throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTime()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, -111488075, 3600, ,overwitten, 1, 10, 2, 0" );
    }

    /**
     * Tests that equals does not return true for distinct EventsCGIResults.
     */
    @Test
    public void testInequality()
    {
        final Iterator<EventsCGIResult> results = eventGenerator( new Random( 0 ) );
        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertFalse( results.next().equals( results.next() ) );
        }
    }

    /**
     * Tests that equals returns false when comparing an EventsCGIResult to a
     * String.
     */
    @Test
    public void testUnrelatedEquality()
    {
        assertFalse( eventGenerator( new Random( 0 ) ).next().equals( "test" ) );
    }

    /**
     * Tests that the status passed to EventsCGIResult.Builder is retained in
     * the built EventsCGIResult.
     */
    @Test
    public void testStatusRetention()
    {
        try
        {
            assertTrue( new EventsCGIResult.Builder().cam( 1 ).alarm( "test" ).julianTime(
                    new UInt31( 100 ) ).offset( 5 ).file( "ignore" ).onDisk(
                    true ).duration( new UInt31( 40 ) ).preAlarm( 1 ).archive(
                    1 ).status( Status.NONE ).alarmType( AlarmType.CAMERA ).build().getStatus() == Status.NONE );
        }
        catch ( final NullPointerException e )
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tests that equal objects have equal hashCodes.
     */
    @Test
    public void testHashCode()
    {
        final Builder builder = eventBuilderGenerator( new Random( 0 ) ).next();
        assertTrue( builder.build().hashCode() == builder.build().hashCode() );
    }

    /**
     * Tests that the toString() implementation returns a non-empty String..
     */
    @Test
    public void testToString()
    {
        final Random random = new Random( 0 );
        final Iterator<EventsCGIResult> gen = eventGenerator( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertTrue( gen.next().toString().length() > 0 );
        }
    }
}
