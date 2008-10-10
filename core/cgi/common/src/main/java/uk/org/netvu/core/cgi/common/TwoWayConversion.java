package uk.org.netvu.core.cgi.common;

/**
 * A TwoWayConversion can convert between two types, A and B, but it can be a
 * partial mapping, which is represented by an Option in the results. A partial
 * mapping means that the conversion might fail, though to have control over how
 * we treat that failure, we represent it as an Option. For internal use only.
 * 
 * @param <A>
 *        the first of the two types that this TwoWayConversion can convert
 *        between.
 * @param <B>
 *        the second of the two types that this TwoWayConversion can convert
 *        between.
 */
public final class TwoWayConversion<T>
{
    /**
     * A partial Conversion that can convert Strings to Ts.
     */
    public final Conversion<String, Option<T>> conversionFromString;

    /**
     * A partial Conversion that can convert Ts to Strings.
     */
    public final Conversion<T, Option<String>> conversionToString;

    private TwoWayConversion(Conversion<String, Option<T>> conversionFromString, Conversion<T, Option<String>> conversionToString)
    {
        Checks.notNull(conversionFromString, conversionToString);
        this.conversionFromString = conversionFromString;
        this.conversionToString = conversionToString;
    }

    /**
     * Constructs a TwoWayConversion from a partial Conversion from Strings to Ts, and a partial Conversion from Ts to Strings.
     * @param conversionFromString the partial Conversion from Strings to Ts.
     * @param conversionToString the partial Conversion from Ts to Strings.
     * @param <T> the type that this TwoWayConversion can convert Strings to and from.
     * @return a TwoWayConversion that can convert Strings to Ts and Ts to Strings.
     */
    public static <T> TwoWayConversion<T> partial(final Conversion<String, Option<T>> conversionFromString,
                                                  final Conversion<T, Option<String>> conversionToString)
    {
        return new TwoWayConversion<T>(conversionFromString, conversionToString);
    }

    /**
     * Constructs a TwoWayConversion from a partial Conversion from Strings to Ts, and T's toString() method.
     * @param conversionFromString the partial Conversion from Strings to Ts.
     * @param <T> the type that this TwoWayConversion can convert Strings to and from.
     * @return a TwoWayConversion that can convert Strings to Ts and Ts to Strings.
     */
    public static <T> TwoWayConversion<T> convenientPartial(final Conversion<String, Option<T>> conversionFromString)
    {
        return partial(conversionFromString, andThenSome(Conversion.<T>objectToString()));
    }

    /**
     * Constructs a TwoWayConversion from a Conversion from Strings to Ts, and T's toString() method.
     * @param conversionFromString the Conversion from Strings to Ts.
     * @param <T> the type that this TwoWayConversion can convert Strings to and from.
     * @return a TwoWayConversion that can convert Strings to Ts and Ts to Strings.
     */
    public static <T> TwoWayConversion<T> convenientTotal(final Conversion<String, T> conversionFromString)
    {
        return partial(andThenSome(conversionFromString), andThenSome(Conversion.<T>objectToString()));
    }

    /**
     * Constructs a TwoWayConversion from a Conversion from Strings to Ts, and a Conversion from Ts to Strings.
     * @param conversionFromString the Conversion from Strings to Ts.
     * @param conversionToString the Conversion from Ts to Strings.
     * @param <T> the type that this TwoWayConversion can convert Strings to and from.
     * @return a TwoWayConversion that can convert Strings to Ts and Ts to Strings.
     */
    public static <T> TwoWayConversion<T> total(final Conversion<String, T> conversionFromString,
                                                final Conversion<T, String> conversionToString)
    {
        return partial(andThenSome(conversionFromString), andThenSome(conversionToString));
    }

    /**
     * A TwoWayConversion that converts between Strings containing decimal
     * integers and Java's Integer and vice-versa.
     */
    public static final TwoWayConversion<Integer> integer = convenientPartial( Conversion.stringToInt() );

    /**
     * A TwoWayConversion that converts between Strings and themselves with no
     * extra processing.
     */
    public static final TwoWayConversion<String> string = convenientTotal( Conversion.<String> identity() );

    /**
     * A TwoWayConversion that converts between Strings containing 'true' or
     * 'false' and Java's Boolean, and vice-versa.
     */
    public static final TwoWayConversion<Boolean> bool = convenientPartial( Conversion.stringToBoolean() );

    /**
     * A TwoWayConversion that converts between Strings containing hexadecimal
     * integers and Java's Long, and vice-versa.
     */
    public static final TwoWayConversion<Long> hexLong = partial(
                                                                         Conversion.hexStringToLong(),
                                                                         Option.someRef( Conversion.longToHexString() ) );

    /**
     * A TwoWayConversion that converts between Strings containing hexadecimal
     * integers and Java's Integer, and vice-versa.
     */
    public static final TwoWayConversion<Integer> hexInt = partial(
                                                                           Conversion.hexStringToInt(),
                                                                           Option.someRef( Conversion.intToHexString() ) );

    private static <T, R> Conversion<T, Option<R>> andThenSome(
            final Conversion<T, R> conversion )
    {
        return conversion.andThen( Option.<R> some() );
    }
}
