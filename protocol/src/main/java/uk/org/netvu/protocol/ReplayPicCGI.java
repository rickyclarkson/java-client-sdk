package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.util.CheckParameters;

/**
 * A parameter list for a replay_pic.cgi query. Use {@link ReplayPicCGI.Builder}
 * to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromString(String)}.
 */
public final class ReplayPicCGI
{
    /**
     * The ParameterMap to get values from.
     */
    private final ParameterMap parameterMap;

    /**
     * Constructs a ReplayPicCGI, using the values from the specified
     * ParameterMap.
     * 
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    ReplayPicCGI( final ParameterMap parameterMap )
    {
        CheckParameters.areNotNull( parameterMap );
        this.parameterMap = parameterMap;
    }

    /**
     * All the parameter specifications, used in parsing URLs.
     */
    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();

    /**
     * The specification of the control parameter.
     */
    private static final ParameterDescription<Control, Control> CONTROL =
            ParameterDescription.parameter( "control",
                    StringConversion.convenientPartial( Control.fromStringFunction() ) ).withDefault( Control.STOP );

    /**
     * The specification of the gmtTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> GMT_TIME =
            ParameterDescription.parameter( "time", StringConversion.convenientPartial( fromTimeFunction() ) )
                .withDefault( 0 );

    /**
     * The specification of the localTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> LOCAL_TIME =
            ParameterDescription.parameter( "local", StringConversion.convenientPartial( fromTimeFunction() ) )
                .withDefault( 0 );

    /**
     * The specification of the text parameter.
     */
    private static final ParameterDescription<String, String> TEXT =
            ParameterDescription.parameter( "text", StringConversion.string() ).withDefault( "" );

    /**
     * The specification of the timeRange parameter.
     */
    private static final ParameterDescription<Integer, Integer> TIME_RANGE =
            ParameterDescription.parameter( "timerange", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the fastForwardMultiplier parameter.
     */
    private static final ParameterDescription<Integer, Integer> FAST_FORWARD_MULTIPLIER =
            ParameterDescription.parameter( "ffmult", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    256, Num.integer );

    /**
     * The specification of the refresh parameter.
     */
    private static final ParameterDescription<Integer, Integer> REFRESH =
            ParameterDescription.parameter( "refresh", StringConversion.integer() ).withDefault( 0 );

    /**
     * Gets the value of the control parameter.
     * 
     * @return the value of the control parameter.
     */
    public Control getControl()
    {
        return parameterMap.get( CONTROL );
    }

    /**
     * Gets the value of the gmtTime parameter.
     * 
     * @return the value of the gmtTime parameter.
     */
    public int getGMTTime()
    {
        return parameterMap.get( GMT_TIME );
    }

    /**
     * Gets the value of the localTime parameter.
     * 
     * @return the value of the localTime parameter.
     */
    public int getLocalTime()
    {
        return parameterMap.get( LOCAL_TIME );
    }

    /**
     * Gets the value of the text parameter.
     * 
     * @return the value of the text parameter.
     */
    public String getText()
    {
        return parameterMap.get( TEXT );
    }

    /**
     * Gets the value of the timeRange parameter.
     * 
     * @return the value of the timeRange parameter.
     */
    public int getTimeRange()
    {
        return parameterMap.get( TIME_RANGE );
    }

    /**
     * Gets the value of the fastForwardMultiplier parameter.
     * 
     * @return the value of the fastForwardMultiplier parameter.
     */
    public int getFastForwardMultiplier()
    {
        return parameterMap.get( FAST_FORWARD_MULTIPLIER );
    }

    /**
     * Gets the value of the refresh parameter.
     * 
     * @return the value of the refresh parameter.
     */
    public int getRefresh()
    {
        return parameterMap.get( REFRESH );
    }

    /**
     * Gets the value of the camera parameter.
     * 
     * @return the value of the camera parameter.
     */
    public int getCamera()
    {
        return parameterMap.get( CommonParameters.CAMERA );
    }

    /**
     * Gets the value of the fieldCount parameter.
     * 
     * @return the value of the fieldCount parameter.
     */
    public int getFieldCount()
    {
        return parameterMap.get( CommonParameters.FIELD_COUNT );
    }

    /**
     * Gets the value of the resolution parameter.
     * 
     * @return the value of the resolution parameter.
     */
    public String getResolution()
    {
        return parameterMap.get( CommonParameters.RESOLUTION );
    }

    /**
     * Gets the value of the cameraSequenceMask parameter.
     * 
     * @return the value of the cameraSequenceMask parameter.
     */
    public String getCameraSequenceMask()
    {
        return parameterMap.get( CommonParameters.CAMERA_SEQUENCE_MASK );
    }

    /**
     * Gets the value of the connectionID parameter.
     * 
     * @return the value of the connectionID parameter.
     */
    public int getConnectionID()
    {
        return parameterMap.get( CommonParameters.CONNECTION_ID );
    }

    /**
     * Gets the value of the maximumTransmitRate parameter.
     * 
     * @return the value of the maximumTransmitRate parameter.
     */
    public int getMaximumTransmitRate()
    {
        return parameterMap.get( CommonParameters.MAXIMUM_TRANSMIT_RATE );
    }

    /**
     * Gets the value of the duration parameter.
     * 
     * @return the value of the duration parameter.
     */
    public int getDuration()
    {
        return parameterMap.get( CommonParameters.DURATION );
    }

    /**
     * Gets the value of the packetSize parameter.
     * 
     * @return the value of the packetSize parameter.
     */
    public int getPacketSize()
    {
        return parameterMap.get( CommonParameters.PACKET_SIZE );
    }

    /**
     * Gets the value of the udpPort parameter.
     * 
     * @return the value of the udpPort parameter.
     */
    public int getUdpPort()
    {
        return parameterMap.get( CommonParameters.UDP_PORT );
    }

    /**
     * Gets the value of the format parameter.
     * 
     * @return the value of the format parameter.
     */
    public VideoFormat getFormat()
    {
        return parameterMap.get( CommonParameters.FORMAT );
    }

    /**
     * Gets the value of the audioChannel parameter.
     * 
     * @return the value of the audioChannel parameter.
     */
    public int getAudioChannel()
    {
        return parameterMap.get( CommonParameters.AUDIO_CHANNEL );
    }

    /**
     * Gets the value of the transmissionMode parameter.
     * 
     * @return the value of the transmissionMode parameter.
     */
    public TransmissionMode getTransmissionMode()
    {
        return parameterMap.get( CommonParameters.TRANSMISSION_MODE );
    }

    /**
     * Gets the value of the slaveIP parameter.
     * 
     * @return the value of the slaveIP parameter.
     */
    public IPAddress getSlaveIP()
    {
        return parameterMap.get( CommonParameters.SLAVE_IP );
    }

    /**
     * Gets the value of the outputChannel parameter.
     * 
     * @return the value of the outputChannel parameter.
     */
    public int getOutputChannel()
    {
        return parameterMap.get( CommonParameters.OUTPUT_CHANNEL );
    }

    /**
     * Gets the value of the proxyMode parameter.
     * 
     * @return the value of the proxyMode parameter.
     */
    public ProxyMode getProxyMode()
    {
        return parameterMap.get( CommonParameters.PROXY_MODE );
    }

    /**
     * Gets the value of the proxyRetries parameter.
     * 
     * @return the value of the proxyRetries parameter.
     */
    public int getProxyRetries()
    {
        return parameterMap.get( CommonParameters.PROXY_RETRIES );
    }

    /**
     * A builder that takes in all the optional values for ReplayPicCGI and
     * produces a ReplayPicCGI when build() is called. Each parameter must be
     * supplied no more than once. A Builder can only be built once; that is, it
     * can only have build() called on it once. Calling it a second time will
     * cause an IllegalStateException. Setting its values after calling build()
     * will cause an IllegalStateException.
     */
    public static final class Builder
    {
        /**
         * The values supplied for each parameter so far. When this is an empty
         * Option, the Builder is in an invalid state, the reason for which is
         * stored in the Option.
         */
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        /**
         * Sets the control parameter in the builder.
         * 
         * @param control
         *        the value to store as the control parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if control is null.
         */
        public Builder control( final Control control )
        {
            CheckParameters.areNotNull( control );
            return set( CONTROL, control );
        }

        /**
         * Sets the gmtTime parameter in the builder.
         * 
         * @param gmtTime
         *        the value to store as the gmtTime parameter.
         * @return the Builder
         */
        public Builder gmtTime( final int gmtTime )
        {
            return set( GMT_TIME, gmtTime );
        }

        /**
         * Sets the localTime parameter in the builder.
         * 
         * @param localTime
         *        the value to store as the localTime parameter.
         * @return the Builder
         */
        public Builder localTime( final int localTime )
        {
            return set( LOCAL_TIME, localTime );
        }

        /**
         * Sets the text parameter in the builder.
         * 
         * @param text
         *        the value to store as the text parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if text is null.
         */
        public Builder text( final String text )
        {
            CheckParameters.areNotNull( text );
            return set( TEXT, text );
        }

        /**
         * Sets the timeRange parameter in the builder.
         * 
         * @param timeRange
         *        the value to store as the timeRange parameter.
         * @return the Builder
         */
        public Builder timeRange( final int timeRange )
        {
            return set( TIME_RANGE, timeRange );
        }

        /**
         * Sets the fastForwardMultiplier parameter in the builder.
         * 
         * @param fastForwardMultiplier
         *        the value to store as the fastForwardMultiplier parameter.
         * @return the Builder
         */
        public Builder fastForwardMultiplier( final int fastForwardMultiplier )
        {
            return set( FAST_FORWARD_MULTIPLIER, fastForwardMultiplier );
        }

        /**
         * Sets the refresh parameter in the builder.
         * 
         * @param refresh
         *        the value to store as the refresh parameter.
         * @return the Builder
         */
        public Builder refresh( final int refresh )
        {
            return set( REFRESH, refresh );
        }

        /**
         * Sets the camera parameter in the builder.
         * 
         * @param camera
         *        the value to store as the camera parameter.
         * @return the Builder
         */
        public Builder camera( final int camera )
        {
            return set( CommonParameters.CAMERA, camera );
        }

        /**
         * Sets the fieldCount parameter in the builder.
         * 
         * @param fieldCount
         *        the value to store as the fieldCount parameter.
         * @return the Builder
         */
        public Builder fieldCount( final int fieldCount )
        {
            return set( CommonParameters.FIELD_COUNT, fieldCount );
        }

        /**
         * Sets the resolution parameter in the builder.
         * 
         * @param resolution
         *        the value to store as the resolution parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if resolution is null.
         */
        public Builder resolution( final String resolution )
        {
            CheckParameters.areNotNull( resolution );
            return set( CommonParameters.RESOLUTION, resolution );
        }

        /**
         * Sets the cameraSequenceMask parameter in the builder.
         * 
         * @param cameraSequenceMask
         *        the value to store as the cameraSequenceMask parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if cameraSequenceMask is null.
         */
        public Builder cameraSequenceMask( final String cameraSequenceMask )
        {
            CheckParameters.areNotNull( cameraSequenceMask );
            return set( CommonParameters.CAMERA_SEQUENCE_MASK, cameraSequenceMask );
        }

        /**
         * Sets the connectionID parameter in the builder.
         * 
         * @param connectionID
         *        the value to store as the connectionID parameter.
         * @return the Builder
         */
        public Builder connectionID( final int connectionID )
        {
            return set( CommonParameters.CONNECTION_ID, connectionID );
        }

        /**
         * Sets the maximumTransmitRate parameter in the builder.
         * 
         * @param maximumTransmitRate
         *        the value to store as the maximumTransmitRate parameter.
         * @return the Builder
         */
        public Builder maximumTransmitRate( final int maximumTransmitRate )
        {
            return set( CommonParameters.MAXIMUM_TRANSMIT_RATE, maximumTransmitRate );
        }

        /**
         * Sets the duration parameter in the builder.
         * 
         * @param duration
         *        the value to store as the duration parameter.
         * @return the Builder
         */
        public Builder duration( final int duration )
        {
            return set( CommonParameters.DURATION, duration );
        }

        /**
         * Sets the packetSize parameter in the builder.
         * 
         * @param packetSize
         *        the value to store as the packetSize parameter.
         * @return the Builder
         */
        public Builder packetSize( final int packetSize )
        {
            return set( CommonParameters.PACKET_SIZE, packetSize );
        }

        /**
         * Sets the udpPort parameter in the builder.
         * 
         * @param udpPort
         *        the value to store as the udpPort parameter.
         * @return the Builder
         */
        public Builder udpPort( final int udpPort )
        {
            return set( CommonParameters.UDP_PORT, udpPort );
        }

        /**
         * Sets the format parameter in the builder.
         * 
         * @param format
         *        the value to store as the format parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if format is null.
         */
        public Builder format( final VideoFormat format )
        {
            CheckParameters.areNotNull( format );
            return set( CommonParameters.FORMAT, format );
        }

        /**
         * Sets the audioChannel parameter in the builder.
         * 
         * @param audioChannel
         *        the value to store as the audioChannel parameter.
         * @return the Builder
         */
        public Builder audioChannel( final int audioChannel )
        {
            return set( CommonParameters.AUDIO_CHANNEL, audioChannel );
        }

        /**
         * Sets the transmissionMode parameter in the builder.
         * 
         * @param transmissionMode
         *        the value to store as the transmissionMode parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if transmissionMode is null.
         */
        public Builder transmissionMode( final TransmissionMode transmissionMode )
        {
            CheckParameters.areNotNull( transmissionMode );
            return set( CommonParameters.TRANSMISSION_MODE, transmissionMode );
        }

        /**
         * Sets the slaveIP parameter in the builder.
         * 
         * @param slaveIP
         *        the value to store as the slaveIP parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if slaveIP is null.
         */
        public Builder slaveIP( final IPAddress slaveIP )
        {
            CheckParameters.areNotNull( slaveIP );
            return set( CommonParameters.SLAVE_IP, slaveIP );
        }

        /**
         * Sets the outputChannel parameter in the builder.
         * 
         * @param outputChannel
         *        the value to store as the outputChannel parameter.
         * @return the Builder
         */
        public Builder outputChannel( final int outputChannel )
        {
            return set( CommonParameters.OUTPUT_CHANNEL, outputChannel );
        }

        /**
         * Sets the proxyMode parameter in the builder.
         * 
         * @param proxyMode
         *        the value to store as the proxyMode parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if proxyMode is null.
         */
        public Builder proxyMode( final ProxyMode proxyMode )
        {
            CheckParameters.areNotNull( proxyMode );
            return set( CommonParameters.PROXY_MODE, proxyMode );
        }

        /**
         * Sets the proxyRetries parameter in the builder.
         * 
         * @param proxyRetries
         *        the value to store as the proxyRetries parameter.
         * @return the Builder
         */
        public Builder proxyRetries( final int proxyRetries )
        {
            return set( CommonParameters.PROXY_RETRIES, proxyRetries );
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
         * @return the Builder
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         * @throws NullPointerException
         *         if parameter or value are null.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            CheckParameters.areNotNull( parameter, value );
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }
            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }

        /**
         * Constructs a ReplayPicCGI with the values from this Builder.
         * 
         * @return a ReplayPicCGI containing the values from this Builder
         * @throws IllegalStateException
         *         if the Builder has already been built.
         */
        public ReplayPicCGI build()
        {
            try
            {
                return new ReplayPicCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
            }
        }

    }
    static
    {
        params.add( CONTROL );
        params.add( GMT_TIME );
        params.add( LOCAL_TIME );
        params.add( TEXT );
        params.add( TIME_RANGE );
        params.add( FAST_FORWARD_MULTIPLIER );
        params.add( REFRESH );
        params.add( CommonParameters.CAMERA );
        params.add( CommonParameters.FIELD_COUNT );
        params.add( CommonParameters.RESOLUTION );
        params.add( CommonParameters.CAMERA_SEQUENCE_MASK );
        params.add( CommonParameters.CONNECTION_ID );
        params.add( CommonParameters.MAXIMUM_TRANSMIT_RATE );
        params.add( CommonParameters.DURATION );
        params.add( CommonParameters.PACKET_SIZE );
        params.add( CommonParameters.UDP_PORT );
        params.add( CommonParameters.FORMAT );
        params.add( CommonParameters.AUDIO_CHANNEL );
        params.add( CommonParameters.TRANSMISSION_MODE );
        params.add( CommonParameters.SLAVE_IP );
        params.add( CommonParameters.OUTPUT_CHANNEL );
        params.add( CommonParameters.PROXY_MODE );
        params.add( CommonParameters.PROXY_RETRIES );
    }

    /**
     * Various video playback modes.
     */
    public static enum Control
    {
        /**
         * Play video forwards at its original speed.
         */
        PLAY,

        /**
         * Play video forwards at a speed controlled by the fast-forward
         * multiplier.
         */
        FFWD,

        /**
         * Play video backwards.
         */
        RWND,

        /**
         * Stop playing video.
         */
        STOP;

        /**
         * A Function that, given a String, will produce an Option containing a
         * member of Control if the passed-in String matches it (ignoring case),
         * and an empty Option otherwise.
         * 
         * @return a Function that parses a String into a Control
         */
        static Function<String, Option<Control>> fromStringFunction()
        {
            return new Function<String, Option<Control>>()
            {
                @Override
                public Option<Control> apply( final String s )
                {
                    for ( final Control element : values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Control element " );
                }
            };
        }

    }

    /**
     * A Function that parses a timestamp either in HH:mm:ss:dd:MM:yy format or
     * as a Julian time.
     * 
     * @return a Function that parses a timestamp.
     */
    static Function<String, Option<Integer>> fromTimeFunction()
    {
        return new Function<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> apply( final String s )
            {
                final java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( "HH:mm:ss:dd:MM:yy" );
                try
                {
                    return Option.getFullOption( (int) ( format.parse( s ).getTime() / 1000 ) );
                }
                catch ( final java.text.ParseException e )
                {
                    try
                    {
                        return Option.getFullOption( Integer.parseInt( s ) );
                    }
                    catch ( final NumberFormatException e2 )
                    {
                        return Option.getEmptyOption( "Cannot parse " + s + " as a timestamp." );
                    }
                }
            }
        };
    }

    /**
     * Converts this ReplayPicCGI into a String containing a URL beginning with
     * /replay_pic.cgi? and containing the supplied parameters.
     * 
     * @return a String containing a URL beginning with /replay_pic.cgi? and
     *         containing the supplied parameters
     */
    @Override
    public String toString()
    {
        return "/replay_pic.cgi?" + parameterMap.toURLParameters( params );
    }

    /**
     * Converts a String containing a URL describing a /replay_pic.cgi? request
     * into a ReplayPicCGI.
     * 
     * @param string
     *        the String to parse.
     * @return A ReplayPicCGI describing the specified URL
     * @throws IllegalArgumentException
     *         if the String cannot be parsed into a ReplayPicCGI.
     * @throws NullPointerException
     *         if string is null.
     */
    public static ReplayPicCGI fromString( final String string )
    {
        CheckParameters.areNotNull( string );
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into a ReplayPicCGI." );
        }
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }
        return new ReplayPicCGI( map.get() );
    }

}
