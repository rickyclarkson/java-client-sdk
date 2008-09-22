package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import uk.org.netvu.core.cgi.common.Generator;
import uk.org.netvu.core.cgi.common.Generators;
import uk.org.netvu.core.cgi.events.EventsCGIResult.AlarmType;
import uk.org.netvu.core.cgi.events.EventsCGIResult.Builder;
import uk.org.netvu.core.cgi.events.EventsCGIResult.Status;

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
    public static final Generator<EventsCGIResult> eventGenerator(
            final Random random )
    {
        final Generator<EventsCGIResult.Builder> builders = eventBuilderGenerator( random );

        return new Generator<EventsCGIResult>()
        {
            public EventsCGIResult next()
            {
                return builders.next().build();
            }
        };

    }

    /**
     * Creates an infinite series of completed random EventsCGIResult.Builders.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of completed random EventsCGIResult.Builders.
     */
    public static final Generator<EventsCGIResult.Builder> eventBuilderGenerator(
            final Random random )
    {
        final Generator<String> strings = Generators.strings( random );

        return new Generator<EventsCGIResult.Builder>()
        {
            private AlarmType oneAlarm( final Random random )
            {
                return AlarmType.values()[random.nextInt( AlarmType.values().length )];
            }

            private Status oneStatus( final Random random )
            {
                return Status.values()[random.nextInt( Status.values().length )];
            }

            public EventsCGIResult.Builder next()
            {
                final Generator<Integer> nonNegatives = Generators.nonNegativeInts( random );
                final int julianTime = nonNegatives.next();

                return new EventsCGIResult.Builder().alarm( strings.next() ).archive(
                        nonNegatives.next() ).camera( random.nextInt( 65 ) ).duration(
                        Math.max( 0, nonNegatives.next() - julianTime ) ).file(
                        strings.next() ).julianTime( julianTime ).offset(
                        random.nextInt( 180000 ) - 90000 ).preAlarm(
                        nonNegatives.next() ).onDisk( random.nextBoolean() ).status(
                        oneStatus( random ) ).alarmType( oneAlarm( random ) );
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
        return event.getJulianTime() + event.getDuration() >= 0;
    }

    /**
     * Tests a number of generated events to see whether they meet the contracts
     * for EventsCGIResult.
     */
    @Test
    public void generatedEvents()
    {
        final Generator<EventsCGIResult> events = EventsCGIResultTest.eventGenerator( new Random(
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
    public void validParse()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0" );
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0, 4, 8" );
    }

    /**
     * Tests that parsing a line of CSV with not enough values throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notEnoughColumns()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2" );

    }

    /**
     * Tests that parsing a line of CSV with too many values throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void tooManyColumns()
    {
        EventsCGIResult.fromString( "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14" );
    }

    /**
     * Tests that parsing a line of CSV with a malformed number causes an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void numericError()
    {
        EventsCGIResult.fromString( "1, b, COURTYARD, 1211488075, 3600, ,overwitten, 1, 10, 2, 0" );
    }

    /**
     * Tests that parsing a line of CSV with a negative timestamp throws an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeTime()
    {
        EventsCGIResult.fromString( "1, 1, COURTYARD, -111488075, 3600, ,overwitten, 1, 10, 2, 0" );
    }

    /**
     * Tests that the values passed to EventsCGIResult.Builder are retained in
     * the built EventsCGIResult.
     */
    @Test
    public void retention()
    {
        final EventsCGIResult result = new EventsCGIResult.Builder().camera( 1 ).alarm(
                "test" ).julianTime( 100 ).offset( 5 ).file( "ignore" ).onDisk(
                true ).duration( 40 ).preAlarm( 1 ).archive( 1 ).status(
                EventsCGIResult.Status.NONE ).alarmType(
                EventsCGIResult.AlarmType.CAMERA ).build();

        assertTrue( result.getCam() == 1 );
        assertTrue( result.getAlarm().equals( "test" ) );
        assertTrue( result.getJulianTime() == 100 );
        assertTrue( result.getOffset() == 5 );
        assertTrue( result.getFile().equals( "ignore" ) );
        assertTrue( result.isOnDisk() );
        assertTrue( result.getDuration() == 40 );
        assertTrue( result.getPreAlarm() == 1 );
        assertTrue( result.getArchive() == 1 );
        assertTrue( result.getStatus() == EventsCGIResult.Status.NONE );
        assertTrue( result.getAlarmType() == EventsCGIResult.AlarmType.CAMERA );
    }

    /**
     * Tests that the toString() implementation returns a non-empty String..
     */
    @Test
    public void testToString()
    {
        final Random random = new Random( 0 );
        final Generator<EventsCGIResult> gen = eventGenerator( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertTrue( gen.next().toString().length() > 0 );
        }
    }

    /**
     * Tests that parsing a null String yields a NullPointerException. The use
     * case "Build events.cgi query" specifies this.
     */
    @Test(expected = NullPointerException.class)
    public void nullPointerException()
    {
        EventsCGIResult.fromString( null );
    }

    /**
     * Tests that parsing an empty ("") String yields an
     * IllegalArgumentException. The use case "Build events.cgi query" specifies
     * this.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyString()
    {
        EventsCGIResult.fromString( "" );
    }

    /**
     * Tests that parsing a malformed String yields an IllegalArgumentException.
     * The use case "Build events.cgi query" specifies this.
     */
    @Test(expected = IllegalArgumentException.class)
    public void parsingMalformedString()
    {
        EventsCGIResult.fromString( "3, 4, 5" );
    }

    /**
     * Tests that parsing a String containing a camera number that is too high
     * yields an {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void highCameraNumber()
    {
        aBuilder().camera( 65 ).build();
    }

    private Builder aBuilder()
    {
        return eventBuilderGenerator( new Random( 0 ) ).next();
    }

    /**
     * Tests that an offset lower than -90000 yields an
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void largeNegativeOffset()
    {
        aBuilder().offset( -90001 ).build();
    }

    /**
     * Tests that an offset greater than 90000 yields an
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void largeOffset()
    {
        aBuilder().offset( 90001 ).build();
    }

    /**
     * Tests that an incomplete EventsCGIResult cannot be built.
     */
    @Test(expected = IllegalStateException.class)
    public void incompleteObject()
    {
        new EventsCGIResult.Builder().build();
    }
}
