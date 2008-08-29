package uk.org.netvu.core.cgi.variable;

import java.util.regex.Pattern;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Variable
{
    C_TITLE( ArrayOrScalar.ARRAY ),
    UTC_OFFSET( ArrayOrScalar.SCALAR ),
    LIVE_CAM( ArrayOrScalar.ARRAY ),
    COAX_TELM_ENABLE_MASK( ArrayOrScalar.ARRAY ),
    RS485_TELM_ENABLE_MASK( ArrayOrScalar.ARRAY ),
    HAS_RTC( ArrayOrScalar.SCALAR ),
    CURRENT_JULIAN( ArrayOrScalar.SCALAR ),
    VA_HORIZONTAL_RESOLUTION( ArrayOrScalar.ARRAY ),
    VA_VERTICAL_RESOLUTION( ArrayOrScalar.ARRAY );

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
