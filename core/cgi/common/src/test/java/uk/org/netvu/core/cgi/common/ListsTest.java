package uk.org.netvu.core.cgi.common;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests for the Lists utility class.
 */
public class ListsTest
{
    /**
     * Tests that Lists.remove works as specified.
     */
    @Test
    public void remove()
    {
        assertTrue( Lists.remove( Arrays.asList( 1, 2, 3 ), 3 ).equals(
                Arrays.asList( 1, 2 ) ) );
    }

    /**
     * Tests that Lists.reduceLeft works as expected.
     */
    @Test
    public void reduceLeft()
    {
        assertTrue( Lists.reduce( Arrays.asList( 1, 2, 3 ),
                new Reduction<Integer, Integer>()
                {
                    @Override
                    public Integer reduce( final Integer newValue,
                            final Integer original )
                    {
                        return original + newValue;
                    }
                } ) == 6 );
    }

    /**
     * Tests that Lists.zip works as specified.
     */
    @Test
    public void zip()
    {
        final List<Pair<Integer, Integer>> zipped = Lists.zip( Arrays.asList(
                1, 2, 3 ), Arrays.asList( 4, 5, 6 ) );

        assertTrue( Lists.reduce( Lists.map( zipped,
                new Conversion<Pair<Integer, Integer>, Integer>()
                {
                    @Override
                    public Integer convert( final Pair<Integer, Integer> t )
                    {
                        return t.first() * t.second();
                    }
                } ), new Reduction<Integer, Integer>()
        {
            @Override
            public Integer reduce( final Integer newValue,
                    final Integer original )
            {
                return original + newValue;
            }
        } ) == 1 * 4 + 2 * 5 + 3 * 6 );
    }

    /**
     * Tests that Lists.removeIndices works as expected.
     */
    @Test
    public void removeIndices()
    {
        assertTrue( Lists.removeIndices( Arrays.asList( 0, 1, 2, 3, 4 ), 1, 3 ).equals(
                Arrays.asList( 0, 2, 4 ) ) );
    }
}
