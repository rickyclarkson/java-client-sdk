package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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

    private static final Parameter<UInt31, Option<UInt31>> time = Parameter.param(
            "time", "time since 1970", UInt31.fromString );
    private static final Parameter<UInt31, Option<UInt31>> range = Parameter.param(
            "range", "range to search", UInt31.fromString );

    @Test(expected = IllegalStateException.class)
    public void validatorFail()
    {
        new GenericBuilder( new Validator()
        {
            public boolean isValid( final GenericBuilder builder )
            {
                final Option<UInt31> oTime = builder.get( time );
                final Option<UInt31> oRange = builder.get( range );

                return oTime.isNone() || oRange.isNone() ? true
                        : oTime.get().toInt() + oRange.get().toInt() >= 0;
            }
        } ).with( time, new UInt31( 2000000000 ) ).with( range,
                new UInt31( 2000000000 ) );
    }

    @Test
    public void testIsDefault()
    {
        assertTrue( new GenericBuilder().isDefault( time ) );
        assertFalse( new GenericBuilder().with( time, new UInt31( 40 ) ).isDefault(
                time ) );
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

        assertTrue( builder.get( time ).get().toInt() == 10 );
        assertTrue( builder.get( range ).get().toInt() == 40 );

        assertTrue( builder.toURLParameters( params ).equals(
                "time=10&range=40" ) );
    }
}
