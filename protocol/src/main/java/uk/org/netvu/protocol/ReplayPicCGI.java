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
     * The specification of the camera parameter.
     */
    private static final ParameterDescription<Integer, Integer> CAMERA =
            ParameterDescription.parameter( "cam", StringConversion.integer() ).withDefault( 1 ).withBounds( 1, 16,
                    Num.integer );

    /**
     * The specification of the fields parameter.
     */
    private static final ParameterDescription<Integer, Integer> FIELDS =
            ParameterDescription.parameter( "fields", StringConversion.integer() ).withDefault( 1 ).notNegative(
                    Num.integer );

    /**
     * The specification of the cameraSequenceMask parameter.
     */
    private static final ParameterDescription<Integer, Integer> CAMERA_SEQUENCE_MASK =
            ParameterDescription.parameter( "seq", StringConversion.hexInt() ).withDefault( 0 ).withBounds( 0, 0xF,
                    Num.integer );

    /**
     * The specification of the connectionID parameter.
     */
    private static final ParameterDescription<Integer, Integer> CONNECTION_ID =
            ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 );

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
     * The specification of the maximumTransmitRate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MAXIMUM_TRANSMIT_RATE =
            ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 );

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
     * The specification of the audioOn parameter.
     */
    private static final ParameterDescription<OnOrOff, OnOrOff> AUDIO =
            ParameterDescription
                .parameter( "audio", StringConversion.convenientPartial( OnOrOff.fromStringFunction() ) ).withDefault(
                        OnOrOff.OFF );

    /**
     * The specification of the fastForwardMultiplier parameter.
     */
    private static final ParameterDescription<Integer, Integer> FAST_FORWARD_MULTIPLIER =
            ParameterDescription.parameter( "ffmult", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    256, Num.integer );

    /**
     * The specification of the duration parameter.
     */
    private static final ParameterDescription<Integer, Integer> DURATION =
            ParameterDescription.parameter( "duration", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );

    /**
     * The specification of the resolution parameter.
     */
    private static final ParameterDescription<String, String> RESOLUTION =
            ParameterDescription.parameter( "res", StringConversion.string() ).withDefault( "med" ).allowedValues(
                    "hi", "med", "lo" );

    /**
     * The specification of the packetSize parameter.
     */
    private static final ParameterDescription<Integer, Integer> PACKET_SIZE =
            ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0, ParameterDescription.parameter(
                    "pkt_size", StringConversion.integer() ).withDefault( 0 ) );

    /**
     * The specification of the udpPort parameter.
     */
    private static final ParameterDescription<Integer, Integer> UDP_PORT =
            ParameterDescription.parameter( "udp_port", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    65535, Num.integer );

    /**
     * The specification of the refresh parameter.
     */
    private static final ParameterDescription<Integer, Integer> REFRESH =
            ParameterDescription.parameter( "refresh", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the format parameter.
     */
    private static final ParameterDescription<Format, Format> FORMAT =
            ParameterDescription
                .parameter( "format", StringConversion.convenientPartial( Format.fromStringFunction() ) ).withDefault(
                        Format.JFIF );

    /**
     * The specification of the transmissionMode parameter.
     */
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE =
            ParameterDescription.parameterWithDefault( "txmode", new Function<ParameterMap, TransmissionMode>()
            {
                @Override
                public TransmissionMode apply( final ParameterMap map )
                {
                    return map.get( FORMAT ) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL;
                }
            }, StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) );

    /**
     * The specification of the slaveIP parameter.
     */
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP =
            ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial( IPAddress.fromString ) )
                .withDefault( IPAddress.fromString( "0.0.0.0" ).get() );

    /**
     * The specification of the outputChannel parameter.
     */
    private static final ParameterDescription<Integer, Integer> OUTPUT_CHANNEL =
            ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 );

    /**
     * The specification of the proxyMode parameter.
     */
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE =
            ParameterDescription.parameter( "proxymode",
                    StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault(
                    ProxyMode.TRANSIENT );

    /**
     * The specification of the proxyRetries parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_RETRIES =
            ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 );

    /**
     * Gets the value of the camera parameter.
     * 
     * @return the value of the camera parameter.
     */
    public int getCamera()
    {
        return parameterMap.get( CAMERA );
    }

    /**
     * Gets the value of the fields parameter.
     * 
     * @return the value of the fields parameter.
     */
    public int getFields()
    {
        return parameterMap.get( FIELDS );
    }

    /**
     * Gets the value of the cameraSequenceMask parameter.
     * 
     * @return the value of the cameraSequenceMask parameter.
     */
    public int getCameraSequenceMask()
    {
        return parameterMap.get( CAMERA_SEQUENCE_MASK );
    }

    /**
     * Gets the value of the connectionID parameter.
     * 
     * @return the value of the connectionID parameter.
     */
    public int getConnectionID()
    {
        return parameterMap.get( CONNECTION_ID );
    }

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
     * Gets the value of the maximumTransmitRate parameter.
     * 
     * @return the value of the maximumTransmitRate parameter.
     */
    public int getMaximumTransmitRate()
    {
        return parameterMap.get( MAXIMUM_TRANSMIT_RATE );
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
     * Gets the value of the audioOn parameter.
     * 
     * @return the value of the audioOn parameter.
     */
    public OnOrOff isAudioOn()
    {
        return parameterMap.get( AUDIO );
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
     * Gets the value of the duration parameter.
     * 
     * @return the value of the duration parameter.
     */
    public int getDuration()
    {
        return parameterMap.get( DURATION );
    }

    /**
     * Gets the value of the resolution parameter.
     * 
     * @return the value of the resolution parameter.
     */
    public String getResolution()
    {
        return parameterMap.get( RESOLUTION );
    }

    /**
     * Gets the value of the packetSize parameter.
     * 
     * @return the value of the packetSize parameter.
     */
    public int getPacketSize()
    {
        return parameterMap.get( PACKET_SIZE );
    }

    /**
     * Gets the value of the udpPort parameter.
     * 
     * @return the value of the udpPort parameter.
     */
    public int getUdpPort()
    {
        return parameterMap.get( UDP_PORT );
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
     * Gets the value of the format parameter.
     * 
     * @return the value of the format parameter.
     */
    public Format getFormat()
    {
        return parameterMap.get( FORMAT );
    }

    /**
     * Gets the value of the transmissionMode parameter.
     * 
     * @return the value of the transmissionMode parameter.
     */
    public TransmissionMode getTransmissionMode()
    {
        return parameterMap.get( TRANSMISSION_MODE );
    }

    /**
     * Gets the value of the slaveIP parameter.
     * 
     * @return the value of the slaveIP parameter.
     */
    public IPAddress getSlaveIP()
    {
        return parameterMap.get( SLAVE_IP );
    }

    /**
     * Gets the value of the outputChannel parameter.
     * 
     * @return the value of the outputChannel parameter.
     */
    public int getOutputChannel()
    {
        return parameterMap.get( OUTPUT_CHANNEL );
    }

    /**
     * Gets the value of the proxyMode parameter.
     * 
     * @return the value of the proxyMode parameter.
     */
    public ProxyMode getProxyMode()
    {
        return parameterMap.get( PROXY_MODE );
    }

    /**
     * Gets the value of the proxyRetries parameter.
     * 
     * @return the value of the proxyRetries parameter.
     */
    public int getProxyRetries()
    {
        return parameterMap.get( PROXY_RETRIES );
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
         * Sets the camera parameter in the builder.
         * 
         * @param camera
         *        the value to store as the camera parameter.
         * @return the Builder
         */
        public Builder camera( final int camera )
        {
            return set( CAMERA, camera );
        }

        /**
         * Sets the fields parameter in the builder.
         * 
         * @param fields
         *        the value to store as the fields parameter.
         * @return the Builder
         */
        public Builder fields( final int fields )
        {
            return set( FIELDS, fields );
        }

        /**
         * Sets the cameraSequenceMask parameter in the builder.
         * 
         * @param cameraSequenceMask
         *        the value to store as the cameraSequenceMask parameter.
         * @return the Builder
         */
        public Builder cameraSequenceMask( final int cameraSequenceMask )
        {
            return set( CAMERA_SEQUENCE_MASK, cameraSequenceMask );
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
            return set( CONNECTION_ID, connectionID );
        }

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
         * Sets the maximumTransmitRate parameter in the builder.
         * 
         * @param maximumTransmitRate
         *        the value to store as the maximumTransmitRate parameter.
         * @return the Builder
         */
        public Builder maximumTransmitRate( final int maximumTransmitRate )
        {
            return set( MAXIMUM_TRANSMIT_RATE, maximumTransmitRate );
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
         * Sets the audioOn parameter in the builder.
         * 
         * @param audioOn
         *        the value to store as the audioOn parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if audioOn is null.
         */
        public Builder audioOn( final OnOrOff audioOn )
        {
            CheckParameters.areNotNull( audioOn );
            return set( AUDIO, audioOn );
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
         * Sets the duration parameter in the builder.
         * 
         * @param duration
         *        the value to store as the duration parameter.
         * @return the Builder
         */
        public Builder duration( final int duration )
        {
            return set( DURATION, duration );
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
            return set( RESOLUTION, resolution );
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
            return set( PACKET_SIZE, packetSize );
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
            return set( UDP_PORT, udpPort );
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
         * Sets the format parameter in the builder.
         * 
         * @param format
         *        the value to store as the format parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if format is null.
         */
        public Builder format( final Format format )
        {
            CheckParameters.areNotNull( format );
            return set( FORMAT, format );
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
            return set( TRANSMISSION_MODE, transmissionMode );
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
            return set( SLAVE_IP, slaveIP );
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
            return set( OUTPUT_CHANNEL, outputChannel );
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
            return set( PROXY_MODE, proxyMode );
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
            return set( PROXY_RETRIES, proxyRetries );
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
        params.add( CAMERA );
        params.add( FIELDS );
        params.add( CAMERA_SEQUENCE_MASK );
        params.add( CONNECTION_ID );
        params.add( CONTROL );
        params.add( GMT_TIME );
        params.add( LOCAL_TIME );
        params.add( MAXIMUM_TRANSMIT_RATE );
        params.add( TEXT );
        params.add( TIME_RANGE );
        params.add( AUDIO );
        params.add( FAST_FORWARD_MULTIPLIER );
        params.add( DURATION );
        params.add( RESOLUTION );
        params.add( PACKET_SIZE );
        params.add( UDP_PORT );
        params.add( REFRESH );
        params.add( FORMAT );
        params.add( TRANSMISSION_MODE );
        params.add( SLAVE_IP );
        params.add( OUTPUT_CHANNEL );
        params.add( PROXY_MODE );
        params.add( PROXY_RETRIES );
    }

    /**
     * The possible formats that the video stream can be returned as.
     */
    public static enum Format
    {
        /**
         * Complete JFIF (JPEG) image data.
         */
        JFIF,

        /**
         * Truncated JPEG image data.
         */
        JPEG,

        /**
         * MPEG-4 image data.
         */
        MP4;

        /**
         * A Function that, given a String, will produce an Option containing a
         * member of Format if the passed-in String matches it (ignoring case),
         * and an empty Option otherwise.
         * 
         * @return a Function that parses a String into a Format
         */
        static Function<String, Option<Format>> fromStringFunction()
        {
            return new Function<String, Option<Format>>()
            {
                @Override
                public Option<Format> apply( final String s )
                {
                    for ( final Format element : values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Format element " );
                }
            };
        }

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
     * The possible stream headers that the video stream can be wrapped in.
     */
    public static enum TransmissionMode
    {
        /**
         * Multipart MIME.
         */
        MIME,

        /**
         * AD's 'binary' format.
         */
        BINARY,

        /**
         * AD's 'minimal' format.
         */
        MINIMAL;

        /**
         * A Function that, given a String, will produce an Option containing a
         * member of TransmissionMode if the passed-in String matches it
         * (ignoring case), and an empty Option otherwise.
         * 
         * @return a Function that parses a String into a TransmissionMode
         */
        static Function<String, Option<TransmissionMode>> fromStringFunction()
        {
            return new Function<String, Option<TransmissionMode>>()
            {
                @Override
                public Option<TransmissionMode> apply( final String s )
                {
                    for ( final TransmissionMode element : values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid TransmissionMode element " );
                }
            };
        }

    }

    /**
     * This is used in storing whether audio should be enabled.
     */
    public static enum OnOrOff
    {
        /**
         * Signifies that audio is enabled.
         */
        ON,

        /**
         * Signifies that audio is disabled.
         */
        OFF;

        /**
         * A Function that, given a String, will produce an Option containing a
         * member of OnOrOff if the passed-in String matches it (ignoring case),
         * and an empty Option otherwise.
         * 
         * @return a Function that parses a String into a OnOrOff
         */
        static Function<String, Option<OnOrOff>> fromStringFunction()
        {
            return new Function<String, Option<OnOrOff>>()
            {
                @Override
                public Option<OnOrOff> apply( final String s )
                {
                    for ( final OnOrOff element : values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid OnOrOff element " );
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
     * Converts a String to a ReplayPicCGI if it matches one of ReplayPicCGI's
     * members case-insensitively, returning it in an Option if it does, and
     * returning an empty Option otherwise.
     * 
     * @param string
     *        the String to parse.
     * @return an Option containing the parsed ReplayPicCGI if the parse
     *         succeeded; the Option is empty otherwise
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
