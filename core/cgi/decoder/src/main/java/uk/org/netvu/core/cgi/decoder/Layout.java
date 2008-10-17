package uk.org.netvu.core.cgi.decoder;

import uk.org.netvu.core.cgi.common.Function;

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
     * Displays six outputs, one large segment in the top left, with 5 smaller
     * segments at the right and bottom.
     */
    SIX_WAY( 2 ),

    /**
     * Displays seven outputs, 3 large segments, with a quad in the bottom right
     * quarter.
     */
    SEVEN_WAY( 3 ),

    /**
     * Displays nine outputs in a 3x3 grid.
     */
    NINE_WAY( 4 ),

    /**
     * Displays ten outputs, 2 quads at the top, and 2 quarters.
     */
    TEN_WAY( 5 ),

    /**
     * Displays thirteen outputs, 1 quarter at the top left, and 3 quads.
     */
    THIRTEEN_WAY( 6 ),

    /**
     * Displays sixteen outputs in a 4x4 grid.
     */
    SIXTEEN_WAY( 7 ),

    /**
     * Displays a near-fullscreen picture with a smaller picture embedded in the
     * top-left.
     */
    PICTURE_IN_PICTURE( 8 );

    /**
     * The numeric value associated with this Layout.
     */
    final int value;

    /**
     * Converts a Layout to its String representation for use in URL parameters.
     */
    static final Function<Layout, String> urlEncode = new Function<Layout, String>()
    {
        @Override
        public String apply( final Layout layout )
        {
            return String.valueOf( layout.value );
        }
    };

    /**
     * Converts the String representation of a Layout to a Layout.
     */
    static final Function<String, Layout> fromURL = new Function<String, Layout>()
    {
        @Override
        public Layout apply( final String url )
        {
            return find( Integer.parseInt( url ) );
        }
    };

    /**
     * @param value
     *        the number associated with the Layout to find.
     * @return the Layout corresponding with the specified value.
     * @throws IllegalArgumentException
     *         if the number does not correspond to a value.
     */
    public static Layout find( final int value )
    {
        for ( final Layout layout : Layout.values() )
        {
            if ( layout.value == value )
            {
                return layout;
            }
        }

        throw new IllegalArgumentException(
                "There is no Layout with the value " + value + '.' );
    }

    Layout( final int value )
    {
        this.value = value;
    }

}
