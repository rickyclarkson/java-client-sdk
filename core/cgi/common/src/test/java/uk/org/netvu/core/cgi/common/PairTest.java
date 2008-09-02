package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PairTest
{
    @Test
    public void testPair()
    {
        assertTrue( Pair.pair( 3, 10.0 ).accept(
                new PairVisitor<Integer, Double, Double>()
                {

                    public Double visit( final Integer t, final Double u )
                    {
                        return t + u;
                    }
                } ) == 13 );
    }

    @Test(expected = IllegalStateException.class)
    public void testNoFlatten()
    {
        Pair.<Integer> noFlatten().convert( Pair.pair( 3, 10 ) );
    }

    @Test
    public void testFirst()
    {
        assertTrue( Pair.pair( 3, 4 ).first() == 3 );
    }

    @Test
    public void testSecond()
    {
        assertTrue( Pair.pair( 3, 4 ).second() == 4 );
    }

    @Test
    public void equality()
    {
        assertTrue( Pair.pair( 3, 4 ).equals( Pair.pair( 3, 4 ) ) );
    }

}
