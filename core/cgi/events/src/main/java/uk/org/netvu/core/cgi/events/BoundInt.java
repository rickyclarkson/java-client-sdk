package uk.org.netvu.core.cgi.events;

class BoundInt
{
    final int value;
    final int lower;
    final int higher;

    public BoundInt( final int value, final int lower, final int higher )
    {
        if ( value < lower || value > higher )
        {
            throw new IllegalArgumentException( value + " is not between "
                    + lower + " and " + higher );
        }

        this.value = value;
        this.lower = lower;
        this.higher = higher;
    }

    public BoundInt newValue( final int newValue )
    {
        return new BoundInt( newValue, lower, higher );
    }
}
