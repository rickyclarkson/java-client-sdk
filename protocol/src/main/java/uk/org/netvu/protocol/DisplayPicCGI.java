package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.util.CheckParameters;

/**
 * A parameter list for a display_pic.cgi query. Use
 * {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or
 * {@link DisplayPicCGI#fromString(String)}.
 */
public final class DisplayPicCGI
{
    /**
     * The ParameterMap to get values from.
     */
    private final ParameterMap parameterMap;

    /**
     * Constructs a DisplayPicCGI, using the values from the specified
     * ParameterMap.
     * 
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    DisplayPicCGI( final ParameterMap parameterMap )
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
     * The specification of the fieldCount parameter.
     */
    private static final ParameterDescription<Integer, Integer> FIELD_COUNT =
            ParameterDescription.parameter( "fields", StringConversion.integer() ).withDefault( 1 ).positive(
                    Num.integer );

    /**
     * The specification of the resolution parameter.
     */
    private static final ParameterDescription<String, String> RESOLUTION =
            ParameterDescription.parameter( "res", StringConversion.string() ).withDefault( "med" ).allowedValues(
                    "hi", "med", "lo" );

    /**
     * The specification of the cameraSequenceMask parameter.
     */
    private static final ParameterDescription<Integer, Integer> CAMERA_SEQUENCE_MASK =
            ParameterDescription.parameter( "seq", StringConversion.hexInt() ).withDefault( 0 ).withBounds( 0, 0xF,
                    Num.integer );

    /**
     * The specification of the dwellTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> DWELL_TIME =
            ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the connectionID parameter.
     */
    private static final ParameterDescription<Integer, Integer> CONNECTION_ID =
            ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the dIndex parameter.
     */
    private static final ParameterDescription<Integer, Integer> DINDEX =
            ParameterDescription.parameter( "dindex", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the preselector parameter.
     */
    private static final ParameterDescription<Integer, Integer> PRESELECTOR =
            ParameterDescription.parameter( "presel", StringConversion.integer() ).withDefault( 0 ).withBounds( 0, 3,
                    Num.integer );

    /**
     * The specification of the channel parameter.
     */
    private static final ParameterDescription<Integer, Integer> CHANNEL =
            ParameterDescription.parameter( "channel", StringConversion.integer() ).withDefault( -1 ).withBounds( -1,
                    1, Num.integer );

    /**
     * The specification of the maximumTransmitRate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MAXIMUM_TRANSMIT_RATE =
            ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the quantisationFactor parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR =
            ParameterDescription.parameter( "forcedq", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    255, Num.integer ).disallowing( 1 );

    /**
     * The specification of the duration parameter.
     */
    private static final ParameterDescription<Integer, Integer> DURATION =
            ParameterDescription.parameter( "duration", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );

    /**
     * The specification of the bufferCount parameter.
     */
    private static final ParameterDescription<Integer, Integer> BUFFER_COUNT =
            ParameterDescription.parameter( "nbuffers", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );

    /**
     * The specification of the quantisationFactorForTelemetryImages parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES =
            ParameterDescription.parameter( "telemQ", StringConversion.integer() ).withDefault( -1 ).withBounds( -1,
                    Integer.MAX_VALUE, Num.integer );

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
     * The specification of the audio parameter.
     */
    private static final ParameterDescription<String, String> AUDIO =
            ParameterDescription.parameter( "audio", StringConversion.string() ).withDefault( "0" ).allowedValues(
                    "on", "off", "0", "1", "2" );

    /**
     * The specification of the format parameter.
     */
    private static final ParameterDescription<Format, Format> FORMAT =
            ParameterDescription
                .parameter( "format", StringConversion.convenientPartial( Format.fromStringFunction() ) ).withDefault(
                        Format.JFIF );

    /**
     * The specification of the audioMode parameter.
     */
    private static final ParameterDescription<AudioMode, AudioMode> AUDIO_MODE =
            ParameterDescription.parameter( "audmode",
                    StringConversion.convenientPartial( AudioMode.fromStringFunction() ) ).withDefault( AudioMode.UDP );

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
     * The specification of the picturesPerSecond parameter.
     */
    private static final ParameterDescription<Integer, Integer> PICTURES_PER_SECOND =
            ParameterDescription.parameter( "pps", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the mp4Bitrate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MP4_BITRATE =
            ParameterDescription.parameter( "mp4rate", StringConversion.integer() ).withDefault( 0 );

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
     * The specification of the proxyPriority parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_PRIORITY =
            ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 );

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
     * Gets the value of the fieldCount parameter.
     * 
     * @return the value of the fieldCount parameter.
     */
    public int getFieldCount()
    {
        return parameterMap.get( FIELD_COUNT );
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
     * Gets the value of the cameraSequenceMask parameter.
     * 
     * @return the value of the cameraSequenceMask parameter.
     */
    public int getCameraSequenceMask()
    {
        return parameterMap.get( CAMERA_SEQUENCE_MASK );
    }

    /**
     * Gets the value of the dwellTime parameter.
     * 
     * @return the value of the dwellTime parameter.
     */
    public int getDwellTime()
    {
        return parameterMap.get( DWELL_TIME );
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
     * Gets the value of the dIndex parameter.
     * 
     * @return the value of the dIndex parameter.
     */
    public int getDIndex()
    {
        return parameterMap.get( DINDEX );
    }

    /**
     * Gets the value of the preselector parameter.
     * 
     * @return the value of the preselector parameter.
     */
    public int getPreselector()
    {
        return parameterMap.get( PRESELECTOR );
    }

    /**
     * Gets the value of the channel parameter.
     * 
     * @return the value of the channel parameter.
     */
    public int getChannel()
    {
        return parameterMap.get( CHANNEL );
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
     * Gets the value of the quantisationFactor parameter.
     * 
     * @return the value of the quantisationFactor parameter.
     */
    public int getQuantisationFactor()
    {
        return parameterMap.get( QUANTISATION_FACTOR );
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
     * Gets the value of the bufferCount parameter.
     * 
     * @return the value of the bufferCount parameter.
     */
    public int getBufferCount()
    {
        return parameterMap.get( BUFFER_COUNT );
    }

    /**
     * Gets the value of the quantisationFactorForTelemetryImages parameter.
     * 
     * @return the value of the quantisationFactorForTelemetryImages parameter.
     */
    public int getQuantisationFactorForTelemetryImages()
    {
        return parameterMap.get( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES );
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
     * Gets the value of the audio parameter.
     * 
     * @return the value of the audio parameter.
     */
    public String getAudio()
    {
        return parameterMap.get( AUDIO );
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
     * Gets the value of the audioMode parameter.
     * 
     * @return the value of the audioMode parameter.
     */
    public AudioMode getAudioMode()
    {
        return parameterMap.get( AUDIO_MODE );
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
     * Gets the value of the picturesPerSecond parameter.
     * 
     * @return the value of the picturesPerSecond parameter.
     */
    public int getPicturesPerSecond()
    {
        return parameterMap.get( PICTURES_PER_SECOND );
    }

    /**
     * Gets the value of the mp4Bitrate parameter.
     * 
     * @return the value of the mp4Bitrate parameter.
     */
    public int getMp4Bitrate()
    {
        return parameterMap.get( MP4_BITRATE );
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
     * Gets the value of the proxyPriority parameter.
     * 
     * @return the value of the proxyPriority parameter.
     */
    public int getProxyPriority()
    {
        return parameterMap.get( PROXY_PRIORITY );
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
     * A builder that takes in all the optional values for DisplayPicCGI and
     * produces a DisplayPicCGI when build() is called. Each parameter must be
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
         * Sets the fieldCount parameter in the builder.
         * 
         * @param fieldCount
         *        the value to store as the fieldCount parameter.
         * @return the Builder
         */
        public Builder fieldCount( final int fieldCount )
        {
            return set( FIELD_COUNT, fieldCount );
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
         * Sets the dwellTime parameter in the builder.
         * 
         * @param dwellTime
         *        the value to store as the dwellTime parameter.
         * @return the Builder
         */
        public Builder dwellTime( final int dwellTime )
        {
            return set( DWELL_TIME, dwellTime );
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
         * Sets the dIndex parameter in the builder.
         * 
         * @param dIndex
         *        the value to store as the dIndex parameter.
         * @return the Builder
         */
        public Builder dIndex( final int dIndex )
        {
            return set( DINDEX, dIndex );
        }

        /**
         * Sets the preselector parameter in the builder.
         * 
         * @param preselector
         *        the value to store as the preselector parameter.
         * @return the Builder
         */
        public Builder preselector( final int preselector )
        {
            return set( PRESELECTOR, preselector );
        }

        /**
         * Sets the channel parameter in the builder.
         * 
         * @param channel
         *        the value to store as the channel parameter.
         * @return the Builder
         */
        public Builder channel( final int channel )
        {
            return set( CHANNEL, channel );
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
         * Sets the quantisationFactor parameter in the builder.
         * 
         * @param quantisationFactor
         *        the value to store as the quantisationFactor parameter.
         * @return the Builder
         */
        public Builder quantisationFactor( final int quantisationFactor )
        {
            return set( QUANTISATION_FACTOR, quantisationFactor );
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
         * Sets the bufferCount parameter in the builder.
         * 
         * @param bufferCount
         *        the value to store as the bufferCount parameter.
         * @return the Builder
         */
        public Builder bufferCount( final int bufferCount )
        {
            return set( BUFFER_COUNT, bufferCount );
        }

        /**
         * Sets the quantisationFactorForTelemetryImages parameter in the
         * builder.
         * 
         * @param quantisationFactorForTelemetryImages
         *        the value to store as the quantisationFactorForTelemetryImages
         *        parameter.
         * @return the Builder
         */
        public Builder quantisationFactorForTelemetryImages( final int quantisationFactorForTelemetryImages )
        {
            return set( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES, quantisationFactorForTelemetryImages );
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
         * Sets the audio parameter in the builder.
         * 
         * @param audio
         *        the value to store as the audio parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if audio is null.
         */
        public Builder audio( final String audio )
        {
            CheckParameters.areNotNull( audio );
            return set( AUDIO, audio );
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
         * Sets the audioMode parameter in the builder.
         * 
         * @param audioMode
         *        the value to store as the audioMode parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if audioMode is null.
         */
        public Builder audioMode( final AudioMode audioMode )
        {
            CheckParameters.areNotNull( audioMode );
            return set( AUDIO_MODE, audioMode );
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
         * Sets the picturesPerSecond parameter in the builder.
         * 
         * @param picturesPerSecond
         *        the value to store as the picturesPerSecond parameter.
         * @return the Builder
         */
        public Builder picturesPerSecond( final int picturesPerSecond )
        {
            return set( PICTURES_PER_SECOND, picturesPerSecond );
        }

        /**
         * Sets the mp4Bitrate parameter in the builder.
         * 
         * @param mp4Bitrate
         *        the value to store as the mp4Bitrate parameter.
         * @return the Builder
         */
        public Builder mp4Bitrate( final int mp4Bitrate )
        {
            return set( MP4_BITRATE, mp4Bitrate );
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
         * Sets the proxyPriority parameter in the builder.
         * 
         * @param proxyPriority
         *        the value to store as the proxyPriority parameter.
         * @return the Builder
         */
        public Builder proxyPriority( final int proxyPriority )
        {
            return set( PROXY_PRIORITY, proxyPriority );
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
         * Constructs a DisplayPicCGI with the values from this Builder.
         * 
         * @return a DisplayPicCGI containing the values from this Builder
         * @throws IllegalStateException
         *         if the Builder has already been built.
         */
        public DisplayPicCGI build()
        {
            try
            {
                return new DisplayPicCGI( parameterMap.get() );
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
        params.add( FIELD_COUNT );
        params.add( RESOLUTION );
        params.add( CAMERA_SEQUENCE_MASK );
        params.add( DWELL_TIME );
        params.add( CONNECTION_ID );
        params.add( DINDEX );
        params.add( PRESELECTOR );
        params.add( CHANNEL );
        params.add( MAXIMUM_TRANSMIT_RATE );
        params.add( QUANTISATION_FACTOR );
        params.add( DURATION );
        params.add( BUFFER_COUNT );
        params.add( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES );
        params.add( PACKET_SIZE );
        params.add( UDP_PORT );
        params.add( AUDIO );
        params.add( FORMAT );
        params.add( AUDIO_MODE );
        params.add( TRANSMISSION_MODE );
        params.add( PICTURES_PER_SECOND );
        params.add( MP4_BITRATE );
        params.add( SLAVE_IP );
        params.add( OUTPUT_CHANNEL );
        params.add( PROXY_MODE );
        params.add( PROXY_PRIORITY );
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
     * The possible mechanisms for returning audio data.
     */
    public static enum AudioMode
    {
        /**
         * Out of band UDP data.
         */
        UDP,

        /**
         * In-band data interleaved with images.
         */
        INLINE;

        /**
         * A Function that, given a String, will produce an Option containing a
         * member of AudioMode if the passed-in String matches it (ignoring
         * case), and an empty Option otherwise.
         * 
         * @return a Function that parses a String into a AudioMode
         */
        static Function<String, Option<AudioMode>> fromStringFunction()
        {
            return new Function<String, Option<AudioMode>>()
            {
                @Override
                public Option<AudioMode> apply( final String s )
                {
                    for ( final AudioMode element : values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid AudioMode element " );
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
     * Converts this DisplayPicCGI into a String containing a URL beginning with
     * /display_pic.cgi? and containing the supplied parameters.
     * 
     * @return a String containing a URL beginning with /display_pic.cgi? and
     *         containing the supplied parameters
     */
    @Override
    public String toString()
    {
        return "/display_pic.cgi?" + parameterMap.toURLParameters( params );
    }

    /**
     * Converts a String to a DisplayPicCGI if it matches one of DisplayPicCGI's
     * members case-insensitively, returning it in an Option if it does, and
     * returning an empty Option otherwise.
     * 
     * @param string
     *        the String to parse.
     * @return an Option containing the parsed DisplayPicCGI if the parse
     *         succeeded; the Option is empty otherwise
     * @throws NullPointerException
     *         if string is null.
     */
    public static DisplayPicCGI fromString( final String string )
    {
        CheckParameters.areNotNull( string );
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into a DisplayPicCGI." );
        }
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }
        return new DisplayPicCGI( map.get() );
    }

}
