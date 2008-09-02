package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class URLExtractorTest
{
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

    @Test
    public void testKeyValuePairs()
    {
        assertTrue( Iterables.sequenceEqual( URLExtractor.keyValuePairs( "" ),
                new ArrayList<Pair<String, String>>() ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.keyValuePairs( "foo=bar" ),
                new ArrayList<Pair<String, String>>()
                {
                    {
                        add( Pair.pair( "foo", "bar" ) );
                    }
                } ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.keyValuePairs( "foo=bar&baz=spam" ),
                new ArrayList<Pair<String, String>>()
                {
                    {
                        add( Pair.pair( "foo", "bar" ) );
                        add( Pair.pair( "baz", "spam" ) );
                    }
                } ) );
        assertTrue( Iterables.sequenceEqual(
                URLExtractor.keyValuePairs( "foo=\"bar=baz\"&spam=\"eggs\"" ),
                new ArrayList<Pair<String, String>>()
                {
                    {
                        add( Pair.pair( "foo", "\"bar=baz\"" ) );
                        add( Pair.pair( "spam", "\"eggs\"" ) );
                    }
                } ) );
    }

}
