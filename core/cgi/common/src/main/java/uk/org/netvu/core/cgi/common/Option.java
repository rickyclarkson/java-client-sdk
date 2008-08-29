package uk.org.netvu.core.cgi.common;

public abstract class Option<T>
{
    private Option()
    {
    }

    public abstract T getOrElse( T reserve );

    public static final class Some<T> extends Option<T>
    {
        private final T t;

        public Some( final T t )
        {
            this.t = t;
        }

        @Override
        public T getOrElse( final T reserve )
        {
            return t;
        }

        @Override
        public boolean isNone()
        {
            return false;
        }

        @Override
        public T get()
        {
            return t;
        }

        @Override
        public String toString()
        {
            return t.toString();
        }
    }

    public static final class None<T> extends Option<T>
    {
        @Override
        public T getOrElse( final T reserve )
        {
            return reserve;
        }

        @Override
        public boolean isNone()
        {
            return true;
        }

        @Override
        public T get()
        {
            throw new IllegalStateException();
        }
    }

    public abstract boolean isNone();

    public abstract T get();
}
