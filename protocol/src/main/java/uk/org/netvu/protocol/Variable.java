package uk.org.netvu.protocol;

/**
 * An enumeration of supported system variables.
 */
public final class Variable
{
    /**
     * System camera titles.
     */
    public static final Variable C_TITLE = new Variable( "c_title", ArrayOrScalar.ARRAY );

    /**
     * Offset from UTC time.
     */
    public static final Variable UTC_OFFSET = new Variable( "utc_offset", ArrayOrScalar.SCALAR );

    /**
     * Bitmask of cameras connected.
     */
    public static final Variable LIVE_CAM = new Variable( "live_cam", ArrayOrScalar.ARRAY );

    /**
     * Bitmask of cameras with coaxial telemetry enabled.
     */
    public static final Variable COAX_TELM_ENABLE_MASK = new Variable( "coax_telm_enable_mask", ArrayOrScalar.ARRAY );

    /**
     * A bitmask of cameras with RS485 serial enabled.
     */
    public static final Variable RS485_TELM_ENABLE_MASK = new Variable( "rs485_telm_enable_mask", ArrayOrScalar.ARRAY );

    /**
     * Identifies whether the device has a real time clock.
     */
    public static final Variable HAS_RTC = new Variable( "has_rtc", ArrayOrScalar.SCALAR );

    /**
     * Current Julian time.
     */
    public static final Variable CURRENT_JULIAN = new Variable( "current_julian", ArrayOrScalar.SCALAR );

    /**
     * The horizontal resolution that Video Analytics is using (one element per
     * camera).
     */
    public static final Variable VA_HORIZONTAL_RESOLUTION =
            new Variable( "va_horizontal_resolution", ArrayOrScalar.ARRAY );

    /**
     * The vertical resolution that Video Analytics is using (one element per
     * camera).
     */
    public static final Variable VA_VERTICAL_RESOLUTION = new Variable( "va_vertical_resolution", ArrayOrScalar.ARRAY );

    /**
     * The cameras to display input from for each segment (decoder-specific).
     */
    public static final Variable COMMANDS = new Variable( "commands", ArrayOrScalar.ARRAY );

    /**
     * The cameras to use (decoder-specific).
     */
    public static final Variable CONNECTIONS = new Variable( "connections", ArrayOrScalar.ARRAY );

    /**
     * The layouts to use on each channel (decoder-specific).
     */
    public static final Variable LAYOUTS = new Variable( "layouts", ArrayOrScalar.ARRAY );

    /**
     * The titles to display on each channel (decoder-specific).
     */
    public static final Variable OUTPUT_TITLES = new Variable( "output_titles", ArrayOrScalar.ARRAY );

    /**
     * A Function that, given a String possibly ending in [], constructs a
     * Variable with the part before the [] as its name, and if there is a [] it
     * will be an {@link ArrayOrScalar.ARRAY} variable, and a
     * {@link ArrayOrScalar.SCALAR} otherwise.
     * 
     * @return a Function that constructs a Variable from a String.
     */
    static final Function<String, Variable> fromStringFunction()
    {
        return new Function<String, Variable>()
        {
            @Override
            public Variable apply( final String s )
            {
                return new Variable( Strings.beforeFirstLeniently( s, "[]" ), s.endsWith( "[]" ) ? ArrayOrScalar.ARRAY
                        : ArrayOrScalar.SCALAR );
            }
        };
    }
    private final String name;

    private final ArrayOrScalar arrayOrScalar;

    /**
     * Constructs a Variable, specifying whether it is an array or scalar
     * variable.
     * 
     * @param name
     *        the name of the Variable.
     * @param arrayOrScalar
     *        this is ArrayOrScala.ARRAY if the Variable is an array variable,
     *        and ArrayOrScala.SCALAR otherwise.
     * @throws NullPointerException
     *         if name or arrayOrScalar are null.
     */
    public Variable( final String name, final ArrayOrScalar arrayOrScalar )
    {
        CheckParameters.areNotNull( name, arrayOrScalar );
        this.name = name;
        this.arrayOrScalar = arrayOrScalar;
    }

    /**
     * Compares this object to another for equality.
     * 
     * @param other
     *        the object to compare this object to.
     * @return true if the specified object is a Variable with the same name and
     *         type (ArrayOrScalar) as this Variable, false otherwise.
     */
    @Override
    public boolean equals( final Object other )
    {
        if ( other instanceof Variable )
        {
            final Variable varOther = (Variable) other;
            return varOther.name.equals( name ) && varOther.arrayOrScalar == arrayOrScalar;
        }

        return false;
    }

    /**
     * Computes a hashcode based on the name and arrayOrScalar values.
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + arrayOrScalar.hashCode() * 13237;
    }

    /**
     * Gives this Variable as a String. If it is an array variable, then [] is
     * appended.
     * 
     * @return this Variable as a lowercase String.
     */
    @Override
    public String toString()
    {
        return name + ( arrayOrScalar == ArrayOrScalar.ARRAY ? "[]" : "" );
    }

    /**
     * Gives the name of the Variable.
     *
     * @return the name of the Variable.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Identifies whether the Variable is an array variable or not.
     *
     * @return true if the Variable is an array variable, false otherwise.
     */
    public boolean isArray()
    {
        return arrayOrScalar == ArrayOrScalar.ARRAY;
    }

    /**
     * Specifies whether a system variable is an array or a scalar value.
     */
    public enum ArrayOrScalar
    {
        /**
         * Represents that the system variable is an array (0 or more values).
         */
        ARRAY,

        /**
         * Represents that the system variable is a scalar (1 value).
         */
        SCALAR
    }
}
