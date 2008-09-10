package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Unit tests for URLExtractor.
 */
public class URLExtractorTest
{
    /**
     * Tests that URLExtractor.queryName works as specified.
     */
    @Test
    public void testQueryName()
    {
        assertTrue( URLExtractor.queryName( "" ).equals( "" ) );
        assertTrue( URLExtractor.queryName( "foo" ).equals( "foo" ) );
        assertTrue( URLExtractor.queryName( "foo/bar" ).equals( "bar" ) );
        assertTrue( URLExtractor.queryName( "foo/bar.cgi?blah" ).equals(
                "bar.cgi" ) );
        assertTrue( URLExtractor.queryName( "?blah" ).equals( "" ) );
        assertTrue( URLExtractor.queryName( "foo?bar?" ).equals( "foo" ) );
    }

    /**
     * Tests that URLExtract.parameters works as specified.
     */
    @Test
    public void testParameters()
    {
        assertTrue( URLExtractor.parameters( "" ).size() == 1 );
        assertTrue( URLExtractor.parameters( "foo=bar" ).size() == 1 );
        assertTrue( URLExtractor.parameters( "foo=bar&baz=spam" ).size() == 2 );
        assertTrue( URLExtractor.parameters( "foo=bar&baz=spam" ).get( 1 ).equals(
                "baz=spam" ) );
        assertTrue( URLExtractor.parameters( "foo=\"bar=baz\"&spam=\"eggs\"" ).size() == 2 );
    }

    /**
     * Test that URLExtractor.nameValuePairs works as specified.
     */
    @Test
    public void testNameValuePairs()
    {
        assertTrue( Iterables.sequenceEqual( URLExtractor.nameValuePairs( "" ),
                new ArrayList<URLParameter>() ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.nameValuePairs( "foo=bar" ),
                Arrays.asList( new URLParameter( "foo", "bar" ) ) ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.nameValuePairs( "foo=bar&baz=spam" ),
                Arrays.asList( new URLParameter( "foo", "bar" ),
                        new URLParameter( "baz", "spam" ) ) ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.nameValuePairs( "foo=\"bar=baz\"&spam=\"eggs\"" ),
                Arrays.asList( new URLParameter( "foo", "\"bar=baz\"" ),
                        new URLParameter( "spam", "\"eggs\"" ) ) ) );
    }
}
