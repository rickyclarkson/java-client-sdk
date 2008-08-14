package uk.org.netvu.core.cgi.events;

interface Action<T>
{
    void invoke( T t );
}
