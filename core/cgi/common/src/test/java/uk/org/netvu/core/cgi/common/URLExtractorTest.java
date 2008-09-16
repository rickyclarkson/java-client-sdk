package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

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
    public void queryName()
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
    public void parameters()
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
    public void nameValuePairs()
    {
        assertTrue( URLExtractor.nameValuePairs( "" ).isEmpty() );
        assertTrue( URLExtractor.nameValuePairs( "foo=bar" ).equals(
                Arrays.asList( new URLParameter( "foo", "bar" ) ) ) );
        assertTrue( URLExtractor.nameValuePairs( "foo=bar&baz=spam" ).equals(
                Arrays.asList( new URLParameter( "foo", "bar" ),
                        new URLParameter( "baz", "spam" ) ) ) );
        assertTrue( URLExtractor.nameValuePairs(
                "foo=\"bar=baz\"&spam=\"eggs\"" ).equals(
                Arrays.asList( new URLParameter( "foo", "\"bar=baz\"" ),
                        new URLParameter( "spam", "\"eggs\"" ) ) ) );
    }
}
