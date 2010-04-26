package uk.org.netvu.util;

import fj.F;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Lists {
    public static <T> List<T> filter(List<T> list, F<T, Boolean> predicate) {
        final List<T> results = new ArrayList<T>();
        for (T t: list)
            if (predicate.f(t))
                results.add(t);
        return results;
    }

    public static <T, U> List<U> map(List<T> list, F<T, U> function) {
        final List<U> results = new ArrayList<U>();
        for (T t: list)
            results.add(function.f(t));
        return results;
    }

    public static <T> List<T> uniqueBy(List<T> list, F<T, ? extends Object> by) {
        final List<T> results = new ArrayList<T>();
        final Set<Object> set = new HashSet<Object>();
        for (T t: list) {
            final Object b = by.f(t);
            if (!set.contains(b))
                results.add(t);
            set.add(b);
        }
        return results;
    }
}
