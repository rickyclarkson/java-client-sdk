package uk.org.netvu.core.cgi.common;

/**
 * A TwoWayConversion can convert between two types, A and B, but it can be a
 * partial mapping, which is represented by an Option in the results. For
 * internal use only.
 * 
 * @param <A>
 *        the first of the two types that this TwoWayConversion can convert
 *        between.
 * @param <B>
 *        the second of the two types that this TwoWayConversion can convert
 *        between.
 */
public abstract class TwoWayConversion<A, B>
{
    /**
     * A TwoWayConversion that converts between Strings containing decimal
     * integers and Java's Integer and vice-versa.
     */
    public static final TwoWayConversion<String, Integer> integer = convenientPartial( Conversion.stringToInt() );

    /**
     * A TwoWayConversion that converts between Strings and themselves with no
     * extra processing.
     */
    public static final TwoWayConversion<String, String> string = convenientTotal( Conversion.<String> identity() );

    /**
     * A TwoWayConversion that converts between Strings containing 'true' or
     * 'false' and Java's Boolean, and vice-versa.
     */
    public static final TwoWayConversion<String, Boolean> bool = convenientPartial( Conversion.stringToBoolean() );

    /**
     * A TwoWayConversion that converts between Strings containing hexadecimal
     * integers and Java's Long, and vice-versa.
     */
    public static final TwoWayConversion<String, Long> hexLong = partial(
                                                                         Conversion.hexStringToLong(),
                                                                         Option.someRef( Conversion.longToHexString() ) );

    /**
     * A TwoWayConversion that converts between Strings containing hexadecimal
     * integers and Java's Integer, and vice-versa.
     */
    public static final TwoWayConversion<String, Integer> hexInt = partial(
                                                                           Conversion.hexStringToInt(),
                                                                           Option.someRef( Conversion.intToHexString() ) );

    /**
     * A TwoWayConversion that uses the specified partial conversion to convert
     * Strings to Bs, and uses Object.toString() to convert Bs to Strings.
     * 
     * @param <B>
     *        the type of object that this TwoWayConversion can convert Strings
     *        to.
     * @param s2b
     *        a partial conversion that converts Strings to Bs.
     * @return a TwoWayConversion that can convert Strings to Bs and vice-versa.
     */
    public static <B> TwoWayConversion<String, B> convenientPartial(
            final Conversion<String, Option<B>> s2b )
    {
        Checks.notNull( s2b );

        return new TwoWayConversion<String, B>()
        {
            @Override
            public Conversion<String, Option<B>> a2b()
            {
                return s2b;
            }

            @Override
            public Conversion<B, Option<String>> b2a()
            {
                return andThenSome( Conversion.<B> objectToString() );
            }
        };
    }

    /**
     * Constructs a TwoWayConversion that can convert Strings to Bs and Bs to
     * Strings, using the specified Conversion from Strings to Bs, and using
     * Object's toString().
     * 
     * @param <B>
     *        the type to convert Strings to and from.
     * @param s2b
     *        a Conversion from Strings to Bs.
     * @return a TwoWayConversion that can convert Strings to Bs and Bs to
     *         Strings.
     */
    public static <B> TwoWayConversion<String, B> convenientTotal(
            final Conversion<String, B> s2b )
    {
        Checks.notNull( s2b );

        return new TwoWayConversion<String, B>()
        {
            @Override
            public Conversion<String, Option<B>> a2b()
            {
                return andThenSome( s2b );
            }

            @Override
            public Conversion<B, Option<String>> b2a()
            {
                return andThenSome( Conversion.<B> objectToString() );
            }
        };
    }

    /**
     * Constructs a TwoWayConversion from a partial Conversion from As to Bs,
     * and a partial Conversion from Bs to As.
     * 
     * @param <A>
     *        the first of the two types that the constructed TwoWayConversion
     *        can convert between.
     * @param <B>
     *        the second of the two types that the constructed TwoWayConversion
     *        can convert between.
     * @param a2b
     *        the partial Conversion from As to Bs.
     * @param b2a
     *        the partial Conversion from Bs to As.
     * @return a TwoWayConversion that can convert As to Bs.
     */
    public static <A, B> TwoWayConversion<A, B> partial(
            final Conversion<A, Option<B>> a2b,
            final Conversion<B, Option<A>> b2a )
    {
        Checks.notNull( a2b, b2a );

        return new TwoWayConversion<A, B>()
        {
            @Override
            public Conversion<A, Option<B>> a2b()
            {
                return a2b;
            }

            @Override
            public Conversion<B, Option<A>> b2a()
            {
                return b2a;
            }
        };
    }

    /**
     * Constructs a TwoWayConversion that can convert As to Bs and vice-versa,
     * from two Conversions, one for each direction.
     * 
     * @param <A>
     *        the first of the two types that the constructed TwoWayConversion
     *        can convert between.
     * @param <B>
     *        the second of the two types that the constructed TwoWayConversion
     *        can convert between.
     * @param a2b
     *        a Conversion from As to Bs.
     * @param b2a
     *        a Conversion from Bs to As.
     * @return a TwoWayConversion that can convert As to Bs and Bs to As.
     */
    public static <A, B> TwoWayConversion<A, B> total(
            final Conversion<A, B> a2b, final Conversion<B, A> b2a )
    {
        Checks.notNull( a2b, b2a );

        return new TwoWayConversion<A, B>()
        {
            @Override
            public Conversion<A, Option<B>> a2b()
            {
                return andThenSome( a2b );
            }

            @Override
            public Conversion<B, Option<A>> b2a()
            {
                return andThenSome( b2a );
            }
        };
    }

    private static <T, R> Conversion<T, Option<R>> andThenSome(
            final Conversion<T, R> conversion )
    {
        return conversion.andThen( Option.<R> some() );
    }

    private TwoWayConversion()
    {
    }

    /**
     * A partial Conversion that can convert As to Bs.
     * 
     * @return a partial Conversion that can convert As to Bs.
     */
    public abstract Conversion<A, Option<B>> a2b();

    /**
     * A partial Conversion that can convert Bs to As.
     * 
     * @return a partial Conversion that can convert Bs to As.
     */
    public abstract Conversion<B, Option<A>> b2a();
}
