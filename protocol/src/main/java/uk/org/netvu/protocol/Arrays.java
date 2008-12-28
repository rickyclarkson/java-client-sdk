package uk.org.netvu.protocol;

public final class Arrays
{
  public static <T> boolean contains(T[] array, T element)
  {
    for (final T t: array)
      if (t.equals(element))
        return true;

    return false;
  }
}