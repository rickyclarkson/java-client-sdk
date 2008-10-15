package uk.org.netvu.core.cgi.common;

/**
 * For internal use only! A StringConversion can convert between Strings and a
 * given type, T, though the conversion can be partial, which is represented by
 * an Option in the results. A partial mapping means that the conversion might
 * fail, though to have control over how we treat that failure, we represent it
 * as an Option. The terms 'partial' and 'total' as used in this class come from
 * their use in mathematics, where a partial function is one that does not
 * compute for all values in the domain. Ignoring imaginary numbers, one partial
 * function is the square root of a number - it is only valid for non-negative
 * numbers. A total function is one that is valid for all values in its domain
 * (usually this is just called a function).
 * 
 * @param <T>
 *        the type that this StringConversion can convert Strings to and from.
 */
public final class StringConversion<T>
{
    /**
     * A StringConversion that converts between Strings containing 'true' or
     * 'false' and Java's Boolean, and vice-versa. It is case-insensitive.
     * 
     * @return a StringConversion that converts between Strings containing
     *         'true' or 'false' and Java's Boolean, and vice versa.
     */
    public static StringConversion<Boolean> bool()
    {
        return convenientPartial( Conversion.getStringToBooleanConversion() );
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
     * @throws NullPointerException
     *         if conversionFromString is null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientPartial(
            final Conversion<String, Option<T>> conversionFromString )
    {
        return partial(
                conversionFromString,
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
     * @throws NullPointerException
     *         if conversionFromString is null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientTotal(
            final Conversion<String, T> conversionFromString )
    {
        return partial(
                andThenToFullOption( conversionFromString ),
                andThenToFullOption( Conversion.<T> getObjectToStringConversion() ) );
    }

    /**
     * A StringConversion that converts between Strings containing hexadecimal
     * integers and Java's Integer, and vice-versa.
     * 
     * @return a StringConversion that converts between Strings containing
     *         hexadecimal integers and Java's Integer, and vice-versa.
     */
    public static StringConversion<Integer> getHexToIntStringConversion()
    {
        return partial(
                Conversion.getHexStringToIntConversion(),
                Option.toPartialConversion( Conversion.getIntToHexStringConversion() ) );
    }

    /**
     * A StringConversion that converts between Strings containing hexadecimal
     * integers and Java's Long, and vice-versa.
     * 
     * @return a StringConversion that converts between Strings containing
     *         hexadecimal integers and Java's Long, and vice-versa.
     */
    public static StringConversion<Long> getHexToLongStringConversion()
    {
        return partial(
                Conversion.getHexStringToLongConversion(),
                Option.toPartialConversion( Conversion.getLongToHexStringConversion() ) );
    }

    /**
     * A StringConversion that converts between Strings containing decimal
     * integers and Java's Integer and vice-versa.
     * 
     * @return a StringConversion that converts between Strings containing
     *         decimal integers and Java's Integer and vice-versa.
     */
    public static StringConversion<Integer> integer()
    {
        return convenientPartial( Conversion.getStringToIntConversion() );
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
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
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
     * A StringConversion that converts between Strings and themselves with no
     * extra processing.
     * 
     * @return a StringConversion that converts between Strings and themselves
     *         with no extra processing.
     */
    public static StringConversion<String> string()
    {
        return convenientTotal( Conversion.<String> getIdentityConversion() );
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
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
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

    /**
     * Given a Conversion from T to R, produces a partial Conversion from T to R
     * that always gives a full Option.
     * 
     * @param <T>
     *        the type that this Conversion will convert from.
     * @param <R>
     *        the type that this Conversion will convert to.
     * @param conversion
     *        the total Conversion to use.
     * @return a partial Conversion from T to R that always gives a full Option.
     * @throws NullPointerException
     *         if conversion is null.
     */
    private static <T, R> Conversion<T, Option<R>> andThenToFullOption(
            final Conversion<T, R> conversion )
    {
        return conversion.andThen( Option.<R> getConversionToFullOption() );
    }

    /**
     * The partial Conversion to use to convert Strings to Ts. This is never
     * null.
     */
    private final Conversion<String, Option<T>> conversionFromString;

    /**
     * The partial Conversion to use to convert Ts to Strings. This is never
     * null.
     */
    private final Conversion<T, Option<String>> conversionToString;

    /**
     * Constructs a StringConversion.
     * 
     * @param conversionFromString
     *        the partial Conversion to use to convert Strings to Ts.
     * @param conversionToString
     *        the partial Conversion to use to convert Ts to Strings.
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
     */
    private StringConversion(
            final Conversion<String, Option<T>> conversionFromString,
            final Conversion<T, Option<String>> conversionToString )
    {
        CheckParameters.areNotNull( conversionFromString, conversionToString );
        this.conversionFromString = conversionFromString;
        this.conversionToString = conversionToString;
    }

    /**
     * Converts a String to a T, returning the result in an Option that contains
     * a T if the conversion succeeds, and nothing if it fails.
     * 
     * @param string
     *        the String to convert to a T.
     * @throws NullPointerException
     *         if string is null.
     * @return An Option containing a T if the conversion succeeded, and an
     *         empty Option otherwise.
     */
    public Option<T> fromString( final String string )
    {
        return conversionFromString.convert( string );
    }

    /**
     * Converts a T to a String, returning the result in an Option that contains
     * a String if the conversion succeeds, and nothing if it fails.
     * 
     * @param t
     *        the T to convert to a String.
     * @return a String representation of the T in an Option if the conversion
     *         succeeded, and an empty Option otherwise.
     */
    public Option<String> toString( final T t )
    {
        return conversionToString.convert( t );
    }
}
