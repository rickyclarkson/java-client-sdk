package uk.org.netvu.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Lists {
    public static <T> List<T> filter(List<T> list, Function<T, Boolean> predicate) {
        final List<T> results = new ArrayList<T>();
        for (T t: list)
            if (predicate.apply(t)) 
                results.add(t);
        return results;
    }

    public static <T, U> List<U> map(List<T> list, Function<T, U> function) {
        final List<U> results = new ArrayList<U>();
        for (T t: list)
            results.add(function.apply(t));
        return results;
    }

    public static <T> List<T> unique(List<T> list)
    {
        return new ArrayList<T>(new LinkedHashSet<T>(list));
    }

    public static <T> List<T> uniqueBy(List<T> list, Function<T, ? extends Object> by) {
        final List<T> results = new ArrayList<T>();
        final Set<Object> set = new HashSet<Object>();
        for (T t: list) {
            final Object b = by.apply(t);
            if (!set.contains(b))
                results.add(t);
            set.add(b);
        }
        return results;
    }
}
