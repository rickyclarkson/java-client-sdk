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
public final class ParameterMapTest
{
    private final static Parameter<String, Option<String>> PARAM = Parameter.parameter(
            "blah", "blah", TwoWayConversion.string );

    /**
     * Tests that a value stored in a ParameterMap gets converted as per the
     * Parameter's rules.
     */
    @Test
    public void conversion()
    {
        assertTrue( Integer.parseInt( new ParameterMap().set( PARAM, "10" ).get(
                PARAM ).get() ) == 10 );
    }

    /**
     * Tests that setting the same once-only Parameter twice results in an
     * IllegalStateException.
     */
    @Test( expected = IllegalStateException.class )
    public void repeating()
    {
        new ParameterMap().set( PARAM, "10" ).set( PARAM, "10" );
    }

    private static final Parameter<Integer, Option<Integer>> TIME = Parameter.notNegative( Parameter.parameter(
            "time", "time since 1970", TwoWayConversion.integer ) );
    private static final Parameter<Integer, Option<Integer>> RANGE = Parameter.parameter(
            "range", "range to search", TwoWayConversion.integer );

    /**
     * Tests that an IllegalStateException is thrown when invalid parameter
     * values are given.
     */
    @Test( expected = IllegalStateException.class )
    public void validatorFail()
    {
        new ParameterMap( new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                final Option<Integer> oTime = parameterMap.get( TIME );
                final Option<Integer> oRange = parameterMap.get( RANGE );

                return oTime.fold( true, new Conversion<Integer, Boolean>()
                {
                    @Override
                    public Boolean convert( final Integer time )
                    {
                        return oRange.fold( true,
                                new Conversion<Integer, Boolean>()
                                {
                                    @Override
                                    public Boolean convert( final Integer range )
                                    {
                                        return time + range >= 0;
                                    }
                                } );
                    }

                } );
            }
        } ).set( TIME, 2000000000 ).set( RANGE, 2000000000 );
    }

    /**
     * Tests that ParameterMap.isDefault works as specified.
     */
    @Test
    public void isDefault()
    {
        assertTrue( new ParameterMap().isDefault( TIME ) );
        assertFalse( new ParameterMap().set( TIME, 40 ).isDefault( TIME ) );
    }

    /**
     * Tests that parsing a URL results in a ParameterMap holding the correct
     * values.
     */
    @Test
    public void fromURL()
    {
        // this is an anonymous intialiser - it is creating a new ArrayList and
        // adding values to it inline.
        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
        {
            {
                add( TIME );
                add( RANGE );
            }
        };

        final ParameterMap parameterMap = ParameterMap.fromURL(
                "time=10&range=40", params ).get();

        assertTrue( parameterMap.get( TIME ).get() == 10 );
        assertTrue( parameterMap.get( RANGE ).get() == 40 );

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
                "foo", "bar", Option.<String> some(),
                Option.<String, String> noneRef( "Conversion not supported" ) );

        // these are anonymous intialisers - each creates a new ArrayList and
        // adds values to it inline.
        assertTrue( new ParameterMap().set( sparseIdentity,
                new ArrayList<Pair<Integer, String>>()
                {
                    {
                        add( Pair.pair( 4, "bar" ) );
                        add( Pair.pair( 5, "foo" ) );
                    }
                } ).set( sparseIdentity, new ArrayList<Pair<Integer, String>>()
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
        final Parameter<String, String> name = Parameter.parameterWithDefault(
                "name", "foo", "Bob", TwoWayConversion.string );
        final Parameter<String, String> surname = Parameter.parameterWithDefault(
                "surname", "foo", "Hope", TwoWayConversion.string );

        // this is an anonymous intialiser - it is creating a new ArrayList and
        // adding values to it inline.
        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
        {
            {
                add( name );
                add( surname );
            }
        };

        assertTrue( ParameterMap.fromStrings( params,
                Arrays.asList( "John", "Major" ) ).get().get( surname ).equals(
                "Major" ) );

    }
}
