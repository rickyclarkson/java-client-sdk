package uk.org.netvu.filter;

final class Filters
{
    private Filters()
    {
    }

    public static int bound(int x)
    {
        return x < 0 ? 0 : x > 255 ? 255 : x;
    }
}