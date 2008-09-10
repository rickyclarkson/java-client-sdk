package uk.org.netvu.core.cgi.variable;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for VariableCGI.
 */
public class VariableCGITest
{
    /**
     * Tests that parsing a variable.cgi query works.
     */
    @Test
    public void testParsing()
    {
        assertTrue( VariableCGI.fromString(
                "foo/variable.cgi?variable=c_title[]" ).getVariable() == Variable.C_TITLE );
        assertTrue( VariableCGI.fromString(
                "foo/variable.cgi?variable=c_title[]&type=http" ).getType() == VariableType.HTTP );
    }

    /**
     * Tests that building a URL works.
     */
    @Test
    public void testBuildingURL()
    {
        final String[] strings = { "/variable.cgi?variable=c_title[]",
            "/variable.cgi?variable=c_title[]&type=include" };

        for ( final String s : strings )
        {
            assertTrue( VariableCGI.fromString( s ).toString().equals( s ) );
        }
    }

    /**
     * Tests that building a VariableCGI programmatically works.
     */
    @Test
    public void testBuildingVariableCGI()
    {
        final VariableCGI cgi = new VariableCGI.Builder().type(
                VariableType.INCLUDE ).variable( Variable.C_TITLE ).build();

        assertTrue( cgi.getType() == VariableType.INCLUDE );
        assertTrue( cgi.getVariable() == Variable.C_TITLE );
    }

    /**
     * Tests that building a VariableCGI with no variable parameter causes an
     * IllegalStateException.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidBuild()
    {
        new VariableCGI.Builder().build();
    }
}
