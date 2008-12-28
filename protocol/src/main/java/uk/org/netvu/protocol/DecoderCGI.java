package uk.org.netvu.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.org.netvu.protocol.ParameterDescription.SparseArrayParameterDescription;
import uk.org.netvu.util.CheckParameters;

/**
 * A parameter list for a decoder query. Use {@link DecoderCGI.Builder} to
 * construct a DecoderCGI, or {@link DecoderCGI#fromURL(String)}.
 */
public final class DecoderCGI
{
    private static final ParameterDescription<Persistence, Persistence> PERSISTENCE = persistence();

    private static final SparseArrayParameterDescription<Connection> CONNECTIONS =
            ParameterDescription.sparseArrayParameter( "connections", StringConversion.total(
                    new Connection.FromURLToConnection(), Connection.urlEncode ) );

    private static final SparseArrayParameterDescription<Layout> LAYOUTS =
            ParameterDescription.sparseArrayParameter( "layouts", StringConversion.total( Layout.fromURLFunction(),
                    Layout.urlEncode ) );

    private static final ParameterDescription<String[], Option<String[]>> OUTPUT_TITLES = outputTitles();

    private static final SparseArrayParameterDescription<String> COMMANDS = commandsParameter();

    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            // this is an anonymous intialiser - it creates a new ArrayList
            // and adds values to it inline.
            add( CONNECTIONS );
            add( LAYOUTS );
            add( OUTPUT_TITLES );
            add( COMMANDS );
        }
    };

    /**
     * Parses a URL, or part of a URL, into a DecoderCGI. If it cannot be
     * determined whether it should be a persistent (.frm) or a temporary (.var)
     * request, it will default to .var.
     * 
     * @param string
     *        the URL to parse.
     * @throws NullPointerException
     *         if string is null.
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

        final Persistence persistence = string.contains( ".frm" ) ? Persistence.PERSISTENT : Persistence.TEMPORARY;

        final ParameterMap complete = map.get().set( PERSISTENCE, persistence );

        return new DecoderCGI( complete );
    }

    /**
     * Initialisation for the COMMANDS variable.
     * 
     * @return the value to assign to COMMANDS.
     */
    private static SparseArrayParameterDescription<String> commandsParameter()
    {
        final Function<String, String> urlEncodeThenQuote = new URLEncoder().andThen( Strings.surroundWithQuotes() );
        final StringConversion<String> conversions =
                StringConversion.total( Function.<String> getIdentityFunction(), urlEncodeThenQuote );

        return ParameterDescription.sparseArrayParameter( "commands", conversions );
    }

    /**
     * Initialisation for the OUTPUT_TITLES variable.
     * 
     * @return the value to assign to OUTPUT_TITLES.
     */
    private static ParameterDescription<String[], Option<String[]>> outputTitles()
    {
        final Function<String, Option<String[]>> parse = new Function<String, Option<String[]>>()
        {
            @Override
            public Option<String[]> apply( final String text )
            {
                final String[] values = text.split( "," );

                for ( int a = 0; a < values.length; a++ )
                {
                    if ( !values[a].startsWith( "\"" ) || !values[a].endsWith( "\"" ) || values[a].length() < 3 )
                    {
                        String message = " could not be parsed as output_titles";
                        message += " - it should be quoted comma-separated text";
                        return Option.getEmptyOption( text + message );
                    }
                    values[a] = values[a].substring( 1, values[a].length() - 1 );
                }

                return Option.getFullOption( values );
            }
        };

        final Function<String[], Option<String>> generate = new Function<String[], Option<String>>()
        {
            @Override
            public Option<String> apply( final String[] array )
            {
                final List<String> eachTitleSurroundedByQuotes =
                        Lists.map( Arrays.asList( array ), Strings.surroundWithQuotes() );

                final String result = Lists.reduce( eachTitleSurroundedByQuotes, Reduction.intersperseWith( "," ) );
                return Option.getFullOption( result );
            }
        };

        return ParameterDescription.parameterWithoutDefault( "output_titles", StringConversion.partial( parse,
                generate ) );
    }

    private static ParameterDescription<Persistence, Persistence> persistence()
    {
        final String message = "Parsing a String into a Persistence is unsupported, as it's embedded in the CGI name.";
        final Function<String, Option<Persistence>> alwaysEmpty = Option.getFunctionToEmptyOption( message );
        return ParameterDescription.parameterWithDefault( "persistence", Persistence.TEMPORARY,
                StringConversion.convenientPartial( alwaysEmpty ) );
    }

    private final ParameterMap parameterMap;

    /**
     * Constructs a DecoderCGI with the specified ParameterMap.
     * 
     * @param parameterMap
     *        the ParameterMap to retrieve parameter values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    private DecoderCGI( final ParameterMap parameterMap )
    {
        CheckParameters.areNotNull( parameterMap );
        this.parameterMap = parameterMap;
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
    public Map<Integer, Connection> getConnections()
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
     * A builder that takes in all the values for decoder requests as per the
     * Draft Decoder Control Specification, and produces a DecoderCGI when
     * build() is called. Each value must be supplied no more than once. A
     * Builder can only be built once; that is, it can only have build() called
     * on it once. Calling build() a second time will cause an
     * IllegalStateException. Setting its values after calling build() will
     * cause an IllegalStateException.
     */
    public static final class Builder
    {
        /**
         * The ParameterMap that this Builder stores values in.
         */
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        /**
         * Constructs a Builder ready to take in the values needed to construct
         * a Connection.
         */
        public Builder()
        {
        }

        /**
         * Constructs a DecoderCGI with the values from this Builder.
         * 
         * @throws IllegalStateException
         *         if this Builder has already been built.
         * @return a DecoderCGI containing the values from this Builder.
         */
        public DecoderCGI build()
        {
            try
            {
                return new DecoderCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
            }
        }

        /**
         * Sets the indexed command to the passed-in command.
         * 
         * @param index
         *        the index of the new command.
         * @param command
         *        the command to store.
         * @throws NullPointerException
         *         if command is null.
         * @throws IllegalArgumentException
         *         if index is negative.
         * @return the Builder.
         */
        public Builder command( final int index, final String command )
        {
            CheckParameters.areNotNull( command ).areNotNegative( index );

            return set( COMMANDS, Collections.singletonList( new Pair<Integer, String>( index, command ) ) );
        }

        /**
         * Sets the indexed Connection to the passed-in Connection.
         * 
         * @param index
         *        the index of the new Connection.
         * @param connection
         *        the Connection to store.
         * @throws NullPointerException
         *         if connection is null.
         * @throws IllegalArgumentException
         *         if index is negative.
         * @return the Builder.
         */
        public Builder connection( final int index, final Connection connection )
        {
            CheckParameters.areNotNull( connection ).areNotNegative( index );

            return set( CONNECTIONS, Collections.singletonList( new Pair<Integer, Connection>( index, connection ) ) );
        }

        /**
         * Sets the indexed Layout to the passed-in Layout.
         * 
         * @param index
         *        the index of the new Layout.
         * @param layout
         *        the Layout to store.
         * @throws NullPointerException
         *         if layout is null.
         * @throws IllegalArgumentException
         *         if index is negative.
         * @return the Builder.
         */
        public Builder layout( final int index, final Layout layout )
        {
            CheckParameters.areNotNull( layout ).areNotNegative( index );

            return set( LAYOUTS, Collections.singletonList( new Pair<Integer, Layout>( index, layout ) ) );
        }

        /**
         * Sets the stored output titles to the passed-in output titles.
         * 
         * @param titles
         *        the output titles to use.
         * @throws NullPointerException
         *         if any of the titles are null.
         * @return the Builder.
         */
        public Builder outputTitles( final String... titles )
        {
            CheckParameters.areNotNull( (Object) titles );
            CheckParameters.areNotNull( (Object[]) titles );

            return set( OUTPUT_TITLES, titles );
        }

        /**
         * Sets the persistence to the passed-in value.
         * 
         * @param persistence
         *        the value to use.
         * @throws NullPointerException
         *         if persistence is null.
         * @return the Builder.
         */
        public Builder persistence( final Persistence persistence )
        {
            CheckParameters.areNotNull( persistence );
            return set( PERSISTENCE, persistence );
        }

        /**
         * Sets the value of a parameter to a given value, and returns the
         * Builder.
         * 
         * @param <T>
         *        the input type of the specified parameter.
         * @param parameter
         *        the parameter to set a value for.
         * @param value
         *        the value to give that parameter.
         * @return the Builder.
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         * @throws NullPointerException
         *         if parameter or value are null.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }

            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }
    }

    /**
     * A Connection tells the decoder which camera to use, and what parameter
     * values to send to it.
     */
    public static final class Connection
    {
        private static final ParameterDescription<String, Option<String>> SLAVE_IP =
                ParameterDescription.parameterWithoutDefault( "slaveip", StringConversion.string() );
        private static final ParameterDescription<Integer, Option<Integer>> SEQ =
          ParameterDescription.parameterWithoutDefault( "seq", StringConversion.hexInt() );
        private static final ParameterDescription<Integer, Option<Integer>> DWELL =
                ParameterDescription.parameterWithoutDefault( "dwell", StringConversion.integer() );
        private static final ParameterDescription<Integer, Option<Integer>> CAM =
                ParameterDescription.parameterWithoutDefault( "cam", StringConversion.integer() );
        private static final ParameterDescription<Integer, Option<Integer>> AUDIO_CHANNEL =
                ParameterDescription.parameterWithoutDefault( "audio", StringConversion.integer() );

        private static final List<ParameterDescription<?, ? extends Option<?>>> connectionParameters =
                new ArrayList<ParameterDescription<?, ? extends Option<?>>>()
                {
                    {
                        // this is an anonymous intialiser - it is creating a
                        // new ArrayList and adding values to it inline.
                        add( SLAVE_IP );
                        add( SEQ );
                        add( DWELL );
                        add( CAM );
                        add( AUDIO_CHANNEL );
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
         * Constructs a Connection using the specified ParameterMap to retrieve
         * values.
         * 
         * @param parameterMap
         *        the ParameterMap that holds the values for this Connection.
         * @throws NullPointerException
         *         if parameterMap is null.
         */
        private Connection( final ParameterMap parameterMap )
        {
            this.parameterMap = parameterMap;
        }

        /**
         * Returns the audio channel.
         * 
         * @throws IllegalStateException
         *         if no audio channel has been set.
         * @return the audio channel.
         */
        public int getAudioChannel()
        {
            return parameterMap.get( AUDIO_CHANNEL ).get();
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
            return parameterMap.get( DWELL ).get();
        }

        /**
         * Returns the source camera mask, or throws an IllegalStateException if
         * none exists.
         * 
         * @return the source camera mask.
         */
        public int getSeq()
        {
            return parameterMap.get( SEQ ).get();
        }

        /**
         * Returns the source video server address, or throws an
         * IllegalStateException if none exists.
         * 
         * @return the source video server address.
         */
        public String getSlaveIP()
        {
            return parameterMap.get( SLAVE_IP ).get();
        }

        /**
         * A Builder that takes in all the optional values for a Connection as
         * per the Draft Decoder Control Specification, and produces a
         * Connection when build() is called. Each parameter must be supplied no
         * more than once. A Builder can only be built once; that is, it can
         * only have build() called on it once. Calling it a second time will
         * cause an IllegalStateException. Setting its values after calling
         * build() will cause an IllegalStateException.
         */
        public static final class Builder
        {
            private Option<ParameterMap> parameterMap =
                    Option.getFullOption( new ParameterMap( new ParameterMap.Validator()
                    {
                        @Override
                        public boolean isValid( final ParameterMap parameterMap )
                        {
                            return parameterMap.isDefault( CAM ) ? true : parameterMap.isDefault( SEQ )
                                    && parameterMap.isDefault( DWELL );
                        }
                    } ) );

            /**
             * Constructs a Builder ready to take in all the optional values for
             * a Connection.
             */
            public Builder()
            {
            }

            /**
             * Sets the audio channel for the Connection.
             * 
             * @param audioChannel
             *        the source audio channel.
             * @return the Builder.
             */
            public Builder audioChannel( final int audioChannel )
            {
                return set( AUDIO_CHANNEL, audioChannel );
            }

            /**
             * Constructs a Connection with the values from this Builder.
             * 
             * @throws IllegalStateException
             *         if this Builder has already been built.
             * @return a Connection containing the values from this Builder.
             */
            public Connection build()
            {
                try
                {
                    return new Connection( parameterMap.get() );
                }
                finally
                {
                    parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
                }
            }

            /**
             * Sets the source camera for the Connection.
             * 
             * @param camera
             *        the source camera.
             * @return the Builder.
             */
            public Builder camera( final int camera )
            {
                return set( CAM, camera );
            }

            /**
             * Sets the dwell time for the Connection.
             * 
             * @param dwell
             *        the time to dwell on each camera in the seq bitmask.
             * @return the Builder.
             */
            public Builder dwell( final int dwell )
            {
                return set( DWELL, dwell );
            }

            /**
             * Sets the seq bitmask for the Connection.
             * 
             * @param seq
             *        a bitmask of source cameras.
             * @return the Builder.
             */
            public Builder seq( final int seq )
            {
                return set( SEQ, seq );
            }

            /**
             * Sets the slave IP address for the Connection.
             * 
             * @param slaveIP
             *        the IP address of the slave camera.
             * @throws NullPointerException
             *         if slaveIP is null.
             * @return the Builder.
             */
            public Builder slaveIP( final String slaveIP )
            {
                return set( SLAVE_IP, slaveIP );
            }

            /**
             * Sets the value of a parameter to a given value, and returns the
             * Builder.
             * 
             * @param <T>
             *        the input type of the specified parameter.
             * @param parameter
             *        the parameter to set a value for.
             * @param value
             *        the value to give that parameter.
             * @return the Builder.
             * @throws IllegalStateException
             *         if the Builder has already been built once.
             * @throws NullPointerException
             *         if parameter or value are null.
             */
            private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
            {
                if ( parameterMap.isEmpty() )
                {
                    final String message = "The Builder has already been built (build() has been called on it).";
                    throw new IllegalStateException( message );
                }

                parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
                return this;
            }
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

        /**
         * Converts the String representation of a Layout to a Layout.
         * 
         * @return a Function that converts the String representation of a
         *         Layout to a Layout.
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
