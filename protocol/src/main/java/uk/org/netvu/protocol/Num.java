package uk.org.netvu.protocol;

/**
 * Arithmetic operations for generic types.
 * 
 * @param <T>
 *        the type that the Num provides arithmetic operations for.
 */
abstract class Num<T>
{
    /**
     * Provides a Num instance for Integer.
     */
    public static final Num<Integer> integer = new Num<Integer>()
    {
        @Override
        public boolean ge( final Integer i, final Integer j )
        {
            return i >= j;
        }

        @Override
        public boolean le( final Integer i, final Integer j )
        {
            return i <= j;
        }

        @Override
        public Integer maxValue()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public Integer succ( final Integer i )
        {
            return i + 1;
        }

        @Override
        public Integer zero()
        {
            return 0;
        }
    };

    /**
     * Tests to see whether the first parameter is greater than or equal to the
     * second.
     * 
     * @param one
     *        the first parameter to test.
     * @param two
     *        the second parameter to test.
     * @return true if the first parameter is greater than or equal to the
     *         second, false otherwise.
     */
    public abstract boolean ge( T one, T two );

    /**
     * Tests to see whether the first parameter is less than or equal to the
     * second.
     * 
     * @param one
     *        the first parameter to test.
     * @param two
     *        the second parameter to test.
     * @return true if the first parameter is less than or equal to the second,
     *         false otherwise.
     */
    public abstract boolean le( T one, T two );

    /**
     * Gives the largest value that the type T can hold.
     * 
     * @return the largest value that the type T can hold.
     */
    public abstract T maxValue();

    /**
     * Gives the next value after the specified value. For example, succ(4) ==
     * 5.
     * 
     * @param t
     *        the value to find the next value after.
     * @return the next value after the specified value.
     */
    public abstract T succ( T t );

    /**
     * Gives zero for the type T.
     * 
     * @return zero for the type T.
     */
    public abstract T zero();
}
