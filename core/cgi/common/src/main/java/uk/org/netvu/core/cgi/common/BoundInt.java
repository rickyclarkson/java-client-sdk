package uk.org.netvu.core.cgi.common;

public class BoundInt
{
    final int lower;
    final int higher;
    public final int value;

    public BoundInt( final int value, final int lower, final int higher )
    {
        check( value, lower, higher );
        this.value = value;
        this.lower = lower;
        this.higher = higher;
    }

    private static void check( final int value, final int lower,
            final int higher )
    {
        if ( value < lower || value > higher )
        {
            throw new IllegalArgumentException( value + " is not between "
                    + lower + " and " + higher );
        }
    }

    public BoundInt newValue( final int newValue )
    {
        return new BoundInt( newValue, lower, higher );
    }
}
