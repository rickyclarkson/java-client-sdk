package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class StringsTest
{
    @Test
    public void split()
    {
        assertTrue( Arrays.asList( Strings.split( "oh, my,word" ) ).equals(
                Arrays.asList( "oh", "my", "word" ) ) );
    }

    @Test
    public void splitIgnoringQuotedSections()
    {
        assertTrue( Strings.splitIgnoringQuotedSections( "one,two,three", ',' ).size() == 3 );
        assertTrue( Strings.splitIgnoringQuotedSections(
                "one,two \" and a third, a fourth and perhaps, \",five", ',' ).size() == 3 );
    }

    @Test
    public void afterFirstLeniently()
    {
        assertTrue( Strings.afterFirstLeniently( "hello", "-" ).equals( "hello" ) );
        assertTrue( Strings.afterFirstLeniently( "", "-" ).equals( "" ) );
        assertTrue( Strings.afterFirstLeniently( "hello - world", "-" ).equals(
                " world" ) );
        assertTrue( Strings.afterFirstLeniently( "hello - world - spam", "-" ).equals(
                " world - spam" ) );
    }
}
