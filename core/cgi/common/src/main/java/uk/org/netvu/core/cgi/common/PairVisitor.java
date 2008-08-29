package uk.org.netvu.core.cgi.common;

public interface PairVisitor<T, U, V>
{
    V visit( T t, U u );
}
