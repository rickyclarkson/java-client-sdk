package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Mode
{
    READ, PROTECT, REINITIALISE;

    public static Conversion<String, Mode> fromString = new Conversion<String, Mode>()
    {
        @Override
        public Mode convert( final String s )
        {
            return Mode.valueOf( s.toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
