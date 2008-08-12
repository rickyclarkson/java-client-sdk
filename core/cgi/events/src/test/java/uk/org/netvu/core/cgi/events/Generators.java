package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.events.Iterables.prepend;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Random data generators for use in automated tests.
 */
class Generators
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
     * An infinite series of Throwables.
     * 
     * @param random
     *        the random number generator to use.
     */
    public static Iterator<Throwable> throwables( final Random random )
    {
        return new Iterator<Throwable>()
        {
            public boolean hasNext()
            {
                return true;
            }

            @SuppressWarnings("serial")
            public Throwable next()
            {
                switch ( random.nextInt( 10 ) )
                {
                    case 0:
                        return new NullPointerException();
                    case 1:
                        return new IllegalArgumentException();
                    case 2:
                        return new RuntimeException( new IOException() );
                    case 3:
                        return new IllegalStateException( "message" );
                    case 4:
                        return new Error();
                    case 5:
                        return new Exception();
                    case 6:
                        return new Throwable();
                    case 7:
                        return new IOException();
                    case 8:
                        return new ThreadDeath();
                    case 9:
                        return new Exception()
                        {
                        };
                }
                throw null;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * An infinite series of Objects.
     */
    public static Iterable<Object> objects()
    {
        return new Iterable<Object>()
        {
            public Iterator<Object> iterator()
            {
                return new Iterator<Object>()
                {
                    public boolean hasNext()
                    {
                        return true;
                    }

                    public Object next()
                    {
                        return new Object();
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
     */
    public static Iterator<String> stringsAndNull( final Random random )
    {
        return Iterables.prepend( Arrays.asList( (String) null ),
                strings( random ) ).iterator();
    }

    /**
     * An infinite series of Objects as per objects(Random), with null at the
     * beginning.
     * 
     * @param random
     *        the random number generator to use.
     */
    public static Iterator<Object> objectsAndNull( final Random random )
    {
        return prepend( Arrays.asList( (Object) null ), Generators.objects() ).iterator();
    }

    /**
     * An infinite series of Lists of a specified type T.
     * 
     * @param generator
     *        an infinite series of the specified type T.
     * @param random
     *        the random number generator to use.
     */
    public static <T> Iterator<List<T>> lists( final Iterator<T> generator,
            final Random random )
    {
        return new Iterator<List<T>>()
        {
            public boolean hasNext()
            {
                return true;
            }

            public List<T> next()
            {
                final int size = random.nextInt( SIZE_LIMIT );
                final List<T> list = new ArrayList<T>( size );

                for ( int a = 0; a < size; a++ )
                {
                    list.add( generator.next() );
                }

                return list;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static InputStream oneRandomByteThenAnIOException(
            final Random random )
    {
        return new InputStream()
        {
            boolean first = true;

            @Override
            public int read() throws IOException
            {
                if ( first )
                {
                    first = false;
                    return random.nextInt( 256 );
                }
                throw new IOException();
            }

        };
    }
}
