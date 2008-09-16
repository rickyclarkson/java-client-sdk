package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Unit tests for ParameterMap.
 */
public final class GenericBuilderTest
{
    Parameter<String, Option<String>> param = Parameter.param( "blah", "blah",
            Conversion.<String> identity() );

    /**
     * Tests that a value stored in a ParameterMap gets converted as per the
     * Parameter's rules.
     */
    @Test
    public void conversion()
    {
        assertTrue( Integer.parseInt( new ParameterMap().with( param, "10" ).get(
                param ).get() ) == 10 );
    }

    /**
     * Tests that setting the same once-only Parameter twice results in an
     * IllegalStateException.
     */
    @Test(expected = IllegalStateException.class)
    public void repeating()
    {
        new ParameterMap().with( param, "10" ).with( param, "10" );
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
        new ParameterMap( new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                final Option<Integer> oTime = parameterMap.get( time );
                final Option<Integer> oRange = parameterMap.get( range );

                return oTime.isNone() || oRange.isNone() ? true : oTime.get()
                        + oRange.get() >= 0;
            }
        } ).with( time, 2000000000 ).with( range, 2000000000 );
    }

    /**
     * Tests that ParameterMap.isDefault works as specified.
     */
    @Test
    public void testIsDefault()
    {
        assertTrue( new ParameterMap().isDefault( time ) );
        assertFalse( new ParameterMap().with( time, 40 ).isDefault( time ) );
    }

    /**
     * Tests that parsing a URL results in a ParameterMap holding the correct
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

        final ParameterMap parameterMap = ParameterMap.fromURL(
                "time=10&range=40", params );

        assertTrue( parameterMap.get( time ).get() == 10 );
        assertTrue( parameterMap.get( range ).get() == 40 );

        assertTrue( parameterMap.toURLParameters( params ).equals(
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

        assertTrue( new ParameterMap().with( sparseIdentity,
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
     * Tests that ParameterMap.fromStrings works as specified.
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

        assertTrue( ParameterMap.fromStrings( params,
                Arrays.asList( "John", "Major" ) ).get( surname ).equals(
                "Major" ) );

    }
}
