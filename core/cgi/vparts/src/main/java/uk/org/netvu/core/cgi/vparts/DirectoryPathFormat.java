package uk.org.netvu.core.cgi.vparts;

import uk.org.netvu.core.cgi.common.Conversion;

public enum DirectoryPathFormat
{
    SHORT, LONG;

    public static final Conversion<String, DirectoryPathFormat> fromString = new Conversion<String, DirectoryPathFormat>()
    {
        @Override
        public DirectoryPathFormat convert( final String t )
        {
            return DirectoryPathFormat.valueOf( t.toUpperCase() );
        }
    };

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
