package uk.org.netvu.core.cgi.variable;

import static uk.org.netvu.core.cgi.variable.ArrayOrScalar.ARRAY;
import static uk.org.netvu.core.cgi.variable.ArrayOrScalar.SCALAR;

import java.util.regex.Pattern;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Variable
{
    C_TITLE( ARRAY ),
    UTC_OFFSET( SCALAR ),
    LIVE_CAM( ARRAY ),
    COAX_TELM_ENABLE_MASK( ARRAY ),
    RS485_TELM_ENABLE_MASK( ARRAY ),
    HAS_RTC( SCALAR ),
    CURRENT_JULIAN( SCALAR ),
    VA_HORIZONTAL_RESOLUTION( ARRAY ),
    VA_VERTICAL_RESOLUTION( ARRAY ),
    COMMANDS( ARRAY ),
    CONNECTIONS( ARRAY ),
    LAYOUTS( ARRAY ),
    OUTPUT_TITLES( ARRAY );

    private final ArrayOrScalar arrayOrScalar;

    Variable( final ArrayOrScalar arrayOrScalar )
    {
        this.arrayOrScalar = arrayOrScalar;
    }

    public static final Conversion<String, Variable> fromString = new Conversion<String, Variable>()
    {
        @Override
        public Variable convert( final String s )
        {
            return valueOf( s.replaceAll( Pattern.quote( "[]" ), "" ).toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase()
                + ( arrayOrScalar == ArrayOrScalar.ARRAY ? "[]" : "" );
    }
}
