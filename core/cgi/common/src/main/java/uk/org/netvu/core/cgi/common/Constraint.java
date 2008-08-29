package uk.org.netvu.core.cgi.common;

public interface Constraint<T>
{
    boolean isValid_delete( GenericBuilder builder, Parameter<T, T> parameter, T newValue );
}
