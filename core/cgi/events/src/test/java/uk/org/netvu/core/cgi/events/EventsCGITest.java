package uk.org.netvu.core.cgi.events;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import uk.org.netvu.core.cgi.common.Action;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Generator;
import uk.org.netvu.core.cgi.common.Generators;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.events.EventsCGI.Builder;

/**
 * A set of tests that try to make sure EventsCGI only allows valid CGI queries
 * as per the Video Server Specification
 */
public class EventsCGITest
{
    /**
     * Tests that the default values in EventsCGI.Builder are correct according
     * to the spec. The format is missing because it is always implicitly
     * specified as CSV.
     */
    @Test
    public void defaultValues()
    {
        final EventsCGI events = new EventsCGI.Builder().build();
        assertTrue( events.getFormat() == Format.CSV );
        assertTrue( events.getTime() == 0 );
        assertTrue( events.getRange() == Integer.MAX_VALUE );
        assertTrue( events.getMaxLength() == 100 );
        assertTrue( events.getText().equals( "" ) );
        assertTrue( events.getCamMask() == 0 );
        assertTrue( events.getAlarmMask() == 0 );
        assertTrue( events.getVmdMask() == 0 );
        assertTrue( events.getGpsMask() == 0 );
        assertTrue( events.getSysMask() == 0 );
    }

    /**
     * Tests that applying one parameter more than once will throw an
     * IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    public void twoTimeParameters()
    {
        new EventsCGI.Builder().time( 100 ).time( 200 );
    }

    /**
     * Tests that negative times are disallowed.
     */
    @Test
    public void negativeTimes()
    {
        try
        {
            new EventsCGI.Builder().time( -100 );
            fail( "Negative times are invalid" );
        }
        catch ( final IllegalArgumentException e )
        {
            // pass
        }
    }

    /**
     * Tests that the text and gpsMask parameters are mutually exclusive.
     */
    @Test
    public void textAndGpsMaskAreMutex()
    {
        try
        {
            new EventsCGI.Builder().text( "text" ).gpsMask( 100 );
            fail();
        }
        catch ( final IllegalStateException e )
        {
            // pass
        }
    }

    /**
     * Tests that the alarm mask, vmd mask, gps mask and sys mask parameters are
     * not allowed in the same EventsCGI.Builder.
     */
    @Test
    public void alarmMaskEtcAreMutuallyExclusive()
    {
        final List<Action<Builder>> params = new ArrayList<Action<Builder>>();
        params.add( new Action<Builder>()
        {
            public void invoke( final Builder builder )
            {
                builder.alarmMask( 4 );
            }
        } );
        params.add( new Action<Builder>()
        {
            public void invoke( final Builder builder )
            {
                builder.vmdMask( 8 );
            }
        } );
        params.add( new Action<Builder>()
        {
            public void invoke( final Builder builder )
            {
                builder.gpsMask( 32 );
            }
        } );
        params.add( new Action<Builder>()
        {
            public void invoke( final Builder builder )
            {
                builder.sysMask( 23 );
            }
        } );

        for ( int a = 0; a < params.size(); a++ )
        {
            for ( int b = 0; b < params.size(); b++ )
            {
                if ( a != b )
                {
                    final Builder builder = new EventsCGI.Builder();
                    params.get( a ).invoke( builder );
                    try
                    {
                        params.get( b ).invoke( builder );
                        fail();
                    }
                    catch ( final IllegalStateException e )
                    {
                        // pass
                    }
                }
            }
        }
    }

    // Tests that the built EventsCGI has the values supplied to the builder,
    // using random data generation.
    private static boolean testRetention( final Random random )
    {
        final int randomInt = random.nextInt();
        final long randomLong = random.nextLong();
        final String randomString = Generators.strings( random ).next();
        final int randomNonNegativeInt = Generators.nonNegativeInts( random ).next();

        assertTrue( new EventsCGI.Builder().alarmMask( randomInt ).build().getAlarmMask() == randomInt );
        assertTrue( new EventsCGI.Builder().range( randomNonNegativeInt ).build().getRange() == randomNonNegativeInt );
        assertTrue( new EventsCGI.Builder().camMask( randomLong ).build().getCamMask() == randomLong );
        assertTrue( new EventsCGI.Builder().gpsMask( randomInt ).build().getGpsMask() == randomInt );
        assertTrue( new EventsCGI.Builder().length( randomInt ).build().getMaxLength() == randomInt );
        assertTrue( new EventsCGI.Builder().sysMask( randomInt ).build().getSysMask() == randomInt );

        return new EventsCGI.Builder().text( randomString ).build().getText().equals(
                randomString )
                && new EventsCGI.Builder().time( randomNonNegativeInt ).build().getTime() == randomNonNegativeInt
                && new EventsCGI.Builder().vmdMask( randomLong ).build().getVmdMask() == randomLong;
    }

    /**
     * Tests that the built EventsCGI has the values supplied to the builder.
     */
    @Test
    public void retention()
    {
        final Random random = new Random( 0 );
        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            assertTrue( testRetention( random ) );
        }
    }

    /**
     * Tests that toString always gives something that can be made into a valid
     * URL.
     * 
     * @throws MalformedURLException
     *         if the URL is invalid.
     */
    @Test
    public void testToString() throws MalformedURLException
    {
        final Random random = new Random( 0 );
        final Generator<EventsCGI> cgis = randomEventsCGIs( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            new URL( "http://none" + cgis.next() );
        }
    }

    // compares some CSV with the generated CSV from an EventsCGIResult,
    // ignoring fields 0 and 7.
    // note that it's 0 and 6 in the code because deleting the first changes the
    // indices of the rest.
    private boolean compare( final String input, final EventsCGIResult output )
    {
        final List<String> fields = new ArrayList<String>(
                Arrays.asList( Strings.split( input ) ) );
        final List<String> others = new ArrayList<String>(
                Arrays.asList( Strings.split( output.toCSV( 0 ) ) ) );

        fields.remove( 0 );
        others.remove( 0 );

        fields.remove( 6 );
        others.remove( 6 );

        return fields.equals( others );
    }

    /**
     * Tests that parsing real data stored from a server results then obtaining
     * CSV from that results in the same CSV as the original.
     */
    @Test
    public void parseRealData()
    {
        final String[] data = {
            "1, 0, System Startup, 1122030592, 3600, ,overwitten, 1, 10, 2, 0, 4, 8",
            "2, 0, RTC reset (CBUS), 1122034260, 3600, (11:10:34 22/Jul/2005),overwitten, 2, 10, 2, 0, 4, 8",
            "3, 0, RTC reset (CBUS), 1122035099, 3600, (13:25:52 22/Jul/2005),overwitten, 3, 10, 2, 0, 4, 8",
            "4, 1, Camera fail, 1122035906, 3600, ,overwitten, 4, 1, 0, 0, 4, 8",
            "5, 1, Camera Restored, 1122035915, 3600, ,overwitten, 5, 1, 0, 0, 4, 8",
            "6, 0, System Startup, 1122035962, 3600, ,overwitten, 6, 10, 2, 0, 4, 8",
            "7, 0, System Startup, 1122037538, 3600, ,overwitten, 7, 10, 2, 0, 4, 8",
            "8, 0, System Startup, 1122037625, 3600, ,overwitten, 8, 10, 2, 0, 4, 8",
            "9, 0, System Startup, 1122038376, 3600, ,overwitten, 9, 10, 2, 0, 4, 8",
            "10, 0, RTC reset (CBUS), 1122463200, 3600, (11:19:58 27/Jul/2005),overwitten, 10, 10, 2, 0, 4, 8",
            "11, 0, System Startup, 1122907601, 3600, ,overwitten, 11, 10, 2, 0, 4, 8",
            "12, 0, RTC reset (CBUS), 1122907620, 3600, (14:46:57 01/Aug/2005),overwitten, 12, 10, 2, 0, 4, 8" };

        for ( final String line : data )
        {
            assertTrue( compare( line, EventsCGIResult.fromString( line ).get() ) );
        }
    }

    /**
     * An infinite series of randomly-created EventsCGI.Builders.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of randomly-created EventsCGI.Builders.
     */
    public static Generator<EventsCGI.Builder> randomEventsCGIBuilders(
            final Random random )
    {
        return new Generator<EventsCGI.Builder>()
        {
            public EventsCGI.Builder next()
            {
                final EventsCGI.Builder builder = new EventsCGI.Builder();

                final Generator<Integer> nonNegativeInts = Generators.nonNegativeInts( random );
                final Generator<String> strings = Generators.strings( random );

                // this is an anonymous intialiser - it is creating a new
                // ArrayList and adding values to it inline.
                final List<Runnable> methods = new ArrayList<Runnable>()
                {
                    {
                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.alarmMask( random.nextInt() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.camMask( random.nextLong() );
                            }
                        } );

                        add( new Runnable()
                        {

                            public void run()
                            {
                                builder.format( Format.oneOf( random ) );
                            }
                        } );
                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.gpsMask( random.nextInt() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.length( random.nextInt() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.range( nonNegativeInts.next() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.sysMask( random.nextInt() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.text( strings.next() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.time( nonNegativeInts.next() );
                            }
                        } );

                        add( new Runnable()
                        {
                            public void run()
                            {
                                builder.vmdMask( random.nextLong() );
                            }
                        } );
                    }
                };

                final List<Runnable> butNoExceptions = new ArrayList<Runnable>();
                for ( final Runnable runnable : methods )
                {
                    butNoExceptions.add( new Runnable()
                    {

                        public void run()
                        {
                            try
                            {
                                runnable.run();
                            }
                            catch ( final RuntimeException e )
                            {
                            }
                        }
                    } );
                }

                final int numMethodsToCall = random.nextInt( butNoExceptions.size() );

                for ( int b = 0; b < numMethodsToCall; b++ )
                {
                    butNoExceptions.get(
                            random.nextInt( butNoExceptions.size() ) ).run();
                }

                return builder;

            }
        };
    }

    /**
     * An infinite series of randomly-generated EventsCGIs.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of randomly-generated EventsCGIs.
     */
    public Generator<EventsCGI> randomEventsCGIs( final Random random )
    {
        final Generator<EventsCGI.Builder> builders = randomEventsCGIBuilders( random );
        return new Generator<EventsCGI>()
        {
            public EventsCGI next()
            {
                return builders.next().build();
            }
        };
    }

    /**
     * Makes sure that text that contains spaces doesn't result in a URL
     * containing spaces - they should be encoded.
     */
    @Test
    public void textWithSpacesDoesntResultInURLWithSpaces()
    {
        assertFalse( new EventsCGI.Builder().text( "hello world" ).build().toString().contains(
                " " ) );
    }

    /**
     * Tests that converting an EventsCGI to a String and back is not lossy.
     */
    @Test
    public void fromString()
    {
        final Random random = new Random( 0 );
        final Generator<EventsCGI> cgis = randomEventsCGIs( random );

        for ( int a = 0; a < Generators.LIMIT; a++ )
        {
            final EventsCGI next = cgis.next();
            assertTrue( EventsCGI.fromString(
                    random.nextBoolean() ? next.toString() : Strings.fromFirst(
                            '?', next.toString() ) ).toString().equals(
                    next.toString() ) );
        }
    }

    /**
     * Tests that parsing null yields a NullPointerException. Specified in the
     * use cases for the events interface.
     */
    @Test(expected = NullPointerException.class)
    public void parseNull()
    {
        EventsCGI.fromString( null );
    }

    /**
     * Tests that parsing the empty String ("") yields an
     * {@link IllegalArgumentException}. Specified in the use cases for the
     * events interface.
     */
    @Test(expected = IllegalArgumentException.class)
    public void parseEmptyString()
    {
        EventsCGI.fromString( "" );
    }

    /**
     * Tests that parsing some invalid CSV yields an
     * {@link IllegalArgumentException}. Specified in the use cases for the
     * events interface.
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalidParse()
    {
        EventsCGI.fromString( "?almmask=six" );
    }
}
