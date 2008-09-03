package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.Test;

public final class GenericBuilderTest
{
    Parameter<String, Option<String>> param = Parameter.param( "blah", "blah",
            Conversion.<String> identity() );

    @Test
    public void conversion()
    {
        assertTrue( Integer.parseInt( new GenericBuilder().with( param, "10" ).get(
                param ).get() ) == 10 );
    }

    @Test(expected = IllegalStateException.class)
    public void repeating()
    {
        new GenericBuilder().with( param, "10" ).with( param, "10" );
    }

    private static final Parameter<Integer, Option<Integer>> time = Parameter.notNegative( Parameter.param(
            "time", "time since 1970", Conversion.stringToInt ) );
    private static final Parameter<Integer, Option<Integer>> range = Parameter.param(
            "range", "range to search", Conversion.stringToInt );

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

    @Test
    public void testIsDefault()
    {
        assertTrue( new GenericBuilder().isDefault( time ) );
        assertFalse( new GenericBuilder().with( time, 40 ).isDefault( time ) );
    }

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

    @Test
    public void sparseArray()
    {
        final Parameter<List<Pair<Integer, String>>, TreeMap<Integer, String>> sparseIdentity = Parameter.sparseArrayParam(
                "foo", "bar", Conversion.<String> identity(), URLBuilder.encode );

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
}
