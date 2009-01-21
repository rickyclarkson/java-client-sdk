package uk.org.netvu.protocol;
import java.util.*;
import static uk.org.netvu.protocol.ParameterDescription.*;
import static uk.org.netvu.protocol.StringConversion.*;
import uk.org.netvu.util.CheckParameters;   

/**
 * A parameter list for a display_pic.cgi query.
 * Use {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or {@link DisplayPicCGI#fromURL(String)}.
 */
public final class DisplayPicCGI
{
    /**
     * The ParameterMap to get values from.
     */
    private final ParameterMap parameterMap;
    
    /**
     * Constructs a DisplayPicCGI, using the values from the specified ParameterMap.
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    DisplayPicCGI( ParameterMap parameterMap )
    {
        CheckParameters.areNotNull( parameterMap );
        this.parameterMap = parameterMap;
    }
    
    /**
     * All the parameter specifications, used in parsing URLs.
     */
    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();;
    
    /**
     * The specification of the cam parameter.
     */
    private static final ParameterDescription<Integer, Integer> CAM = parameter("cam", integer()).withDefault(1).withBounds(1, 16, Num.integer);
    
    /**
     * The specification of the fields parameter.
     */
    private static final ParameterDescription<Integer, Integer> FIELDS = parameter("fields", integer()).withDefault(1).positive(Num.integer);
    
    /**
     * The specification of the res parameter.
     */
    private static final ParameterDescription<String, String> RES = parameter("res", string()).withDefault("med").allowedValues("hi", "med", "lo");
    
    /**
     * The specification of the seq parameter.
     */
    private static final ParameterDescription<Integer, Integer> SEQ = parameter("seq", hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer);
    
    /**
     * The specification of the dwell parameter.
     */
    private static final ParameterDescription<Integer, Integer> DWELL = parameter("dwell", integer()).withDefault(0);
    
    /**
     * The specification of the id parameter.
     */
    private static final ParameterDescription<Integer, Integer> ID = parameter("id", integer()).withDefault(0);
    
    /**
     * The specification of the dIndex parameter.
     */
    private static final ParameterDescription<Integer, Integer> DINDEX = parameter("dindex", integer()).withDefault(0);
    
    /**
     * The specification of the presel parameter.
     */
    private static final ParameterDescription<Integer, Integer> PRESEL = parameter("presel", integer()).withDefault(0).withBounds(0, 3, Num.integer);
    
    /**
     * The specification of the channel parameter.
     */
    private static final ParameterDescription<Integer, Integer> CHANNEL = parameter("channel", integer()).withDefault(-1).withBounds(-1, 1, Num.integer);
    
    /**
     * The specification of the rate parameter.
     */
    private static final ParameterDescription<Integer, Integer> RATE = parameter("rate", integer()).withDefault(0);
    
    /**
     * The specification of the forcedQ parameter.
     */
    private static final ParameterDescription<Integer, Integer> FORCED_Q = parameter("forcedq", integer()).withDefault(0).withBounds(0, 255, Num.integer).disallowing(1);
    
    /**
     * The specification of the duration parameter.
     */
    private static final ParameterDescription<Integer, Integer> DURATION = parameter("duration", integer()).withDefault(0).notNegative(Num.integer);
    
    /**
     * The specification of the nBuffers parameter.
     */
    private static final ParameterDescription<Integer, Integer> N_BUFFERS = parameter("nbuffers", integer()).withDefault(0).notNegative(Num.integer);
    
    /**
     * The specification of the telemQ parameter.
     */
    private static final ParameterDescription<Integer, Integer> TELEM_Q = parameter("telemQ", integer()).withDefault(-1).withBounds(-1, Integer.MAX_VALUE, Num.integer);
    
    /**
     * The specification of the pktSize parameter.
     */
    private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameter("pkt_size", integer()).withDefault(0));
    
    /**
     * The specification of the udpPort parameter.
     */
    private static final ParameterDescription<Integer, Integer> UDP_PORT = parameter("udp_port", integer()).withDefault(0).withBounds(0, 65535, Num.integer);
    
    /**
     * The specification of the audio parameter.
     */
    private static final ParameterDescription<String, String> AUDIO = parameter("audio", string()).withDefault("0").allowedValues("on", "off", "0", "1", "2");
    
    /**
     * The specification of the format parameter.
     */
    private static final ParameterDescription<Format, Format> FORMAT = parameter("format", convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF);
    
    /**
     * The specification of the audioMode parameter.
     */
    private static final ParameterDescription<AudioMode, AudioMode> AUDIO_MODE = parameter("audmode", convenientPartial(AudioMode.fromStringFunction())).withDefault(AudioMode.UDP);
    
    /**
     * The specification of the transmissionMode parameter.
     */
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
    
    /**
     * The specification of the pps parameter.
     */
    private static final ParameterDescription<Integer, Integer> PPS = parameter("pps", integer()).withDefault(0);
    
    /**
     * The specification of the mp4Rate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MP4_RATE = parameter("mp4rate", integer()).withDefault(0);
    
    /**
     * The specification of the slaveIP parameter.
     */
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameter("slaveip", convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get());
    
    /**
     * The specification of the opChan parameter.
     */
    private static final ParameterDescription<Integer, Integer> OP_CHAN = parameter("opchan", integer()).withDefault(-1);
    
    /**
     * The specification of the proxyMode parameter.
     */
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameter("proxymode", convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT);
    
    /**
     * The specification of the proxyPri parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_PRI = parameter("proxypri", integer()).withDefault(1);
    
    /**
     * The specification of the proxyRetry parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameter("proxyretry", integer()).withDefault(0);
    
    /**
     * Gets the value of the cam parameter.
     * @return the value of the cam parameter.
     */
    public int getCam(  )
    {
        return parameterMap.get( CAM );
    }
    
    /**
     * Gets the value of the fields parameter.
     * @return the value of the fields parameter.
     */
    public int getFields(  )
    {
        return parameterMap.get( FIELDS );
    }
    
    /**
     * Gets the value of the res parameter.
     * @return the value of the res parameter.
     */
    public String getRes(  )
    {
        return parameterMap.get( RES );
    }
    
    /**
     * Gets the value of the seq parameter.
     * @return the value of the seq parameter.
     */
    public int getSeq(  )
    {
        return parameterMap.get( SEQ );
    }
    
    /**
     * Gets the value of the dwell parameter.
     * @return the value of the dwell parameter.
     */
    public int getDwell(  )
    {
        return parameterMap.get( DWELL );
    }
    
    /**
     * Gets the value of the id parameter.
     * @return the value of the id parameter.
     */
    public int getId(  )
    {
        return parameterMap.get( ID );
    }
    
    /**
     * Gets the value of the dIndex parameter.
     * @return the value of the dIndex parameter.
     */
    public int getDIndex(  )
    {
        return parameterMap.get( DINDEX );
    }
    
    /**
     * Gets the value of the presel parameter.
     * @return the value of the presel parameter.
     */
    public int getPresel(  )
    {
        return parameterMap.get( PRESEL );
    }
    
    /**
     * Gets the value of the channel parameter.
     * @return the value of the channel parameter.
     */
    public int getChannel(  )
    {
        return parameterMap.get( CHANNEL );
    }
    
    /**
     * Gets the value of the rate parameter.
     * @return the value of the rate parameter.
     */
    public int getRate(  )
    {
        return parameterMap.get( RATE );
    }
    
    /**
     * Gets the value of the forcedQ parameter.
     * @return the value of the forcedQ parameter.
     */
    public int getForcedQ(  )
    {
        return parameterMap.get( FORCED_Q );
    }
    
    /**
     * Gets the value of the duration parameter.
     * @return the value of the duration parameter.
     */
    public int getDuration(  )
    {
        return parameterMap.get( DURATION );
    }
    
    /**
     * Gets the value of the nBuffers parameter.
     * @return the value of the nBuffers parameter.
     */
    public int getNBuffers(  )
    {
        return parameterMap.get( N_BUFFERS );
    }
    
    /**
     * Gets the value of the telemQ parameter.
     * @return the value of the telemQ parameter.
     */
    public int getTelemQ(  )
    {
        return parameterMap.get( TELEM_Q );
    }
    
    /**
     * Gets the value of the pktSize parameter.
     * @return the value of the pktSize parameter.
     */
    public int getPktSize(  )
    {
        return parameterMap.get( PKT_SIZE );
    }
    
    /**
     * Gets the value of the udpPort parameter.
     * @return the value of the udpPort parameter.
     */
    public int getUdpPort(  )
    {
        return parameterMap.get( UDP_PORT );
    }
    
    /**
     * Gets the value of the audio parameter.
     * @return the value of the audio parameter.
     */
    public String getAudio(  )
    {
        return parameterMap.get( AUDIO );
    }
    
    /**
     * Gets the value of the format parameter.
     * @return the value of the format parameter.
     */
    public Format getFormat(  )
    {
        return parameterMap.get( FORMAT );
    }
    
    /**
     * Gets the value of the audioMode parameter.
     * @return the value of the audioMode parameter.
     */
    public AudioMode getAudioMode(  )
    {
        return parameterMap.get( AUDIO_MODE );
    }
    
    /**
     * Gets the value of the transmissionMode parameter.
     * @return the value of the transmissionMode parameter.
     */
    public TransmissionMode getTransmissionMode(  )
    {
        return parameterMap.get( TRANSMISSION_MODE );
    }
    
    /**
     * Gets the value of the pps parameter.
     * @return the value of the pps parameter.
     */
    public int getPPS(  )
    {
        return parameterMap.get( PPS );
    }
    
    /**
     * Gets the value of the mp4Rate parameter.
     * @return the value of the mp4Rate parameter.
     */
    public int getMp4Rate(  )
    {
        return parameterMap.get( MP4_RATE );
    }
    
    /**
     * Gets the value of the slaveIP parameter.
     * @return the value of the slaveIP parameter.
     */
    public IPAddress getSlaveIP(  )
    {
        return parameterMap.get( SLAVE_IP );
    }
    
    /**
     * Gets the value of the opChan parameter.
     * @return the value of the opChan parameter.
     */
    public int getOpChan(  )
    {
        return parameterMap.get( OP_CHAN );
    }
    
    /**
     * Gets the value of the proxyMode parameter.
     * @return the value of the proxyMode parameter.
     */
    public ProxyMode getProxyMode(  )
    {
        return parameterMap.get( PROXY_MODE );
    }
    
    /**
     * Gets the value of the proxyPri parameter.
     * @return the value of the proxyPri parameter.
     */
    public int getProxyPri(  )
    {
        return parameterMap.get( PROXY_PRI );
    }
    
    /**
     * Gets the value of the proxyRetry parameter.
     * @return the value of the proxyRetry parameter.
     */
    public int getProxyRetry(  )
    {
        return parameterMap.get( PROXY_RETRY );
    }
    
    /**
     * A builder that takes in all the optional values for DisplayPicCGI and produces a DisplayPicCGI when build() is
     * called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can only have
     * build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its values after
     * calling build() will cause an IllegalStateException.
     */
    public static final class Builder
    {
        /**
         * The values supplied for each parameter so far.
         * When this is an empty Option, the Builder is in an invalid state, the reason for which is stored in the Option.
         */
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );;
        
        /**
         * Sets the cam parameter in the builder.
         * @param cam
         *        the value to store as the cam parameter.
         * @throws NullPointerException
         *         if cam is null.
         * @return the Builder
         */
        public Builder cam( Integer cam )
        {
            CheckParameters.areNotNull( cam );
            return set( CAM, cam );
        }
        
        /**
         * Sets the fields parameter in the builder.
         * @param fields
         *        the value to store as the fields parameter.
         * @throws NullPointerException
         *         if fields is null.
         * @return the Builder
         */
        public Builder fields( Integer fields )
        {
            CheckParameters.areNotNull( fields );
            return set( FIELDS, fields );
        }
        
        /**
         * Sets the res parameter in the builder.
         * @param res
         *        the value to store as the res parameter.
         * @throws NullPointerException
         *         if res is null.
         * @return the Builder
         */
        public Builder res( String res )
        {
            CheckParameters.areNotNull( res );
            return set( RES, res );
        }
        
        /**
         * Sets the seq parameter in the builder.
         * @param seq
         *        the value to store as the seq parameter.
         * @throws NullPointerException
         *         if seq is null.
         * @return the Builder
         */
        public Builder seq( Integer seq )
        {
            CheckParameters.areNotNull( seq );
            return set( SEQ, seq );
        }
        
        /**
         * Sets the dwell parameter in the builder.
         * @param dwell
         *        the value to store as the dwell parameter.
         * @throws NullPointerException
         *         if dwell is null.
         * @return the Builder
         */
        public Builder dwell( Integer dwell )
        {
            CheckParameters.areNotNull( dwell );
            return set( DWELL, dwell );
        }
        
        /**
         * Sets the id parameter in the builder.
         * @param id
         *        the value to store as the id parameter.
         * @throws NullPointerException
         *         if id is null.
         * @return the Builder
         */
        public Builder id( Integer id )
        {
            CheckParameters.areNotNull( id );
            return set( ID, id );
        }
        
        /**
         * Sets the dIndex parameter in the builder.
         * @param dIndex
         *        the value to store as the dIndex parameter.
         * @throws NullPointerException
         *         if dIndex is null.
         * @return the Builder
         */
        public Builder dIndex( Integer dIndex )
        {
            CheckParameters.areNotNull( dIndex );
            return set( DINDEX, dIndex );
        }
        
        /**
         * Sets the presel parameter in the builder.
         * @param presel
         *        the value to store as the presel parameter.
         * @throws NullPointerException
         *         if presel is null.
         * @return the Builder
         */
        public Builder presel( Integer presel )
        {
            CheckParameters.areNotNull( presel );
            return set( PRESEL, presel );
        }
        
        /**
         * Sets the channel parameter in the builder.
         * @param channel
         *        the value to store as the channel parameter.
         * @throws NullPointerException
         *         if channel is null.
         * @return the Builder
         */
        public Builder channel( Integer channel )
        {
            CheckParameters.areNotNull( channel );
            return set( CHANNEL, channel );
        }
        
        /**
         * Sets the rate parameter in the builder.
         * @param rate
         *        the value to store as the rate parameter.
         * @throws NullPointerException
         *         if rate is null.
         * @return the Builder
         */
        public Builder rate( Integer rate )
        {
            CheckParameters.areNotNull( rate );
            return set( RATE, rate );
        }
        
        /**
         * Sets the forcedQ parameter in the builder.
         * @param forcedQ
         *        the value to store as the forcedQ parameter.
         * @throws NullPointerException
         *         if forcedQ is null.
         * @return the Builder
         */
        public Builder forcedQ( Integer forcedQ )
        {
            CheckParameters.areNotNull( forcedQ );
            return set( FORCED_Q, forcedQ );
        }
        
        /**
         * Sets the duration parameter in the builder.
         * @param duration
         *        the value to store as the duration parameter.
         * @throws NullPointerException
         *         if duration is null.
         * @return the Builder
         */
        public Builder duration( Integer duration )
        {
            CheckParameters.areNotNull( duration );
            return set( DURATION, duration );
        }
        
        /**
         * Sets the nBuffers parameter in the builder.
         * @param nBuffers
         *        the value to store as the nBuffers parameter.
         * @throws NullPointerException
         *         if nBuffers is null.
         * @return the Builder
         */
        public Builder nBuffers( Integer nBuffers )
        {
            CheckParameters.areNotNull( nBuffers );
            return set( N_BUFFERS, nBuffers );
        }
        
        /**
         * Sets the telemQ parameter in the builder.
         * @param telemQ
         *        the value to store as the telemQ parameter.
         * @throws NullPointerException
         *         if telemQ is null.
         * @return the Builder
         */
        public Builder telemQ( Integer telemQ )
        {
            CheckParameters.areNotNull( telemQ );
            return set( TELEM_Q, telemQ );
        }
        
        /**
         * Sets the pktSize parameter in the builder.
         * @param pktSize
         *        the value to store as the pktSize parameter.
         * @throws NullPointerException
         *         if pktSize is null.
         * @return the Builder
         */
        public Builder pktSize( Integer pktSize )
        {
            CheckParameters.areNotNull( pktSize );
            return set( PKT_SIZE, pktSize );
        }
        
        /**
         * Sets the udpPort parameter in the builder.
         * @param udpPort
         *        the value to store as the udpPort parameter.
         * @throws NullPointerException
         *         if udpPort is null.
         * @return the Builder
         */
        public Builder udpPort( Integer udpPort )
        {
            CheckParameters.areNotNull( udpPort );
            return set( UDP_PORT, udpPort );
        }
        
        /**
         * Sets the audio parameter in the builder.
         * @param audio
         *        the value to store as the audio parameter.
         * @throws NullPointerException
         *         if audio is null.
         * @return the Builder
         */
        public Builder audio( String audio )
        {
            CheckParameters.areNotNull( audio );
            return set( AUDIO, audio );
        }
        
        /**
         * Sets the format parameter in the builder.
         * @param format
         *        the value to store as the format parameter.
         * @throws NullPointerException
         *         if format is null.
         * @return the Builder
         */
        public Builder format( Format format )
        {
            CheckParameters.areNotNull( format );
            return set( FORMAT, format );
        }
        
        /**
         * Sets the audioMode parameter in the builder.
         * @param audioMode
         *        the value to store as the audioMode parameter.
         * @throws NullPointerException
         *         if audioMode is null.
         * @return the Builder
         */
        public Builder audioMode( AudioMode audioMode )
        {
            CheckParameters.areNotNull( audioMode );
            return set( AUDIO_MODE, audioMode );
        }
        
        /**
         * Sets the transmissionMode parameter in the builder.
         * @param transmissionMode
         *        the value to store as the transmissionMode parameter.
         * @throws NullPointerException
         *         if transmissionMode is null.
         * @return the Builder
         */
        public Builder transmissionMode( TransmissionMode transmissionMode )
        {
            CheckParameters.areNotNull( transmissionMode );
            return set( TRANSMISSION_MODE, transmissionMode );
        }
        
        /**
         * Sets the pps parameter in the builder.
         * @param pps
         *        the value to store as the pps parameter.
         * @throws NullPointerException
         *         if pps is null.
         * @return the Builder
         */
        public Builder pps( Integer pps )
        {
            CheckParameters.areNotNull( pps );
            return set( PPS, pps );
        }
        
        /**
         * Sets the mp4Rate parameter in the builder.
         * @param mp4Rate
         *        the value to store as the mp4Rate parameter.
         * @throws NullPointerException
         *         if mp4Rate is null.
         * @return the Builder
         */
        public Builder mp4Rate( Integer mp4Rate )
        {
            CheckParameters.areNotNull( mp4Rate );
            return set( MP4_RATE, mp4Rate );
        }
        
        /**
         * Sets the slaveIP parameter in the builder.
         * @param slaveIP
         *        the value to store as the slaveIP parameter.
         * @throws NullPointerException
         *         if slaveIP is null.
         * @return the Builder
         */
        public Builder slaveIP( IPAddress slaveIP )
        {
            CheckParameters.areNotNull( slaveIP );
            return set( SLAVE_IP, slaveIP );
        }
        
        /**
         * Sets the opChan parameter in the builder.
         * @param opChan
         *        the value to store as the opChan parameter.
         * @throws NullPointerException
         *         if opChan is null.
         * @return the Builder
         */
        public Builder opChan( Integer opChan )
        {
            CheckParameters.areNotNull( opChan );
            return set( OP_CHAN, opChan );
        }
        
        /**
         * Sets the proxyMode parameter in the builder.
         * @param proxyMode
         *        the value to store as the proxyMode parameter.
         * @throws NullPointerException
         *         if proxyMode is null.
         * @return the Builder
         */
        public Builder proxyMode( ProxyMode proxyMode )
        {
            CheckParameters.areNotNull( proxyMode );
            return set( PROXY_MODE, proxyMode );
        }
        
        /**
         * Sets the proxyPri parameter in the builder.
         * @param proxyPri
         *        the value to store as the proxyPri parameter.
         * @throws NullPointerException
         *         if proxyPri is null.
         * @return the Builder
         */
        public Builder proxyPri( Integer proxyPri )
        {
            CheckParameters.areNotNull( proxyPri );
            return set( PROXY_PRI, proxyPri );
        }
        
        /**
         * Sets the proxyRetry parameter in the builder.
         * @param proxyRetry
         *        the value to store as the proxyRetry parameter.
         * @throws NullPointerException
         *         if proxyRetry is null.
         * @return the Builder
         */
        public Builder proxyRetry( Integer proxyRetry )
        {
            CheckParameters.areNotNull( proxyRetry );
            return set( PROXY_RETRY, proxyRetry );
        }
        
        /**
         * Sets the value of a parameter to a given value, and returns the Builder.
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
        * @throws IllegalStateException
        *         if this Builder has already been built.
        * @return a DisplayPicCGI containing the values from this Builder.
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
        params.add( CAM );
        params.add( FIELDS );
        params.add( RES );
        params.add( SEQ );
        params.add( DWELL );
        params.add( ID );
        params.add( DINDEX );
        params.add( PRESEL );
        params.add( CHANNEL );
        params.add( RATE );
        params.add( FORCED_Q );
        params.add( DURATION );
        params.add( N_BUFFERS );
        params.add( TELEM_Q );
        params.add( PKT_SIZE );
        params.add( UDP_PORT );
        params.add( AUDIO );
        params.add( FORMAT );
        params.add( AUDIO_MODE );
        params.add( TRANSMISSION_MODE );
        params.add( PPS );
        params.add( MP4_RATE );
        params.add( SLAVE_IP );
        params.add( OP_CHAN );
        params.add( PROXY_MODE );
        params.add( PROXY_PRI );
        params.add( PROXY_RETRY );
    }
    /**
     * The possible formats that the video stream can be returned as.
     */
    public static enum Format
    {
        /**
         * Complete JFIF (JPEG) image data
         */
        JFIF
        ,
        
        /**
         * Truncated JPEG image data
         */
        JPEG
        ,
        
        /**
         * MPEG-4 image data
         */
        MP4
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of Format if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a Format
         */
        static Function<String, Option<Format>> fromStringFunction(  )
        {
            return
            new Function<String, Option<Format>>()
            {
                public Option<Format> apply(String s )
                {
                    for ( final Format element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Format element " );
                }
            }
            ;
        }
        
    }
    /**
     * The possible mechanisms for returning audio data
     */
    public static enum AudioMode
    {
        /**
         * Out of band UDP data
         */
        UDP
        ,
        
        /**
         * In-band data interleaved with images
         */
        INLINE
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of AudioMode if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a AudioMode
         */
        static Function<String, Option<AudioMode>> fromStringFunction(  )
        {
            return
            new Function<String, Option<AudioMode>>()
            {
                public Option<AudioMode> apply(String s )
                {
                    for ( final AudioMode element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid AudioMode element " );
                }
            }
            ;
        }
        
    }
    /**
     * The possible stream headers that the video stream can be wrapped in.
     */
    public static enum TransmissionMode
    {
        /**
         * Multipart MIME
         */
        MIME
        ,
        
        /**
         * AD's 'binary' format
         */
        BINARY
        ,
        
        /**
         * AD's 'minimal' format
         */
        MINIMAL
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of TransmissionMode if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a TransmissionMode
         */
        static Function<String, Option<TransmissionMode>> fromStringFunction(  )
        {
            return
            new Function<String, Option<TransmissionMode>>()
            {
                public Option<TransmissionMode> apply(String s )
                {
                    for ( final TransmissionMode element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid TransmissionMode element " );
                }
            }
            ;
        }
        
    }
    /**
     * This controls whether or not a decoder that is connected to by the server maintains connections to cameras set up by the CGI request
     */
    public static enum ProxyMode
    {
        /**
         * A decoder will clear connections to cameras made by the CGI request after the video stream has terminated
         */
        TRANSIENT
        ,
        
        /**
         * A decoder will maintain connections to cameras made by the CGI request after the video stream has terminated
         */
        PERSISTENT
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of ProxyMode if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a ProxyMode
         */
        static Function<String, Option<ProxyMode>> fromStringFunction(  )
        {
            return
            new Function<String, Option<ProxyMode>>()
            {
                public Option<ProxyMode> apply(String s )
                {
                    for ( final ProxyMode element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid ProxyMode element " );
                }
            }
            ;
        }
        
    }
    @Override
    public String toString()
    {
        return "/display_pic.cgi?" + parameterMap.toURLParameters( params );
    }
    public static DisplayPicCGI fromString( String string )
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

