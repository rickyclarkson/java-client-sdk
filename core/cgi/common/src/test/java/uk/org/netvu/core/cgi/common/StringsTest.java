package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class StringsTest
{
    @Test
    public void fromLast()
    {
        assertTrue( Strings.fromLast( 'l', "hello" ).equals( "o" ) );
        assertTrue( Strings.fromLast( 'l', "hellooo" ).equals( "ooo" ) );
        assertTrue( Strings.fromLast( 'm', "hello" ).equals( "hello" ) );
    }

    @Test
    public void split()
    {
        assertTrue( Arrays.asList( Strings.split( "oh, my,word" ) ).equals(
                Arrays.asList( "oh", "my", "word" ) ) );
    }
}
