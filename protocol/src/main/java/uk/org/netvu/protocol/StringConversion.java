package uk.org.netvu.protocol;

import uk.org.netvu.util.CheckParameters;

/**
 * A StringConversion can convert between Strings and a given type, T, though
 * the conversion can be partial, which is represented by an Option in the
 * results. A partial mapping means that the conversion might fail, though to
 * have control over how we treat that failure, we represent it as an Option.
 * The terms 'partial' and 'total' as used in this class come from their use in
 * mathematics, where a partial function is one that does not compute for all
 * values in the domain. Ignoring imaginary numbers, one partial function is the
 * square root of a number - it is only valid for non-negative numbers. A total
 * function is one that is valid for all values in its domain (usually this is
 * just called a function).
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
        return convenientPartial( Function.getStringToBooleanFunction() );
    }

    /**
     * Constructs a StringConversion from a partial Function from Strings to Ts,
     * and T's toString() method.
     * 
     * @param conversionFromString
     *        the partial Function from Strings to Ts.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @throws NullPointerException
     *         if conversionFromString is null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientPartial( final Function<String, Option<T>> conversionFromString )
    {
        return partial( conversionFromString, andThenToFullOption( Function.<T> getObjectToStringFunction() ) );
    }

    /**
     * Constructs a StringConversion from a Function from Strings to Ts, and T's
     * toString() method.
     * 
     * @param conversionFromString
     *        the Function from Strings to Ts.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @throws NullPointerException
     *         if conversionFromString is null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> convenientTotal( final Function<String, T> conversionFromString )
    {
        return partial( andThenToFullOption( conversionFromString ),
                andThenToFullOption( Function.<T> getObjectToStringFunction() ) );
    }

    /**
     * A StringConversion that converts between Strings containing hexadecimal
     * integers and Java's Integer, and vice-versa.
     * 
     * @return a StringConversion that converts between Strings containing
     *         hexadecimal integers and Java's Integer, and vice-versa.
     */
    public static StringConversion<Integer> hexInt()
    {
        return partial( Function.getHexStringToIntFunction(),
                Option.toPartialFunction( Function.getIntToHexStringFunction() ) );
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
        return partial( Function.getHexStringToLongFunction(),
                Option.toPartialFunction( Function.getLongToHexStringFunction() ) );
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
        return convenientPartial( Function.getStringToIntFunction() );
    }

    /**
     * A StringConversion that always yields an empty Option in either
     * direction.
     * 
     * @param <T>
     *        the type to convert Strings to and from.
     * @param reason
     *        the reason the Option is empty.
     * @return a StringConversion that always yields an empty Option in either
     *         direction.
     * @throws NullPointerException
     *         if reason is null.
     */
    public static <T> StringConversion<T> none( final String reason )
    {
        CheckParameters.areNotNull( reason );
        return new StringConversion<T>( Option.<String, T> getFunctionToEmptyOption( reason ),
                Option.<T, String> getFunctionToEmptyOption( reason ) );
    }

    /**
     * Constructs a StringConversion from a partial Function from Strings to Ts,
     * and a partial Function from Ts to Strings.
     * 
     * @param conversionFromString
     *        the partial Function from Strings to Ts.
     * @param conversionToString
     *        the partial Function from Ts to Strings.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> partial( final Function<String, Option<T>> conversionFromString,
            final Function<T, Option<String>> conversionToString )
    {
        return new StringConversion<T>( conversionFromString, conversionToString );
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
        return convenientTotal( Function.<String> getIdentityFunction() );
    }

    /**
     * Constructs a StringConversion from a Function from Strings to Ts, and a
     * Function from Ts to Strings.
     * 
     * @param conversionFromString
     *        the Function from Strings to Ts.
     * @param conversionToString
     *        the Function from Ts to Strings.
     * @param <T>
     *        the type that this StringConversion can convert Strings to and
     *        from.
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
     * @return a StringConversion that can convert Strings to Ts and Ts to
     *         Strings.
     */
    public static <T> StringConversion<T> total( final Function<String, T> conversionFromString,
            final Function<T, String> conversionToString )
    {
        return partial( andThenToFullOption( conversionFromString ), andThenToFullOption( conversionToString ) );
    }

    /**
     * Given a Function from T to R, produces a partial Function from T to R
     * that always gives a full Option.
     * 
     * @param <T>
     *        the type that this Function will convert from.
     * @param <R>
     *        the type that this Function will convert to.
     * @param conversion
     *        the total Function to use.
     * @return a partial Function from T to R that always gives a full Option.
     * @throws NullPointerException
     *         if conversion is null.
     */
    private static <T, R> Function<T, Option<R>> andThenToFullOption( final Function<T, R> conversion )
    {
        return conversion.andThen( Option.<R> getFunctionToFullOption() );
    }

    /**
     * The partial Function to use to convert Strings to Ts. This is never null.
     */
    private final Function<String, Option<T>> conversionFromString;

    /**
     * The partial Function to use to convert Ts to Strings. This is never null.
     */
    private final Function<T, Option<String>> conversionToString;

    /**
     * Constructs a StringConversion.
     * 
     * @param conversionFromString
     *        the partial Function to use to convert Strings to Ts.
     * @param conversionToString
     *        the partial Function to use to convert Ts to Strings.
     * @throws NullPointerException
     *         if conversionFromString or conversionToString are null.
     */
    private StringConversion( final Function<String, Option<T>> conversionFromString,
            final Function<T, Option<String>> conversionToString )
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
        return conversionFromString.apply( string );
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
        return conversionToString.apply( t );
    }
}
