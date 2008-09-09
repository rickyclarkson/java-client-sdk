package uk.org.netvu.core.cgi.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Random data generators for use in automated tests.
 */
public class Generators
{
    /**
     * A limit on iterations - control this to control how long tests that use
     * random data run for.
     */
    public static final int LIMIT = 500;

    /**
     * A limit on size - control this to control how large the data structures
     * that tests that use random data are.
     */
    public static final int SIZE_LIMIT = 250;

    /**
     * An infinite series of random Strings of maximum size SIZE_LIMIT.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of random Strings.
     */
    public static Iterable<String> strings( final Random random )
    {
        return new Iterable<String>()
        {
            public Iterator<String> iterator()
            {
                return new Iterator<String>()
                {
                    public boolean hasNext()
                    {
                        return true;
                    }

                    public String next()
                    {
                        final char[] chars = new char[random.nextInt( SIZE_LIMIT )];
                        for ( int a = 0; a < chars.length; a++ )
                        {
                            chars[a] = (char) ( random.nextInt( 'z' - 'a' ) + 'a' );
                        }
                        return new String( chars );
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }

                };
            }
        };
    }

    /**
     * An infinite series of List&lt;Integer&gt;s, each of maximum size
     * SIZE_LIMIT.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of List&lt;Integer&gt;s.
     */
    public static Iterator<List<Integer>> intLists( final Random random )
    {
        return new Iterator<List<Integer>>()
        {
            public boolean hasNext()
            {
                return true;
            }

            public List<Integer> next()
            {
                final int size = random.nextInt( Generators.SIZE_LIMIT );
                final List<Integer> list = new ArrayList<Integer>( size );

                for ( int a = 0; a < size; a++ )
                {
                    list.add( random.nextInt() );
                }

                return list;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * An infinite series of Strings as per strings(Random), with null at the
     * beginning.
     * 
     * @param random
     *        the random number generator to use.
     * @return an infinite series of Strings with null at the beginning.
     */
    public static Iterator<String> stringsAndNull( final Random random )
    {
        return Iterables.prepend( Arrays.asList( (String) null ),
                strings( random ) ).iterator();
    }

    public static Iterable<Integer> nonNegativeInts( final long seed )
    {
        return new Iterable<Integer>()
        {
            public Iterator<Integer> iterator()
            {
                return new Iterator<Integer>()
                {
                    Random random = new Random( seed );

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }

                    public Integer next()
                    {
                        return random.nextInt( Integer.MAX_VALUE );
                    }

                    public boolean hasNext()
                    {
                        return true;
                    }
                };
            }
        };
    }
}
