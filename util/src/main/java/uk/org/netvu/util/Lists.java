package uk.org.netvu.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

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
}
