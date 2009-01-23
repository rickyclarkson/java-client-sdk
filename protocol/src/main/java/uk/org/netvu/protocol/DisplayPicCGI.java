package uk.org.netvu.protocol;

import static uk.org.netvu.protocol.CommonParameters.AUDIO_CHANNEL;
import static uk.org.netvu.protocol.CommonParameters.BUFFER_COUNT;
import static uk.org.netvu.protocol.CommonParameters.CAMERA;
import static uk.org.netvu.protocol.CommonParameters.CAMERA_SEQUENCE_MASK;
import static uk.org.netvu.protocol.CommonParameters.CONNECTION_ID;
import static uk.org.netvu.protocol.CommonParameters.DURATION;
import static uk.org.netvu.protocol.CommonParameters.FIELD_COUNT;
import static uk.org.netvu.protocol.CommonParameters.FORMAT;
import static uk.org.netvu.protocol.CommonParameters.MAXIMUM_TRANSMIT_RATE;
import static uk.org.netvu.protocol.CommonParameters.OUTPUT_CHANNEL;
import static uk.org.netvu.protocol.CommonParameters.PACKET_SIZE;
import static uk.org.netvu.protocol.CommonParameters.PROXY_MODE;
import static uk.org.netvu.protocol.CommonParameters.PROXY_RETRIES;
import static uk.org.netvu.protocol.CommonParameters.RESOLUTION;
import static uk.org.netvu.protocol.CommonParameters.SLAVE_IP;
import static uk.org.netvu.protocol.CommonParameters.TRANSMISSION_MODE;
import static uk.org.netvu.protocol.CommonParameters.UDP_PORT;

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
     * The specification of the dwellTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> DWELL_TIME =
            ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 );

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
     * The specification of the quantisationFactor parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR =
            ParameterDescription.parameter( "forcedq", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    255, Num.integer ).disallowing( 1 );

    /**
     * The specification of the quantisationFactorForTelemetryImages parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES =
            ParameterDescription.parameter( "telemQ", StringConversion.integer() ).withDefault( -1 ).withBounds( -1,
                    Integer.MAX_VALUE, Num.integer );

    /**
     * The specification of the audioMode parameter.
     */
    private static final ParameterDescription<AudioMode, AudioMode> AUDIO_MODE =
            ParameterDescription.parameter( "audmode",
                    StringConversion.convenientPartial( AudioMode.fromStringFunction() ) ).withDefault( AudioMode.UDP );

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
     * The specification of the proxyPriority parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_PRIORITY =
            ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 );

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
     * Gets the value of the quantisationFactor parameter.
     * 
     * @return the value of the quantisationFactor parameter.
     */
    public int getQuantisationFactor()
    {
        return parameterMap.get( QUANTISATION_FACTOR );
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
     * Gets the value of the audioMode parameter.
     * 
     * @return the value of the audioMode parameter.
     */
    public AudioMode getAudioMode()
    {
        return parameterMap.get( AUDIO_MODE );
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
     * Gets the value of the proxyPriority parameter.
     * 
     * @return the value of the proxyPriority parameter.
     */
    public int getProxyPriority()
    {
        return parameterMap.get( PROXY_PRIORITY );
    }

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
    public String getCameraSequenceMask()
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
     * Gets the value of the maximumTransmitRate parameter.
     * 
     * @return the value of the maximumTransmitRate parameter.
     */
    public int getMaximumTransmitRate()
    {
        return parameterMap.get( MAXIMUM_TRANSMIT_RATE );
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
     * Gets the value of the format parameter.
     * 
     * @return the value of the format parameter.
     */
    public VideoFormat getFormat()
    {
        return parameterMap.get( FORMAT );
    }

    /**
     * Gets the value of the audioChannel parameter.
     * 
     * @return the value of the audioChannel parameter.
     */
    public int getAudioChannel()
    {
        return parameterMap.get( AUDIO_CHANNEL );
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
         * @throws NullPointerException
         *         if cameraSequenceMask is null.
         */
        public Builder cameraSequenceMask( final String cameraSequenceMask )
        {
            CheckParameters.areNotNull( cameraSequenceMask );
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
            return set( FORMAT, format );
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
            return set( AUDIO_CHANNEL, audioChannel );
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
        params.add( DWELL_TIME );
        params.add( PRESELECTOR );
        params.add( CHANNEL );
        params.add( QUANTISATION_FACTOR );
        params.add( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES );
        params.add( AUDIO_MODE );
        params.add( PICTURES_PER_SECOND );
        params.add( MP4_BITRATE );
        params.add( PROXY_PRIORITY );
        params.add( CAMERA );
        params.add( FIELD_COUNT );
        params.add( RESOLUTION );
        params.add( CAMERA_SEQUENCE_MASK );
        params.add( CONNECTION_ID );
        params.add( MAXIMUM_TRANSMIT_RATE );
        params.add( DURATION );
        params.add( BUFFER_COUNT );
        params.add( PACKET_SIZE );
        params.add( UDP_PORT );
        params.add( FORMAT );
        params.add( AUDIO_CHANNEL );
        params.add( TRANSMISSION_MODE );
        params.add( SLAVE_IP );
        params.add( OUTPUT_CHANNEL );
        params.add( PROXY_MODE );
        params.add( PROXY_RETRIES );
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
