package uk.org.netvu.data;

interface Effect<T>
{
  void apply(T t);
}