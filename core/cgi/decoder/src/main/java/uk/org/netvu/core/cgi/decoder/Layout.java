package uk.org.netvu.core.cgi.decoder;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Layout
{
    SINGLE( 0 ), FOUR_WAY( 1 ), NINE_WAY( 2 ), SIXTEEN_WAY( 3 );

    private final int value;

    Layout( final int value )
    {
        this.value = value;
    }

    public static final Conversion<Layout, String> urlEncode = new Conversion<Layout, String>()
    {
        @Override
        public String convert( final Layout layout )
        {
            return String.valueOf( layout.value );
        }
    };

    public static final Conversion<String, Layout> fromURL = new Conversion<String, Layout>()
    {
        @Override
        public Layout convert( final String url )
        {
            return find( Integer.parseInt( url ) );
        }
    };

    private static Layout find( final int value )
    {
        for ( final Layout layout : Layout.values() )
        {
            if ( layout.value == value )
            {
                return layout;
            }
        }

        throw new IllegalArgumentException();
    }

}
