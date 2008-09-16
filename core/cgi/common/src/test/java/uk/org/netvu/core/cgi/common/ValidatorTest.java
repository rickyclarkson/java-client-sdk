package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for Validator.
 */
public class ValidatorTest
{
    private static final Parameter<String, Option<String>> name = Parameter.param(
            "name", "foo", Conversion.<String> identity() );

    private static final Parameter<String, Option<String>> address = Parameter.param(
            "address", "foo", Conversion.<String> identity() );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( name );
            add( address );
        }
    };

    /**
     * Tests that making two parameters mutually exclusive doesn't prevent one
     * from being set.
     */
    @Test
    public void mutuallyExclusivePass()
    {
        new ParameterMap( Validator.mutuallyExclusive( params ) ).with( name,
                "bob" );
    }

    /**
     * Tests that mutually exclusive parameters really are mutually exclusive.
     */
    @Test(expected = IllegalStateException.class)
    public void mutuallyExclusiveFail()
    {
        new ParameterMap( Validator.mutuallyExclusive( params ) ).with( name,
                "bob" ).with( address, "here" );
    }
}
