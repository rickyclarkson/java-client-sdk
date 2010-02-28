package uk.org.netvu.util;

import java.util.Locale;

public class Strings {
    public static boolean equalsIgnoreCase(String one, String two) {
        return one.toLowerCase(Locale.ENGLISH).equals(two.toLowerCase(Locale.ENGLISH));
    }

    public static int compareToIgnoreCase(String one, String two) {
        return one.toLowerCase(Locale.ENGLISH).compareTo(two.toLowerCase(Locale.ENGLISH));
    }
}
