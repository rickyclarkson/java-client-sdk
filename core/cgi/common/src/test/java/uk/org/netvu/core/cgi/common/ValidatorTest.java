package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for Validator.
 */
public class ValidatorTest
{
    private static final Parameter<String, Option<String>> NAME = Parameter.param(
            "name", "foo", Option.<String> some() );

    private static final Parameter<String, Option<String>> ADDRESS = Parameter.param(
            "address", "foo", Option.<String> some() );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( NAME );
            add( ADDRESS );
        }
    };

    /**
     * Tests that making two parameters mutually exclusive doesn't prevent one
     * from being set.
     */
    @Test
    public void mutuallyExclusivePass()
    {
        new ParameterMap( Validator.mutuallyExclusive( params ) ).set( NAME,
                "bob" );
    }

    /**
     * Tests that mutually exclusive parameters really are mutually exclusive.
     */
    @Test(expected = IllegalStateException.class)
    public void mutuallyExclusiveFail()
    {
        new ParameterMap( Validator.mutuallyExclusive( params ) ).set( NAME,
                "bob" ).set( ADDRESS, "here" );
    }
}
