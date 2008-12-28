package uk.org.netvu.protocol;


import java.util.List;
import static java.util.Arrays.asList;

public final class IPAddress
{
  public final int rawValue;

  public IPAddress(int rawValue)
  {
    this.rawValue = rawValue;
  }

  public String toString()
  {
    return (rawValue >>> 24) + "." + ((rawValue & 0x00FF0000) >>> 16) + "." + ((rawValue & 0x0000FF00) >>> 8) + "." + ((rawValue & 0xFF));
  }

  public static Option<IPAddress> fromString(String s) { List<String> parts = asList(s.split("\\."));
        
    List<Integer> intParts = Lists.map(parts, new Function<String, Integer>() { public Integer apply(String s) { return Integer.parseInt(s); } });
    intParts = Lists.filter(intParts, new Function<Integer, Boolean>() { public Boolean apply(Integer i) { return i >= 0 && i <= 255; } });
    
    if (intParts.size() != 4)
      return Option.getEmptyOption(s+ " is not a valid IP address.");

    return Option.getFullOption(new IPAddress(intParts.get(0) << 24 | intParts.get(1) << 16 | intParts.get(2) << 8 | intParts.get(3)));
   }
public static final Function<String, Option<IPAddress>> fromString = new Function<String, Option<IPAddress>>() { public Option<IPAddress> apply(String s) { return fromString(s); } };
}