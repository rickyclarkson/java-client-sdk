package uk.org.netvu.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.org.netvu.protocol.ParameterDescription.SparseArrayParameterDescription;

/**
 * DecoderCGI is used for building CGI requests to send to decoders, using
 * decoder.frm or decoder.var, and for parsing those requests from URLs.
 */
public final class DecoderCGI
{
    private static final ParameterDescription<Persistence, Persistence> PERSISTENCE;

    private static final SparseArrayParameterDescription<Connection> CONNECTIONS =
            ParameterDescription.sparseArrayParameter( "connections", StringConversion.total(
                    new Connection.FromURLToConnection(), Connection.urlEncode ) );

    private static final SparseArrayParameterDescription<Layout> LAYOUTS =
        ParameterDescription.sparseArrayParameter( "layouts", StringConversion.total( Layout.fromURLFunction(),
                    Layout.urlEncode ) );

    private static final ParameterDescription<String[], Option<String[]>> OUTPUT_TITLES =
            ParameterDescription.parameterWithoutDefault( "output_titles", StringConversion.partial(
                    Option.<String, String[]> getFunctionToEmptyOption( "Parsing not supported for output_titles" ),
                    Option.toPartialFunction( new Function<String[], String>()
                    {
                        @Override
                        public String apply( final String[] array )
                        {
                            return Lists.reduce( Lists.map( Arrays.asList( array ), Strings.surroundWithQuotes() ),
                                    Reduction.intersperseWith( "," ) );
                        }
                    } ) ) );

    private static final SparseArrayParameterDescription<String> COMMANDS = commandsParameter();

    private static SparseArrayParameterDescription<String> commandsParameter()
    {
        final Function<String, String> urlEncodeThenQuote = new URLEncoder().andThen( Strings.surroundWithQuotes() );
        final StringConversion<String> conversions = StringConversion.total( Function.<String>getIdentityFunction(),
                urlEncodeThenQuote );

        return ParameterDescription.sparseArrayParameter( "commands", conversions );
    }

    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            // this is an anonymous intialiser - it is creating a new ArrayList and adding values to it inline.
            add( CONNECTIONS );
            add( LAYOUTS );
            add( OUTPUT_TITLES );
            add( COMMANDS );
        }
    };

    static
    {
        final String message = "Parsing a String into a Persistence is unsupported, as it's embedded in the CGI name.";
        final Function<String, Option<Persistence>> alwaysEmpty = Option.getFunctionToEmptyOption( message );
        PERSISTENCE =
                ParameterDescription.parameterWithDefault(
                        "persistence",
                        Persistence.TEMPORARY,
                        StringConversion.convenientPartial( alwaysEmpty ) );
    }

    /**
     * Parses a URL, or part of a URL, into a DecoderCGI. If it cannot be
     * determined whether it should be a persistent (.frm) or a temporary (.var)
     * request, it will default to .var.
     * 
     * @param string
     *        the URL to parse.
     * @throws NullPointerException if string is null.
     * @return a DecoderCGI.
     */
    public static DecoderCGI fromURL( final String string )
    {
        CheckParameters.areNotNull( string );
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( "Cannot parse " + string + " into a DecoderCGI, because "
                    + map.reason() );
        }

        return new DecoderCGI( map.get() ).persistence( string.contains( ".frm" ) ? Persistence.PERSISTENT
                : Persistence.TEMPORARY );
    }

    private final ParameterMap parameterMap;

    /**
     * Constructs a DecoderCGI with no values set.
     */
    public DecoderCGI()
    {
        this( new ParameterMap() );
    }

    /**
     * Constructs a DecoderCGI with the specified ParameterMap.
     * 
     * @param parameterMap
     *        the ParameterMap to retrieve parameter values from.
     * @throws NullPointerException if parameterMap is null.
     */
    private DecoderCGI( final ParameterMap parameterMap )
    {
        CheckParameters.areNotNull( parameterMap );
        this.parameterMap = parameterMap;
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed command set to the passed-in command.
     * 
     * @param index
     *        the index of the new command.
     * @param command
     *        the command to store.
     * @throws NullPointerException if command is null.
     * @throws IllegalArgumentException if index is negative.
     * @return a new DecoderCGI.
     */
    public DecoderCGI command( final int index, final String command )
    {
        CheckParameters.areNotNull( command ).areNotNegative( index );

        return new DecoderCGI( parameterMap.set( COMMANDS, Collections.singletonList( new Pair<Integer, String>(
                index, command ) ) ) );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed connection set to the passed in Connection.
     * 
     * @param index
     *        the index of the new Connection.
     * @param connection
     *        the Connection to store.
     * @throws NullPointerException if connection is null.
     * @throws IllegalArgumentException if index is negative.
     * @return a new DecoderCGI.
     */
    public DecoderCGI connection( final int index, final Connection connection )
    {
        CheckParameters.areNotNull( connection ).areNotNegative( index );

        return new DecoderCGI( parameterMap.set( CONNECTIONS,
                Collections.singletonList( new Pair<Integer, Connection>( index, connection ) ) ) );
    }

    /**
     * Gets the commands that have been set for this DecoderCGI.
     * 
     * @return the commands that have been set for this DecoderCGI.
     */
    public Map<Integer, String> getCommands()
    {
        return parameterMap.get( COMMANDS );
    }

    /**
     * Gets the connections that have been set for this DecoderCGI.
     * 
     * @return the connections that have been set for this DecoderCGI.
     */
    public TreeMap<Integer, Connection> getConnections()
    {
        return parameterMap.get( CONNECTIONS );
    }

    /**
     * Gets the layouts that have been set for this DecoderCGI.
     * 
     * @return the layouts that have been set for this DecoderCGI.
     */
    public Map<Integer, Layout> getLayouts()
    {
        return parameterMap.get( LAYOUTS );
    }

    /**
     * Gets the output titles that have been set for this DecoderCGI.
     * 
     * @return the output titles that have been set for this DecoderCGI.
     */
    public String[] getOutputTitles()
    {
        return parameterMap.get( OUTPUT_TITLES ).get();
    }

    /**
     * @return the persistence mode for this DecoderCGI.
     */
    public Persistence getPersistence()
    {
        return parameterMap.get( PERSISTENCE );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed layout set to the passed in Layout.
     * 
     * @param index
     *        the index of the new Layout.
     * @param layout
     *        the Layout to store.
     * @throws NullPointerException if layout is null.
     * @throws IllegalArgumentException if index is negative.
     * @return a new DecoderCGI.
     */
    public DecoderCGI layout( final int index, final Layout layout )
    {
        CheckParameters.areNotNull( layout ).areNotNegative( index );

        return new DecoderCGI( parameterMap.set( LAYOUTS, Collections.singletonList( new Pair<Integer, Layout>( index,
                layout ) ) ) );
    }

    /**
     * Constructs a DecoderCGI containing the output titles passed in.
     * 
     * @param titles
     *        the output titles to use.
     * @throws NullPointerException if any of the titles are null.
     * @return a new DecoderCGI.
     */
    public DecoderCGI outputTitles( final String... titles )
    {
        CheckParameters.areNotNull( (Object) titles );
        CheckParameters.areNotNull( (Object[]) titles );

        return new DecoderCGI( parameterMap.set( OUTPUT_TITLES, titles ) );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * persistence parameter to the passed-in value.
     * 
     * @param persistence
     *        the value to use.
     * @throws NullPointerException if persistence is null.
     * @return a new DecoderCGI.
     */
    public DecoderCGI persistence( final Persistence persistence )
    {
        CheckParameters.areNotNull( persistence );
        return new DecoderCGI( parameterMap.set( PERSISTENCE, persistence ) );
    }

    /**
     * Produces part of a URL that can be passed to a decoder, beginning with
     * decoder.frm or decoder.var.
     * 
     * @return part of a URL that can be passed to a decoder.
     */
    public String toURLParameters()
    {
        return "decoder" + getPersistence() + '?' + parameterMap.toURLParameters( params );
    }

    /**
     * A Connection tells the decoder which camera to use, and what parameter
     * values to send to it.
     */
    public static final class Connection
    {
        private static final ParameterDescription<String, Option<String>> SLAVE_IP_PARAM =
                ParameterDescription.parameterWithoutDefault( "slaveip", StringConversion.string() );
        private static final ParameterDescription<Integer, Option<Integer>> SEQ_PARAM =
                ParameterDescription.parameterWithoutDefault( "seq", StringConversion.getHexToIntStringConversion() );
        private static final ParameterDescription<Integer, Option<Integer>> DWELL_PARAM =
                ParameterDescription.parameterWithoutDefault( "dwell", StringConversion.integer() );
        private static final ParameterDescription<Integer, Option<Integer>> CAM =
                ParameterDescription.parameterWithoutDefault( "cam", StringConversion.integer() );
        private static final ParameterDescription<Integer, Option<Integer>> AUDIO_CHANNEL_PARAM =
                ParameterDescription.parameterWithoutDefault( "audio", StringConversion.integer() );

        private static final List<ParameterDescription<?, ? extends Option<?>>> connectionParameters =
                new ArrayList<ParameterDescription<?, ? extends Option<?>>>()
                {
                    {
                        // this is an anonymous intialiser - it is creating a
                        // new ArrayList and adding values to it inline.
                        add( SLAVE_IP_PARAM );
                        add( SEQ_PARAM );
                        add( DWELL_PARAM );
                        add( CAM );
                        add( AUDIO_CHANNEL_PARAM );
                    }
                };

        private final ParameterMap parameterMap;

        /**
         * A Function that, given a Connection, produces a URL-encoded String
         * containing a representation of it as URL parameters.
         */
        static final Function<Connection, String> urlEncode;

        static
        {
            final Function<Connection, String> beforeQuotes = new Function<Connection, String>()
            {
                @Override
                public String apply( final Connection connection )
                {
                    final URLEncoder e = new URLEncoder();
                    final String result = connection.parameterMap.toURLParameters( connectionParameters );
                    return e.apply( result.replaceAll( "&", "," ) );
                }
            };

            urlEncode = beforeQuotes.andThen( Strings.surroundWithQuotes() );
        }

        /**
         * Constructs a Connection.
         */
        public Connection()
        {
            this( new ParameterMap( new ParameterMap.Validator()
            {
                @Override
                public boolean isValid( final ParameterMap parameterMap )
                {
                    return parameterMap.isDefault( CAM ) ? true : parameterMap.isDefault( SEQ_PARAM )
                            && parameterMap.isDefault( DWELL_PARAM );
                }
            } ) );
        }

        /**
         * Constructs a Connection using the specified ParameterMap to store values.
         *
         * @throws NullPointerException if parameterMap is null.
         */
        private Connection( final ParameterMap parameterMap )
        {
            this.parameterMap = parameterMap;
        }

        /**
         * Constructs a new Connection containing the same values as this
         * Connection, but with audio set to the parameter passed in.
         * 
         * @param audioChannel
         *        the source audio channel.
         * @return the new Connection.
         */
        public Connection audio( final int audioChannel )
        {
            return new Connection( parameterMap.set( AUDIO_CHANNEL_PARAM, audioChannel ) );
        }

        /**
         * Constructs a new Connection containing the same values as this
         * Connection, but with cam set to the parameter passed in.
         * 
         * @param cam
         *        the source camera.
         * @return the new Connection.
         */
        public Connection cam( final int cam )
        {
            return new Connection( parameterMap.set( CAM, cam ) );
        }

        /**
         * Constructs a new Connection containing the same values as this
         * Connection, but with dwell set to the parameter passed in.
         * 
         * @param dwell
         *        the time to dwell on each camera in the seq bitmask.
         * @return the new Connection.
         */
        public Connection dwell( final int dwell )
        {
            return new Connection( parameterMap.set( DWELL_PARAM, dwell ) );
        }

        /**
         * Returns the source camera, or throws an IllegalStateException if none
         * exists.
         * 
         * @return the source camera.
         */
        public int getCam()
        {
            return parameterMap.get( CAM ).get();
        }

        /**
         * Returns the time to dwell on each camera in the seq bitmask, or
         * throws an IllegalStateException if this has not been set.
         * 
         * @return the time to dwell on each camera in the seq bitmask.
         */
        public int getDwell()
        {
            return parameterMap.get( DWELL_PARAM ).get();
        }

        /**
         * Returns the source camera mask, or throws an IllegalStateException if
         * none exists.
         * 
         * @return the source camera mask.
         */
        public int getSeq()
        {
            return parameterMap.get( SEQ_PARAM ).get();
        }

        /**
         * Returns the source video server address, or throws an
         * IllegalStateException if none exists.
         * 
         * @return the source video server address.
         */
        public String getSlaveIP()
        {
            return parameterMap.get( SLAVE_IP_PARAM ).get();
        }

        /**
         * Constructs a new Connection containing the same values as this
         * Connection, but with seq set to the parameter passed in.
         * 
         * @param seq
         *        a bitmask of source cameras.
         * @return the new Connection.
         */
        public Connection seq( final int seq )
        {
            return new Connection( parameterMap.set( SEQ_PARAM, seq ) );
        }

        /**
         * Constructs a new Connection containing the same values as this
         * Connection, but with the slaveIP set to the parameter passed in.
         * 
         * @param slaveIP
         *        the IP address of the slave camera.
         * @throws NullPointerException if slaveIP is null.
         * @return the new Connection.
         */
        public Connection slaveIP( final String slaveIP )
        {
            return new Connection( parameterMap.set( SLAVE_IP_PARAM, slaveIP ) );
        }

        /**
         * A Function that, given a String containing URL parameters, produces a
         * Connection from it.
         */
        static final class FromURLToConnection
                extends Function<String, Connection>
        {
            @Override
            public Connection apply( final String urlParameters )
            {
                try
                {
                    final Option<ParameterMap> map =
                            ParameterMap.fromURL( URLDecoder.decode( urlParameters, "UTF-8" ).replaceAll( ",", "&" ),
                                    connectionParameters );

                    if ( map.isEmpty() )
                    {
                        throw new IllegalArgumentException( "Cannot parse " + urlParameters
                                + " into a Connection, because " + map.reason() );
                    }

                    return new Connection( map.get() );
                }
                catch ( final UnsupportedEncodingException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
    }

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
         * Displays six outputs, one large segment in the top left, with 5
         * smaller segments at the right and bottom.
         */
        SIX_WAY( 2 ),

        /**
         * Displays seven outputs, 3 large segments, with a quad in the bottom
         * right quarter.
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
         * Displays a near-fullscreen picture with a smaller picture embedded in
         * the top-left.
         */
        PICTURE_IN_PICTURE( 8 );

        /**
         * The numeric value associated with this Layout.
         */
        final int value;

        /**
         * Converts a Layout to its String representation for use in URL
         * parameters.
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
        static Function<String, Layout> fromURLFunction()
        {
            return new Function<String, Layout>()
            {
                @Override
                public Layout apply( final String url )
                {
                    return find( Integer.parseInt( url ) );
                }
            };
        }

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

            throw new IllegalArgumentException( "There is no Layout with the value " + value + '.' );
        }

        Layout( final int value )
        {
            this.value = value;
        }

    }

    /**
     * Specifies whether a DecoderCGI's query sets variables temporarily or
     * persistently.
     */
    public enum Persistence
    {
        /**
         * Sets variables persistently (on-disk).
         */
        PERSISTENT( ".frm" ),

        /**
         * Sets variables temporarily (in-memory).
         */
        TEMPORARY( ".var" );

        private final String extension;

        Persistence( final String extension )
        {
            this.extension = extension;
        }

        /**
         * Gives ".frm" for PERSISTENT and ".var" for TEMPORARY.
         */
        @Override
        public String toString()
        {
            return extension;
        }
    }
}
