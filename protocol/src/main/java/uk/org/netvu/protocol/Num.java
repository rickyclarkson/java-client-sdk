package uk.org.netvu.protocol;

abstract class Num<T>
{
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

    public abstract boolean ge( T one, T two );

    public abstract boolean le( T one, T two );

    public abstract T maxValue();

    public abstract T succ( T t );

    public abstract T zero();
}
