package uk.org.netvu.core.cgi.events;

interface Conversion<T, R>
{
    R convert( T t );
}
