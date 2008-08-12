package uk.org.netvu.core.cgi.events;

abstract class Action<T>
{
    public abstract void invoke( T t );

    public static <T> Action<T> doNothing()
    {
        return new Action<T>()
        {
            @Override
            public void invoke( final T t )
            {
            }
        };
    }
}
