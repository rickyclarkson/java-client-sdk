package uk.org.netvu.core.cgi.common;

public interface Constraint<T>
{
    boolean isValid( GenericBuilder builder, Parameter<T> parameter, T newValue );
}
