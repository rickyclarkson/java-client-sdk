package uk.org.netvu.protocol;
import java.util.List;
import java.util.ArrayList;
import uk.org.netvu.util.CheckParameters;   

/**
 * A parameter list for a replay_pic.cgi query.
 * Use {@link ReplayPicCGI.Builder} to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromURL(String)}.
 */
public final class ReplayPicCGI
{
    /**
     * The ParameterMap to get values from.
     */
    private final ParameterMap parameterMap;
    
    /**
     * Constructs a ReplayPicCGI, using the values from the specified ParameterMap.
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    ReplayPicCGI( ParameterMap parameterMap )
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
    private static final ParameterDescription<Integer, Integer> CAM = ParameterDescription.parameter("cam", StringConversion.integer()).withDefault(1).withBounds(1, 16, Num.integer);
    
    /**
     * The specification of the fields parameter.
     */
    private static final ParameterDescription<Integer, Integer> FIELDS = ParameterDescription.parameter("fields", StringConversion.integer()).withDefault(1).notNegative(Num.integer);
    
    /**
     * The specification of the seq parameter.
     */
    private static final ParameterDescription<Integer, Integer> SEQ = ParameterDescription.parameter("seq", StringConversion.hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer);
    
    /**
     * The specification of the id parameter.
     */
    private static final ParameterDescription<Integer, Integer> ID = ParameterDescription.parameter("id", StringConversion.integer()).withDefault(0);
    
    /**
     * The specification of the control parameter.
     */
    private static final ParameterDescription<Control, Control> CONTROL = ParameterDescription.parameter("control", StringConversion.convenientPartial(Control.fromStringFunction())).withDefault(Control.STOP);
    
    /**
     * The specification of the time parameter.
     */
    private static final ParameterDescription<Integer, Integer> TIME = ParameterDescription.parameter("time", StringConversion.convenientPartial(fromTimeFunction())).withDefault(0);
    
    /**
     * The specification of the local parameter.
     */
    private static final ParameterDescription<Integer, Integer> LOCAL = ParameterDescription.parameter("local", StringConversion.convenientPartial(fromTimeFunction())).withDefault(0);
    
    /**
     * The specification of the rate parameter.
     */
    private static final ParameterDescription<Integer, Integer> RATE = ParameterDescription.parameter("rate", StringConversion.integer()).withDefault(0);
    
    /**
     * The specification of the text parameter.
     */
    private static final ParameterDescription<String, String> TEXT = ParameterDescription.parameter("text", StringConversion.string()).withDefault("");
    
    /**
     * The specification of the timeRange parameter.
     */
    private static final ParameterDescription<Integer, Integer> TIME_RANGE = ParameterDescription.parameter("timerange", StringConversion.integer()).withDefault(0);
    
    /**
     * The specification of the audioOn parameter.
     */
    private static final ParameterDescription<OnOrOff, OnOrOff> AUDIO = ParameterDescription.parameter("audio", StringConversion.convenientPartial(OnOrOff.fromStringFunction())).withDefault(OnOrOff.OFF);
    
    /**
     * The specification of the fastForwardMultiplier parameter.
     */
    private static final ParameterDescription<Integer, Integer> FAST_FORWARD_MULTIPLIER = ParameterDescription.parameter("ffmult", StringConversion.integer()).withDefault(0).withBounds(0, 256, Num.integer);
    
    /**
     * The specification of the duration parameter.
     */
    private static final ParameterDescription<Integer, Integer> DURATION = ParameterDescription.parameter("duration", StringConversion.integer()).withDefault(0).notNegative(Num.integer);
    
    /**
     * The specification of the res parameter.
     */
    private static final ParameterDescription<String, String> RES = ParameterDescription.parameter("res", StringConversion.string()).withDefault("med").allowedValues("hi", "med", "lo");
    
    /**
     * The specification of the pktSize parameter.
     */
    private static final ParameterDescription<Integer, Integer> PKT_SIZE = ParameterDescription.parameterWithBoundsAndAnException(100, 1500, 0, ParameterDescription.parameter("pkt_size", StringConversion.integer()).withDefault(0));
    
    /**
     * The specification of the udpPort parameter.
     */
    private static final ParameterDescription<Integer, Integer> UDP_PORT = ParameterDescription.parameter("udp_port", StringConversion.integer()).withDefault(0).withBounds(0, 65535, Num.integer);
    
    /**
     * The specification of the refresh parameter.
     */
    private static final ParameterDescription<Integer, Integer> REFRESH = ParameterDescription.parameter("refresh", StringConversion.integer()).withDefault(0);
    
    /**
     * The specification of the format parameter.
     */
    private static final ParameterDescription<Format, Format> FORMAT = ParameterDescription.parameter("format", StringConversion.convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF);
    
    /**
     * The specification of the transmissionMode parameter.
     */
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE = ParameterDescription.parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, StringConversion.convenientPartial(TransmissionMode.fromStringFunction()));
    
    /**
     * The specification of the slaveIP parameter.
     */
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = ParameterDescription.parameter("slaveip", StringConversion.convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get());
    
    /**
     * The specification of the opChan parameter.
     */
    private static final ParameterDescription<Integer, Integer> OP_CHAN = ParameterDescription.parameter("opchan", StringConversion.integer()).withDefault(-1);
    
    /**
     * The specification of the proxyMode parameter.
     */
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = ParameterDescription.parameter("proxymode", StringConversion.convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT);
    
    /**
     * The specification of the proxyRetry parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY = ParameterDescription.parameter("proxyretry", StringConversion.integer()).withDefault(0);
    
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
     * Gets the value of the seq parameter.
     * @return the value of the seq parameter.
     */
    public int getSeq(  )
    {
        return parameterMap.get( SEQ );
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
     * Gets the value of the control parameter.
     * @return the value of the control parameter.
     */
    public Control getControl(  )
    {
        return parameterMap.get( CONTROL );
    }
    
    /**
     * Gets the value of the time parameter.
     * @return the value of the time parameter.
     */
    public int getTime(  )
    {
        return parameterMap.get( TIME );
    }
    
    /**
     * Gets the value of the local parameter.
     * @return the value of the local parameter.
     */
    public int getLocal(  )
    {
        return parameterMap.get( LOCAL );
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
     * Gets the value of the text parameter.
     * @return the value of the text parameter.
     */
    public String getText(  )
    {
        return parameterMap.get( TEXT );
    }
    
    /**
     * Gets the value of the timeRange parameter.
     * @return the value of the timeRange parameter.
     */
    public int getTimeRange(  )
    {
        return parameterMap.get( TIME_RANGE );
    }
    
    /**
     * Gets the value of the audioOn parameter.
     * @return the value of the audioOn parameter.
     */
    public OnOrOff isAudioOn(  )
    {
        return parameterMap.get( AUDIO );
    }
    
    /**
     * Gets the value of the fastForwardMultiplier parameter.
     * @return the value of the fastForwardMultiplier parameter.
     */
    public int getFastForwardMultiplier(  )
    {
        return parameterMap.get( FAST_FORWARD_MULTIPLIER );
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
     * Gets the value of the res parameter.
     * @return the value of the res parameter.
     */
    public String getRes(  )
    {
        return parameterMap.get( RES );
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
     * Gets the value of the refresh parameter.
     * @return the value of the refresh parameter.
     */
    public int getRefresh(  )
    {
        return parameterMap.get( REFRESH );
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
     * Gets the value of the transmissionMode parameter.
     * @return the value of the transmissionMode parameter.
     */
    public TransmissionMode getTransmissionMode(  )
    {
        return parameterMap.get( TRANSMISSION_MODE );
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
     * Gets the value of the proxyRetry parameter.
     * @return the value of the proxyRetry parameter.
     */
    public int getProxyRetry(  )
    {
        return parameterMap.get( PROXY_RETRY );
    }
    
    /**
     * A builder that takes in all the optional values for ReplayPicCGI and produces a ReplayPicCGI when build() is
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
         * @return the Builder
         */
        public Builder cam( int cam )
        {
            return set( CAM, cam );
        }
        
        /**
         * Sets the fields parameter in the builder.
         * @param fields
         *        the value to store as the fields parameter.
         * @return the Builder
         */
        public Builder fields( int fields )
        {
            return set( FIELDS, fields );
        }
        
        /**
         * Sets the seq parameter in the builder.
         * @param seq
         *        the value to store as the seq parameter.
         * @return the Builder
         */
        public Builder seq( int seq )
        {
            return set( SEQ, seq );
        }
        
        /**
         * Sets the id parameter in the builder.
         * @param id
         *        the value to store as the id parameter.
         * @return the Builder
         */
        public Builder id( int id )
        {
            return set( ID, id );
        }
        
        /**
         * Sets the control parameter in the builder.
         * @param control
         *        the value to store as the control parameter.
         * @throws NullPointerException
         *         if control is null.
         * @return the Builder
         */
        public Builder control( Control control )
        {
            CheckParameters.areNotNull( control );
            return set( CONTROL, control );
        }
        
        /**
         * Sets the time parameter in the builder.
         * @param time
         *        the value to store as the time parameter.
         * @return the Builder
         */
        public Builder time( int time )
        {
            return set( TIME, time );
        }
        
        /**
         * Sets the local parameter in the builder.
         * @param local
         *        the value to store as the local parameter.
         * @return the Builder
         */
        public Builder local( int local )
        {
            return set( LOCAL, local );
        }
        
        /**
         * Sets the rate parameter in the builder.
         * @param rate
         *        the value to store as the rate parameter.
         * @return the Builder
         */
        public Builder rate( int rate )
        {
            return set( RATE, rate );
        }
        
        /**
         * Sets the text parameter in the builder.
         * @param text
         *        the value to store as the text parameter.
         * @throws NullPointerException
         *         if text is null.
         * @return the Builder
         */
        public Builder text( String text )
        {
            CheckParameters.areNotNull( text );
            return set( TEXT, text );
        }
        
        /**
         * Sets the timeRange parameter in the builder.
         * @param timeRange
         *        the value to store as the timeRange parameter.
         * @return the Builder
         */
        public Builder timeRange( int timeRange )
        {
            return set( TIME_RANGE, timeRange );
        }
        
        /**
         * Sets the audioOn parameter in the builder.
         * @param audioOn
         *        the value to store as the audioOn parameter.
         * @throws NullPointerException
         *         if audioOn is null.
         * @return the Builder
         */
        public Builder audioOn( OnOrOff audioOn )
        {
            CheckParameters.areNotNull( audioOn );
            return set( AUDIO, audioOn );
        }
        
        /**
         * Sets the fastForwardMultiplier parameter in the builder.
         * @param fastForwardMultiplier
         *        the value to store as the fastForwardMultiplier parameter.
         * @return the Builder
         */
        public Builder fastForwardMultiplier( int fastForwardMultiplier )
        {
            return set( FAST_FORWARD_MULTIPLIER, fastForwardMultiplier );
        }
        
        /**
         * Sets the duration parameter in the builder.
         * @param duration
         *        the value to store as the duration parameter.
         * @return the Builder
         */
        public Builder duration( int duration )
        {
            return set( DURATION, duration );
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
         * Sets the pktSize parameter in the builder.
         * @param pktSize
         *        the value to store as the pktSize parameter.
         * @return the Builder
         */
        public Builder pktSize( int pktSize )
        {
            return set( PKT_SIZE, pktSize );
        }
        
        /**
         * Sets the udpPort parameter in the builder.
         * @param udpPort
         *        the value to store as the udpPort parameter.
         * @return the Builder
         */
        public Builder udpPort( int udpPort )
        {
            return set( UDP_PORT, udpPort );
        }
        
        /**
         * Sets the refresh parameter in the builder.
         * @param refresh
         *        the value to store as the refresh parameter.
         * @return the Builder
         */
        public Builder refresh( int refresh )
        {
            return set( REFRESH, refresh );
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
         * @return the Builder
         */
        public Builder opChan( int opChan )
        {
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
         * Sets the proxyRetry parameter in the builder.
         * @param proxyRetry
         *        the value to store as the proxyRetry parameter.
         * @return the Builder
         */
        public Builder proxyRetry( int proxyRetry )
        {
            return set( PROXY_RETRY, proxyRetry );
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
        private  <T>Builder set( ParameterDescription<T, ?> parameter, T value )
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
        public  ReplayPicCGI build(  )
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
        params.add( CAM );
        params.add( FIELDS );
        params.add( SEQ );
        params.add( ID );
        params.add( CONTROL );
        params.add( TIME );
        params.add( LOCAL );
        params.add( RATE );
        params.add( TEXT );
        params.add( TIME_RANGE );
        params.add( AUDIO );
        params.add( FAST_FORWARD_MULTIPLIER );
        params.add( DURATION );
        params.add( RES );
        params.add( PKT_SIZE );
        params.add( UDP_PORT );
        params.add( REFRESH );
        params.add( FORMAT );
        params.add( TRANSMISSION_MODE );
        params.add( SLAVE_IP );
        params.add( OP_CHAN );
        params.add( PROXY_MODE );
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
        JFIF
        ,
        
        /**
         * Truncated JPEG image data.
         */
        JPEG
        ,
        
        /**
         * MPEG-4 image data.
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
     * Various video playback modes
     */
    public static enum Control
    {
        /**
         * Play video forwards at its original speed.
         */
        PLAY
        ,
        
        /**
         * Play video forwards at a speed controlled by the fast-forward multiplier.
         */
        FFWD
        ,
        
        /**
         * Play video backwards.
         */
        RWND
        ,
        
        /**
         * Stop playing video.
         */
        STOP
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of Control if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a Control
         */
        static Function<String, Option<Control>> fromStringFunction(  )
        {
            return
            new Function<String, Option<Control>>()
            {
                public Option<Control> apply(String s )
                {
                    for ( final Control element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Control element " );
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
         * Multipart MIME.
         */
        MIME
        ,
        
        /**
         * AD's 'binary' format.
         */
        BINARY
        ,
        
        /**
         * AD's 'minimal' format.
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
     * This is used in storing whether audio should be enabled
     */
    public static enum OnOrOff
    {
        /**
         * Signifies that audio is enabled.
         */
        ON
        ,
        
        /**
         * Signifies that audio is disabled.
         */
        OFF
        ;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of OnOrOff if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * @return a Function that parses a String into a OnOrOff
         */
        static Function<String, Option<OnOrOff>> fromStringFunction(  )
        {
            return
            new Function<String, Option<OnOrOff>>()
            {
                public Option<OnOrOff> apply(String s )
                {
                    for ( final OnOrOff element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid OnOrOff element " );
                }
            }
            ;
        }
        
    }
    public static Function<String, Option<Integer>> fromTimeFunction()
    {
        return
        new Function<String, Option<Integer>>()
        {
            public Option<Integer> apply(String s )
            {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yy");
                try
                {
                    return Option.getFullOption((int)(format.parse(s).getTime()/1000));
                }
                catch (java.text.ParseException e)
                {
                    try
                    {
                        return Option.getFullOption(Integer.parseInt(s));
                    }
                    catch (NumberFormatException e2)
                    {
                        return Option.getEmptyOption("Cannot parse "+s+" as a timestamp.");
                    }
                }
            }
        }
        ;
    }
    @Override
    public String toString()
    {
        return "/replay_pic.cgi?" + parameterMap.toURLParameters( params );
    }
    public static ReplayPicCGI fromString( String string )
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

