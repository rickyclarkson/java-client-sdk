package uk.org.netvu.core.cgi.common;

public abstract class Reduction<T, R>
{
    public abstract R reduce( T newValue, R original );
}
