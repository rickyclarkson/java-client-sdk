package uk.org.netvu.protocol;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * An enumeration of supported system variables.
 */
public enum Variable
{
    /**
     * System camera titles.
     */
    C_TITLE( ArrayOrScalar.ARRAY ),

    /**
     * Offset from UTC time.
     */
    UTC_OFFSET( ArrayOrScalar.SCALAR ),

    /**
     * Bitmask of cameras connected.
     */
    LIVE_CAM( ArrayOrScalar.ARRAY ),

    /**
     * Bitmask of cameras with coaxial telemetry enabled.
     */
    COAX_TELM_ENABLE_MASK( ArrayOrScalar.ARRAY ),

    /**
     * A bitmask of cameras with RS485 serial enabled.
     */
    RS485_TELM_ENABLE_MASK( ArrayOrScalar.ARRAY ),

    /**
     * Identifies whether the device has a real time clock.
     */
    HAS_RTC( ArrayOrScalar.SCALAR ),

    /**
     * Current Julian time.
     */
    CURRENT_JULIAN( ArrayOrScalar.SCALAR ),

    /**
     * The horizontal resolution that Video Analytics is using (one element per
     * camera).
     */
    VA_HORIZONTAL_RESOLUTION( ArrayOrScalar.ARRAY ),

    /**
     * The vertical resolution that Video Analytics is using (one element per
     * camera).
     */
    VA_VERTICAL_RESOLUTION( ArrayOrScalar.ARRAY ),

    /**
     * The cameras to display input from for each segment (decoder-specific).
     */
    COMMANDS( ArrayOrScalar.ARRAY ),

    /**
     * The cameras to use (decoder-specific).
     */
    CONNECTIONS( ArrayOrScalar.ARRAY ),

    /**
     * The layouts to use on each channel (decoder-specific).
     */
    LAYOUTS( ArrayOrScalar.ARRAY ),

    /**
     * The titles to display on each channel (decoder-specific).
     */
    OUTPUT_TITLES( ArrayOrScalar.ARRAY );

    private final ArrayOrScalar arrayOrScalar;

    /**
     * A Function that, given a String possibly ending in [], ignores the [] and
     * matches it, case-insensitively, to one of the given Variables, returning
     * it in an Option, or returning an empty Option if there was no match.
     */
    static final Function<String, Option<Variable>> functionFromStringToVariable()
    {
        return new Function<String, Option<Variable>>()
    {
        @Override
        public Option<Variable> apply( final String s )
        {
            try
            {
                return Option.getFullOption( valueOf( s.replaceAll( Pattern.quote( "[]" ), "" ).toUpperCase(
                        Locale.ENGLISH ) ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.getEmptyOption( "Cannot parse " + s + " into a Variable" );
            }
        }
    };
    }

    /**
     * Constructs a Variable, specifying whether it is an array or scalar
     * variable.
     * 
     * @param arrayOrScalar
     *        this is ArrayOrScala.ARRAY if the Variable is an array variable,
     *        and ArrayOrScala.SCALAR otherwise.
     */
    Variable( final ArrayOrScalar arrayOrScalar )
    {
        CheckParameters.areNotNull( arrayOrScalar );
        this.arrayOrScalar = arrayOrScalar;
    }

    /**
     * Gives this Variable as a lowercase String. If it is an array variable,
     * then [] is appended.
     * 
     * @return this Variable as a lowercase String.
     */
    @Override
    public String toString()
    {
        return super.toString().toLowerCase( Locale.ENGLISH ) + ( arrayOrScalar == ArrayOrScalar.ARRAY ? "[]" : "" );
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
