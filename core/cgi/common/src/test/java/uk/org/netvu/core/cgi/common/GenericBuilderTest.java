package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Unit tests for GenericBuilder.
 */
public final class GenericBuilderTest
{
    Parameter<String, Option<String>> param = Parameter.param( "blah", "blah",
            Conversion.<String> identity() );

    /**
     * Tests that a value stored in a GenericBuilder gets converted as per the
     * Parameter's rules.
     */
    @Test
    public void conversion()
    {
        assertTrue( Integer.parseInt( new GenericBuilder().with( param, "10" ).get(
                param ).get() ) == 10 );
    }

    /**
     * Tests that setting the same once-only Parameter twice results in an
     * IllegalStateException.
     */
    @Test(expected = IllegalStateException.class)
    public void repeating()
    {
        new GenericBuilder().with( param, "10" ).with( param, "10" );
    }

    private static final Parameter<Integer, Option<Integer>> time = Parameter.notNegative( Parameter.param(
            "time", "time since 1970", Conversion.stringToInt ) );
    private static final Parameter<Integer, Option<Integer>> range = Parameter.param(
            "range", "range to search", Conversion.stringToInt );

    /**
     * Tests that an IllegalStateException is thrown when invalid parameter
     * values are given.
     */
    @Test(expected = IllegalStateException.class)
    public void validatorFail()
    {
        new GenericBuilder( new Validator()
        {
            @Override
            public boolean isValid( final GenericBuilder builder )
            {
                final Option<Integer> oTime = builder.get( time );
                final Option<Integer> oRange = builder.get( range );

                return oTime.isNone() || oRange.isNone() ? true : oTime.get()
                        + oRange.get() >= 0;
            }
        } ).with( time, 2000000000 ).with( range, 2000000000 );
    }

    /**
     * Tests that GenericBuilder.isDefault works as specified.
     */
    @Test
    public void testIsDefault()
    {
        assertTrue( new GenericBuilder().isDefault( time ) );
        assertFalse( new GenericBuilder().with( time, 40 ).isDefault( time ) );
    }

    /**
     * Tests that parsing a URL results in a GenericBuilder holding the correct
     * values.
     */
    @Test
    public void fromURL()
    {
        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
        {
            {
                add( time );
                add( range );
            }
        };

        final GenericBuilder builder = GenericBuilder.fromURL(
                "time=10&range=40", params );

        assertTrue( builder.get( time ).get() == 10 );
        assertTrue( builder.get( range ).get() == 40 );

        assertTrue( builder.toURLParameters( params ).equals(
                "time=10&range=40" ) );
    }

    /**
     * Tests that populating a sparse array Parameter and then reading back its
     * values yields the correct values.
     */
    @Test
    public void sparseArray()
    {
        final Parameter<List<Pair<Integer, String>>, TreeMap<Integer, String>> sparseIdentity = Parameter.sparseArrayParam(
                "foo",
                "bar",
                Conversion.<String> identity(),
                Conversion.<String, String> throwUnsupportedOperationException() );

        assertTrue( new GenericBuilder().with( sparseIdentity,
                new ArrayList<Pair<Integer, String>>()
                {
                    {
                        add( Pair.pair( 4, "bar" ) );
                        add( Pair.pair( 5, "foo" ) );
                    }
                } ).with( sparseIdentity,
                new ArrayList<Pair<Integer, String>>()
                {
                    {
                        add( Pair.pair( 2, "baz" ) );
                        add( Pair.pair( 10, "spam" ) );
                    }
                } ).get( sparseIdentity ).get( 2 ).equals( "baz" ) );

    }

    /**
     * Tests that GenericBuilder.fromStrings works as specified.
     */
    @Test
    public void fromStrings()
    {
        final Parameter<String, String> name = Parameter.param( "name", "foo",
                "Bob", Conversion.<String> identity() );
        final Parameter<String, String> surname = Parameter.param( "surname",
                "foo", "Hope", Conversion.<String> identity() );

        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
        {
            {
                add( name );
                add( surname );
            }
        };

        assertTrue( GenericBuilder.fromStrings( params,
                Arrays.asList( "John", "Major" ) ).get( surname ).equals(
                "Major" ) );

    }
}
