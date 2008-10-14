package uk.org.netvu.core.cgi.common;

/**
 * For internal use only!
 * A StringConversion can convert between Strings and a given type, T, though
 * the conversion can be partial, which is represented by an Option in the
 * results. A partial mapping means that the conversion might fail, though to
 * have control over how we treat that failure, we represent it as an Option.
 * 
 * @param <T>
 *        the type that this StringConversion can convert Strings to and from.
 */
public final class StringConversion<T>
{
    /**
     * A StringConversion that converts between Strings containing decimal
     * integers and Java's Integer and vice-versa.
     */
    public static StringConversion<Integer> integer()
    {
        return convenientPartial( Conversion.getStringToIntConversion() );
    }

    /**
     * A StringConversion that converts between Strings and themselves with no
     * extra processing.
     */
    public static StringConversion<String> string()
    {
        return convenientTotal( Conversion.<String> getIdentityConversion() );
    }

    /**
     * A StringConversion that converts between Strings containing 'true' or
     * 'false' and Java's Boolean, and vice-versa.
     */
    public static StringConversion<Boolean> bool()
    {
        return convenientPartial( Conversion.getStringToBooleanConversion() );
    }

    /**
     * A StringConversion that converts between Strings containing hexadecimal
     * integers and Java's Long, and vice-versa.
     */
    public static StringConversion<Long> hexLong()
    {
        return partial(Conversion.getHexStringToLongConversion(),
                       Option.toPartialConversion( Conversion.getLongToHexStringConversion() ) );
    }

    /**
     * A StringConversion that converts between Strings containing hexadecimal
     * integers and Java's Integer, and vice-versa.
     */
    public static StringConversion<Integer> hexInt()
    {
        return partial(Conversion.getHexStringToIntConversion(),
                       Option.toPartialConversion( Conversion.getIntToHexStringConversion() ) );
    }

    /**
     * Constructs a StringConversion from a partial Conversion from Strings to
     * Ts, and T's toString() method.
     * 
     * @param conversionFromString
     *        the partial Conversion from Strings to Ts.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientPartial(
            final Conversion<String, Option<T>> conversionFromString )
    {
        return partial( conversionFromString,
                andThenToFullOption( Conversion.<T> getObjectToStringConversion() ) );
    }

    /**
     * Constructs a StringConversion from a Conversion from Strings to Ts, and
     * T's toString() method.
     * 
     * @param conversionFromString
     *        the Conversion from Strings to Ts.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientTotal(
            final Conversion<String, T> conversionFromString )
    {
        return partial( andThenToFullOption( conversionFromString ),
                andThenToFullOption( Conversion.<T> getObjectToStringConversion() ) );
    }

    /**
     * Constructs a StringConversion from a partial Conversion from Strings to
     * Ts, and a partial Conversion from Ts to Strings.
     * 
     * @param conversionFromString
     *        the partial Conversion from Strings to Ts.
     * @param conversionToString
     *        the partial Conversion from Ts to Strings.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> partial(
            final Conversion<String, Option<T>> conversionFromString,
            final Conversion<T, Option<String>> conversionToString )
    {
        return new StringConversion<T>( conversionFromString,
                conversionToString );
    }

    /**
     * Constructs a StringConversion from a Conversion from Strings to Ts, and a
     * Conversion from Ts to Strings.
     * 
     * @param conversionFromString
     *        the Conversion from Strings to Ts.
     * @param conversionToString
     *        the Conversion from Ts to Strings.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> total(
            final Conversion<String, T> conversionFromString,
            final Conversion<T, String> conversionToString )
    {
        return partial( andThenToFullOption( conversionFromString ),
                andThenToFullOption( conversionToString ) );
    }

    private static <T, R> Conversion<T, Option<R>> andThenToFullOption(
            final Conversion<T, R> conversion )
    {
        return conversion.andThen( Option.<R> getConversionToFullOption() );
    }

    private final Conversion<String, Option<T>> conversionFromString;

    private final Conversion<T, Option<String>> conversionToString;

    private StringConversion(
            final Conversion<String, Option<T>> conversionFromString,
            final Conversion<T, Option<String>> conversionToString )
    {
        CheckParameters.areNotNull( conversionFromString, conversionToString );
        this.conversionFromString = conversionFromString;
        this.conversionToString = conversionToString;
    }

    /**
     * Converts Strings to Ts, returning the result in an Option that contains a
     * T if the conversion succeeds, and nothing if it fails.
     */
    public Option<T> fromString( final String string )
    {
        return conversionFromString.convert( string );
    }

    /**
     * Converts Ts to Strings, returning the result in an Option that contains a
     * String if the conversion succeeds, and nothing if it fails.
     */
    public Option<String> toString( final T t )
    {
        return conversionToString.convert( t );
    }
}
