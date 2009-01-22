package uk.org.netvu.protocol;

/**
 * Utility operations on arrays.
 */
final class Arrays
{
    /**
     * Identifies whether the specified array contains the specified element.
     * 
     * @param <T>
     *        the type of the array.
     * @param array
     *        the array to search.
     * @param element
     *        the element to search for.
     * @return true if the array contains the element, false otherwise.
     */
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
