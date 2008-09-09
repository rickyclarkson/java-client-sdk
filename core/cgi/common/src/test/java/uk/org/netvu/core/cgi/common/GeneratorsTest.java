package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeneratorsTest
{
    @Test
    public void nonNegativeInts()
    {
        assertTrue( IterablesTest.testContract( Generators.nonNegativeInts( 0 ) ) );
    }
}
