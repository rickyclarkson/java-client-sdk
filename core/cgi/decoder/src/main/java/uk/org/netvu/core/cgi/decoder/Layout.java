package uk.org.netvu.core.cgi.decoder;

import uk.org.netvu.core.cgi.common.Conversion;

/**
 * An enumeration of all the layouts that the DecoderCGI can specify.
 */
public enum Layout
{
    /**
     * Displays one output on the whole screen.
     */
    SINGLE( 0 ),

    /**
     * Displays four outputs in a 2x2 grid.
     */
    FOUR_WAY( 1 ),

    /**
     * Displays nine outputs in a 3x3 grid.
     */
    NINE_WAY( 2 ),

    /**
     * Displays sixteen outputs in a 4x4 grid.
     */
    SIXTEEN_WAY( 3 );

    final int value;

    Layout( final int value )
    {
        this.value = value;
    }

    static final Conversion<Layout, String> urlEncode = new Conversion<Layout, String>()
    {
        @Override
        public String convert( final Layout layout )
        {
            return String.valueOf( layout.value );
        }
    };

    static final Conversion<String, Layout> fromURL = new Conversion<String, Layout>()
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
