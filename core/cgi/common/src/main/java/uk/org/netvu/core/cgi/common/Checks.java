package uk.org.netvu.core.cgi.common;

public final class Checks
{
    private Checks()
    {
    }

    public static void notNull(Object... args)
    {
        int a=0;
        for (Object o: args)
        {
            if (o == null)
                throw new NullPointerException("Value number "+a+" was null.");
            a++;
        }
    }
}