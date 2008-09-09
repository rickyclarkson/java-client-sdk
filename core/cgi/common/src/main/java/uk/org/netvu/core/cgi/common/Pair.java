package uk.org.netvu.core.cgi.common;

public final class Pair<T, U>
{
    private final T t;
    private final U u;

    public Pair( final T t, final U u )
    {
        this.t = t;
        this.u = u;
    }

    public static <T, U> Pair<T, U> pair( final T t, final U u )
    {
        return new Pair<T, U>( t, u );
    }

    public <V> V accept( final PairVisitor<T, U, V> visitor )
    {
        return visitor.visit( t, u );
    }

    public static <T> Conversion<Pair<T, T>, T> noFlatten()
    {
        return new Conversion<Pair<T, T>, T>()
        {
            @Override
            public T convert( final Pair<T, T> t )
            {
                throw new IllegalStateException();
            }
        };
    }

    public T first()
    {
        return t;
    }

    public U second()
    {
        return u;
    }

    public static <T, U> Conversion<Pair<T, U>, U> getSecond()
    {
        return new Conversion<Pair<T, U>, U>()
        {
            @Override
            public U convert( final Pair<T, U> t )
            {
                return t.second();
            }
        };
    }
}
