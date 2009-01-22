package uk.org.netvu.protocol;

final class Arrays
{
    public static <T> boolean contains( final T[] array, final T element )
    {
        for ( final T t : array )
        {
            if ( t.equals( element ) )
            {
                return true;
            }
        }

        return false;
    }
}
