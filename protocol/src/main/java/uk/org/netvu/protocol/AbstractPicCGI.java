package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;   

/**
 * A common supertype of DisplayPicCGI and ReplayPicCGI containing common code.
 */
class AbstractPicCGI
{
    /**
     * The ParameterMap to get values from.
     */
    final ParameterMap parameterMap;
    
    /**
     * Constructs an AbstractPicCGI with the specified ParameterMap.
     * @param parameterMap
     *        The ParameterMap to store parameter values in..
     */
    AbstractPicCGI( ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }
    /**
     * The specification of the camera parameter.
     */
    static  final ParameterDescription<Integer, Integer> CAMERA = 
            ParameterDescription.parameter( "cam", StringConversion.integer() )
            .withDefault( 1 ).positive( Num.integer );
    
    /**
     * The specification of the fieldCount parameter.
     */
    static  final ParameterDescription<Integer, Integer> FIELD_COUNT = 
            ParameterDescription.parameter( "fields", StringConversion.integer() )
            .withDefault( 1 ).positive( Num.integer );
    
    /**
     * The specification of the resolution parameter.
     */
    static  final ParameterDescription<String, String> RESOLUTION = 
            ParameterDescription.parameter( "res", StringConversion.string() )
            .withDefault( "med" ).allowedValues( "hi", "med", "lo" );
    
    /**
     * The specification of the cameraSequenceMask parameter.
     */
    static  final ParameterDescription<String, String> CAMERA_SEQUENCE_MASK = 
            ParameterDescription.parameter( "seq", StringConversion.onlyHexDigits() ).withDefault( "0" );
    
    /**
     * The specification of the connectionID parameter.
     */
    static  final ParameterDescription<Integer, Integer> CONNECTION_ID = 
            ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the maximumTransmitRate parameter.
     */
    static  final ParameterDescription<Integer, Integer> MAXIMUM_TRANSMIT_RATE = 
            ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the duration parameter.
     */
    static  final ParameterDescription<Integer, Integer> DURATION = 
            ParameterDescription.parameter( "duration", StringConversion.integer() )
            .withDefault( 0 ).notNegative( Num.integer );
    
    /**
     * The specification of the packetSize parameter.
     */
    static  final ParameterDescription<Integer, Integer> PACKET_SIZE = 
            ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0,
            ParameterDescription.parameter( "pkt_size", StringConversion.integer() ).withDefault( 0 ) );
    
    /**
     * The specification of the udpPort parameter.
     */
    static  final ParameterDescription<Integer, Integer> UDP_PORT = 
            ParameterDescription.parameter( "udp_port", StringConversion.integer() ).withDefault( 0 )
            .withBounds( 0, 65535, Num.integer );
    
    /**
     * The specification of the format parameter.
     */
    static  final ParameterDescription<VideoFormat, VideoFormat> FORMAT = 
            ParameterDescription.parameter( "format",
            StringConversion.convenientPartial( VideoFormat.fromStringFunction() ) )
            .withDefault( VideoFormat.JFIF );
    
    /**
     * The specification of the audioChannel parameter.
     */
    static  final ParameterDescription<Integer, Integer> AUDIO_CHANNEL = 
            ParameterDescription.parameter( "audio", StringConversion.integer() )
            .withDefault( 0 ).positive( Num.integer );
    
    /**
     * The specification of the transmissionMode parameter.
     */
    static  final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE = 
            ParameterDescription.parameterWithDefault( "txmode", new Function<ParameterMap, TransmissionMode>()
            {
                @Override
                public TransmissionMode apply( ParameterMap map )
                {
                    return map.get( FORMAT ) == VideoFormat.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL;
                }
            }, StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) );
    
    /**
     * The specification of the slaveIP parameter.
     */
    static  final ParameterDescription<String, String> SLAVE_IP = 
            ParameterDescription.parameter( "slaveip", StringConversion.string() )
            .withDefault( "0.0.0.0" );
    
    /**
     * The specification of the outputChannel parameter.
     */
    static  final ParameterDescription<Integer, Integer> OUTPUT_CHANNEL = 
            ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 );
    
    /**
     * The specification of the proxyMode parameter.
     */
    static  final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = 
            ParameterDescription.parameter( "proxymode",
            StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault( ProxyMode.TRANSIENT );
    
    /**
     * The specification of the proxyRetries parameter.
     */
    static  final ParameterDescription<Integer, Integer> PROXY_RETRIES = 
            ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * All the common parameter specifications, used in parsing URLs.
     */
    static final List<ParameterDescription<?, ?>> commonParams = 
            new ArrayList<ParameterDescription<?, ?>>();
    
    static
    {
        commonParams.add( CAMERA );
        commonParams.add( FIELD_COUNT );
        commonParams.add( RESOLUTION );
        commonParams.add( CAMERA_SEQUENCE_MASK );
        commonParams.add( CONNECTION_ID );
        commonParams.add( MAXIMUM_TRANSMIT_RATE );
        commonParams.add( DURATION );
        commonParams.add( PACKET_SIZE );
        commonParams.add( UDP_PORT );
        commonParams.add( FORMAT );
        commonParams.add( AUDIO_CHANNEL );
        commonParams.add( TRANSMISSION_MODE );
        commonParams.add( SLAVE_IP );
        commonParams.add( OUTPUT_CHANNEL );
        commonParams.add( PROXY_MODE );
        commonParams.add( PROXY_RETRIES );
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
    public String getSlaveIP()
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
     * A common supertype of DisplayPicCGI.Builder and ReplayPicCGI.Builder containing common code.
     * @param <Builder>
     *        The type of the subclass of AbstractBuilder.
     */
    static abstract class AbstractBuilder<Builder>
    {
        /**
         * Gives this instance as a Builder, instead of an AbstractBuilder&lt;Builder&gt;.
         * 
         * @return this instance as a Builder
         */
        abstract Builder self();
        /**
         * The values supplied for each parameter so far.
         * When this is an empty Option, the Builder is in an invalid state, the reason for
         * which is stored in the Option.
         */
        Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );
        
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
        public Builder slaveIP( final String slaveIP )
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
         * Sets the value of a parameter to a given value, and returns the Builder.
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
        <T>Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            CheckParameters.areNotNull( parameter, value );
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }
            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return self();
        }
        
    }
}

