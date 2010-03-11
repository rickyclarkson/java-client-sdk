package uk.org.netvu.util;

import java.util.Locale;

public class Strings {
    public static boolean equalsIgnoreCase(String one, String two) {
        return one == null ? two == null : two != null && one.toLowerCase(Locale.ENGLISH).equals(two.toLowerCase(Locale.ENGLISH));
    }
}
