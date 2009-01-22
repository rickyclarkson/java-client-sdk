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
     * The specification of the cam parameter.
     */
    private static final ParameterDescription<Integer, Integer> CAM =
            ParameterDescription.parameter( "cam", StringConversion.integer() ).withDefault( 1 ).withBounds( 1, 16,
                    Num.integer );

    /**
     * The specification of the fields parameter.
     */
    private static final ParameterDescription<Integer, Integer> FIELDS =
            ParameterDescription.parameter( "fields", StringConversion.integer() ).withDefault( 1 ).positive(
                    Num.integer );

    /**
     * The specification of the res parameter.
     */
    private static final ParameterDescription<String, String> RES =
            ParameterDescription.parameter( "res", StringConversion.string() ).withDefault( "med" ).allowedValues(
                    "hi", "med", "lo" );

    /**
     * The specification of the seq parameter.
     */
    private static final ParameterDescription<Integer, Integer> SEQ =
            ParameterDescription.parameter( "seq", StringConversion.hexInt() ).withDefault( 0 ).withBounds( 0, 0xF,
                    Num.integer );

    /**
     * The specification of the dwell parameter.
     */
    private static final ParameterDescription<Integer, Integer> DWELL =
            ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the id parameter.
     */
    private static final ParameterDescription<Integer, Integer> ID =
            ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the dIndex parameter.
     */
    private static final ParameterDescription<Integer, Integer> DINDEX =
            ParameterDescription.parameter( "dindex", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the presel parameter.
     */
    private static final ParameterDescription<Integer, Integer> PRESEL =
            ParameterDescription.parameter( "presel", StringConversion.integer() ).withDefault( 0 ).withBounds( 0, 3,
                    Num.integer );

    /**
     * The specification of the channel parameter.
     */
    private static final ParameterDescription<Integer, Integer> CHANNEL =
            ParameterDescription.parameter( "channel", StringConversion.integer() ).withDefault( -1 ).withBounds( -1,
                    1, Num.integer );

    /**
     * The specification of the rate parameter.
     */
    private static final ParameterDescription<Integer, Integer> RATE =
            ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the forcedQ parameter.
     */
    private static final ParameterDescription<Integer, Integer> FORCED_Q =
            ParameterDescription.parameter( "forcedq", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    255, Num.integer ).disallowing( 1 );

    /**
     * The specification of the duration parameter.
     */
    private static final ParameterDescription<Integer, Integer> DURATION =
            ParameterDescription.parameter( "duration", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );

    /**
     * The specification of the nBuffers parameter.
     */
    private static final ParameterDescription<Integer, Integer> N_BUFFERS =
            ParameterDescription.parameter( "nbuffers", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );

    /**
     * The specification of the telemQ parameter.
     */
    private static final ParameterDescription<Integer, Integer> TELEM_Q =
            ParameterDescription.parameter( "telemQ", StringConversion.integer() ).withDefault( -1 ).withBounds( -1,
                    Integer.MAX_VALUE, Num.integer );

    /**
     * The specification of the pktSize parameter.
     */
    private static final ParameterDescription<Integer, Integer> PKT_SIZE =
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
     * The specification of the pps parameter.
     */
    private static final ParameterDescription<Integer, Integer> PPS =
            ParameterDescription.parameter( "pps", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the mp4Rate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MP4_RATE =
            ParameterDescription.parameter( "mp4rate", StringConversion.integer() ).withDefault( 0 );

    /**
     * The specification of the slaveIP parameter.
     */
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP =
            ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial( IPAddress.fromString ) )
                .withDefault( IPAddress.fromString( "0.0.0.0" ).get() );

    /**
     * The specification of the opChan parameter.
     */
    private static final ParameterDescription<Integer, Integer> OP_CHAN =
            ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 );

    /**
     * The specification of the proxyMode parameter.
     */
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE =
            ParameterDescription.parameter( "proxymode",
                    StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault(
                    ProxyMode.TRANSIENT );

    /**
     * The specification of the proxyPri parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_PRI =
            ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 );

    /**
     * The specification of the proxyRetry parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY =
            ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 );

    /**
     * Gets the value of the cam parameter.
     * 
     * @return the value of the cam parameter.
     */
    public int getCam()
    {
        return parameterMap.get( CAM );
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
     * Gets the value of the res parameter.
     * 
     * @return the value of the res parameter.
     */
    public String getRes()
    {
        return parameterMap.get( RES );
    }

    /**
     * Gets the value of the seq parameter.
     * 
     * @return the value of the seq parameter.
     */
    public int getSeq()
    {
        return parameterMap.get( SEQ );
    }

    /**
     * Gets the value of the dwell parameter.
     * 
     * @return the value of the dwell parameter.
     */
    public int getDwell()
    {
        return parameterMap.get( DWELL );
    }

    /**
     * Gets the value of the id parameter.
     * 
     * @return the value of the id parameter.
     */
    public int getId()
    {
        return parameterMap.get( ID );
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
     * Gets the value of the presel parameter.
     * 
     * @return the value of the presel parameter.
     */
    public int getPresel()
    {
        return parameterMap.get( PRESEL );
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
     * Gets the value of the rate parameter.
     * 
     * @return the value of the rate parameter.
     */
    public int getRate()
    {
        return parameterMap.get( RATE );
    }

    /**
     * Gets the value of the forcedQ parameter.
     * 
     * @return the value of the forcedQ parameter.
     */
    public int getForcedQ()
    {
        return parameterMap.get( FORCED_Q );
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
     * Gets the value of the nBuffers parameter.
     * 
     * @return the value of the nBuffers parameter.
     */
    public int getNBuffers()
    {
        return parameterMap.get( N_BUFFERS );
    }

    /**
     * Gets the value of the telemQ parameter.
     * 
     * @return the value of the telemQ parameter.
     */
    public int getTelemQ()
    {
        return parameterMap.get( TELEM_Q );
    }

    /**
     * Gets the value of the pktSize parameter.
     * 
     * @return the value of the pktSize parameter.
     */
    public int getPktSize()
    {
        return parameterMap.get( PKT_SIZE );
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
     * Gets the value of the pps parameter.
     * 
     * @return the value of the pps parameter.
     */
    public int getPPS()
    {
        return parameterMap.get( PPS );
    }

    /**
     * Gets the value of the mp4Rate parameter.
     * 
     * @return the value of the mp4Rate parameter.
     */
    public int getMp4Rate()
    {
        return parameterMap.get( MP4_RATE );
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
     * Gets the value of the opChan parameter.
     * 
     * @return the value of the opChan parameter.
     */
    public int getOpChan()
    {
        return parameterMap.get( OP_CHAN );
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
     * Gets the value of the proxyPri parameter.
     * 
     * @return the value of the proxyPri parameter.
     */
    public int getProxyPri()
    {
        return parameterMap.get( PROXY_PRI );
    }

    /**
     * Gets the value of the proxyRetry parameter.
     * 
     * @return the value of the proxyRetry parameter.
     */
    public int getProxyRetry()
    {
        return parameterMap.get( PROXY_RETRY );
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
         * Sets the cam parameter in the builder.
         * 
         * @param cam
         *        the value to store as the cam parameter.
         * @return the Builder
         */
        public Builder cam( final int cam )
        {
            return set( CAM, cam );
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
         * Sets the res parameter in the builder.
         * 
         * @param res
         *        the value to store as the res parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if res is null.
         */
        public Builder res( final String res )
        {
            CheckParameters.areNotNull( res );
            return set( RES, res );
        }

        /**
         * Sets the seq parameter in the builder.
         * 
         * @param seq
         *        the value to store as the seq parameter.
         * @return the Builder
         */
        public Builder seq( final int seq )
        {
            return set( SEQ, seq );
        }

        /**
         * Sets the dwell parameter in the builder.
         * 
         * @param dwell
         *        the value to store as the dwell parameter.
         * @return the Builder
         */
        public Builder dwell( final int dwell )
        {
            return set( DWELL, dwell );
        }

        /**
         * Sets the id parameter in the builder.
         * 
         * @param id
         *        the value to store as the id parameter.
         * @return the Builder
         */
        public Builder id( final int id )
        {
            return set( ID, id );
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
         * Sets the presel parameter in the builder.
         * 
         * @param presel
         *        the value to store as the presel parameter.
         * @return the Builder
         */
        public Builder presel( final int presel )
        {
            return set( PRESEL, presel );
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
         * Sets the rate parameter in the builder.
         * 
         * @param rate
         *        the value to store as the rate parameter.
         * @return the Builder
         */
        public Builder rate( final int rate )
        {
            return set( RATE, rate );
        }

        /**
         * Sets the forcedQ parameter in the builder.
         * 
         * @param forcedQ
         *        the value to store as the forcedQ parameter.
         * @return the Builder
         */
        public Builder forcedQ( final int forcedQ )
        {
            return set( FORCED_Q, forcedQ );
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
         * Sets the nBuffers parameter in the builder.
         * 
         * @param nBuffers
         *        the value to store as the nBuffers parameter.
         * @return the Builder
         */
        public Builder nBuffers( final int nBuffers )
        {
            return set( N_BUFFERS, nBuffers );
        }

        /**
         * Sets the telemQ parameter in the builder.
         * 
         * @param telemQ
         *        the value to store as the telemQ parameter.
         * @return the Builder
         */
        public Builder telemQ( final int telemQ )
        {
            return set( TELEM_Q, telemQ );
        }

        /**
         * Sets the pktSize parameter in the builder.
         * 
         * @param pktSize
         *        the value to store as the pktSize parameter.
         * @return the Builder
         */
        public Builder pktSize( final int pktSize )
        {
            return set( PKT_SIZE, pktSize );
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
         * Sets the pps parameter in the builder.
         * 
         * @param pps
         *        the value to store as the pps parameter.
         * @return the Builder
         */
        public Builder pps( final int pps )
        {
            return set( PPS, pps );
        }

        /**
         * Sets the mp4Rate parameter in the builder.
         * 
         * @param mp4Rate
         *        the value to store as the mp4Rate parameter.
         * @return the Builder
         */
        public Builder mp4Rate( final int mp4Rate )
        {
            return set( MP4_RATE, mp4Rate );
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
         * Sets the opChan parameter in the builder.
         * 
         * @param opChan
         *        the value to store as the opChan parameter.
         * @return the Builder
         */
        public Builder opChan( final int opChan )
        {
            return set( OP_CHAN, opChan );
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
         * Sets the proxyPri parameter in the builder.
         * 
         * @param proxyPri
         *        the value to store as the proxyPri parameter.
         * @return the Builder
         */
        public Builder proxyPri( final int proxyPri )
        {
            return set( PROXY_PRI, proxyPri );
        }

        /**
         * Sets the proxyRetry parameter in the builder.
         * 
         * @param proxyRetry
         *        the value to store as the proxyRetry parameter.
         * @return the Builder
         */
        public Builder proxyRetry( final int proxyRetry )
        {
            return set( PROXY_RETRY, proxyRetry );
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
