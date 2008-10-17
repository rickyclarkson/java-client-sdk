package uk.org.netvu.core.cgi.variable;

import static uk.org.netvu.core.cgi.variable.ArrayOrScalar.ARRAY;
import static uk.org.netvu.core.cgi.variable.ArrayOrScalar.SCALAR;

import java.util.regex.Pattern;

import uk.org.netvu.core.cgi.common.Function;
import uk.org.netvu.core.cgi.common.Option;

/**
 * An enumeration of supported system variables.
 */
public enum Variable
{
    /**
     * System camera titles.
     */
    C_TITLE( ARRAY ),

    /**
     * Offset to UTC time.
     */
    UTC_OFFSET( SCALAR ),

    /**
     * Bitmask of cameras connected.
     */
    LIVE_CAM( ARRAY ),

    /**
     * Bitmask of cameras with coaxial telemetry enabled.
     */
    COAX_TELM_ENABLE_MASK( ARRAY ),

    /**
     * A bitmask of cameras with RS485 serial enabled.
     */
    RS485_TELM_ENABLE_MASK( ARRAY ),

    /**
     * Identifies whether the device has a real time clock.
     */
    HAS_RTC( SCALAR ),

    /**
     * Current Julian time.
     */
    CURRENT_JULIAN( SCALAR ),

    /**
     * The horizontal resolution that Video Analytics is using (one element per
     * camera).
     */
    VA_HORIZONTAL_RESOLUTION( ARRAY ),

    /**
     * The vertical resolution that Video Analytics is using (one element per
     * camera).
     */
    VA_VERTICAL_RESOLUTION( ARRAY ),

    /**
     * The cameras to display input from for each segment.
     */
    COMMANDS( ARRAY ),

    /**
     * The cameras to use (decoder-specific).
     */
    CONNECTIONS( ARRAY ),

    /**
     * The layouts to use on each channel (decoder-specific).
     */
    LAYOUTS( ARRAY ),

    /**
     * The titles to display on each channel (decoder-specific).
     */
    OUTPUT_TITLES( ARRAY );

    private final ArrayOrScalar arrayOrScalar;

    /**
     * A Function that, given a String possibly ending in [], ignores the []
     * and matches it, case-insensitively, to one of the given Variables,
     * returning it in an Option, or returning an empty Option if there was no
     * match.
     */
    static final Function<String, Option<Variable>> fromString = new Function<String, Option<Variable>>()
    {
        @Override
        public Option<Variable> apply( final String s )
        {
            try
            {
                return Option.getFullOption( valueOf( s.replaceAll(
                        Pattern.quote( "[]" ), "" ).toUpperCase() ) );
            }
            catch ( final IllegalArgumentException exception )
            {
                return Option.getEmptyOption( "Cannot parse " + s
                        + " into a Variable" );
            }
        }
    };

    Variable( final ArrayOrScalar arrayOrScalar )
    {
        this.arrayOrScalar = arrayOrScalar;
    }

    @Override
    public String toString()
    {
        return super.toString().toLowerCase()
                + ( arrayOrScalar == ArrayOrScalar.ARRAY ? "[]" : "" );
    }
}
