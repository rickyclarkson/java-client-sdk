package uk.org.netvu.core.cgi.common;

public interface Action<T>
{
    void invoke( T t );
}
