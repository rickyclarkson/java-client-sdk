package uk.org.netvu.protocol;
   import java.util.*;
   import static uk.org.netvu.protocol.ParameterDescription.*;
   import static uk.org.netvu.protocol.StringConversion.*;
   import uk.org.netvu.util.CheckParameters;
   
   public final class DisplayPicCGI
{
    private final ParameterMap parameterMap;
                      DisplayPicCGI(ParameterMap parameterMap)
    {
        this.parameterMap = parameterMap;
    }
    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();
    private static final ParameterDescription<Integer, Integer> CAM = parameter("cam", integer()).withDefault(1).withBounds(1, 16, Num.integer);
    private static final ParameterDescription<Integer, Integer> FIELDS = parameter("fields", integer()).withDefault(1).positive(Num.integer);
    private static final ParameterDescription<String, String> RES = parameter("res", string()).withDefault("med").allowedValues("hi", "med", "lo");
    private static final ParameterDescription<Integer, Integer> SEQ = parameter("seq", hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer);
    private static final ParameterDescription<Integer, Integer> DWELL = parameter("dwell", integer()).withDefault(0);
    private static final ParameterDescription<Integer, Integer> ID = parameter("id", integer()).withDefault(0);
    private static final ParameterDescription<Integer, Integer> DINDEX = parameter("dindex", integer()).withDefault(0);
    private static final ParameterDescription<Integer, Integer> PRESEL = parameter("presel", integer()).withDefault(0).withBounds(0, 3, Num.integer);
    private static final ParameterDescription<Integer, Integer> CHANNEL = parameter("channel", integer()).withDefault(-1).withBounds(-1, 1, Num.integer);
    private static final ParameterDescription<Integer, Integer> RATE = parameter("rate", integer()).withDefault(0);
    private static final ParameterDescription<Integer, Integer> FORCED_Q = parameter("forcedq", integer()).withDefault(0).withBounds(0, 255, Num.integer).disallowing(1);
    private static final ParameterDescription<Integer, Integer> DURATION = parameter("duration", integer()).withDefault(0).notNegative(Num.integer);
    private static final ParameterDescription<Integer, Integer> N_BUFFERS = parameter("nbuffers", integer()).withDefault(0).notNegative(Num.integer);
    private static final ParameterDescription<Integer, Integer> TELEM_Q = parameter("telemQ", integer()).withDefault(-1).withBounds(-1, Integer.MAX_VALUE, Num.integer);
    private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameter("pkt_size", integer()).withDefault(0));
    private static final ParameterDescription<Integer, Integer> UDP_PORT = parameter("udp_port", integer()).withDefault(0).withBounds(0, 65535, Num.integer);
    private static final ParameterDescription<String, String> AUDIO = parameter("audio", string()).withDefault("0").allowedValues("on", "off", "0", "1", "2");
    private static final ParameterDescription<Format, Format> FORMAT = parameter("format", convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF);
    private static final ParameterDescription<AudioMode, AudioMode> AUDIO_MODE = parameter("aud_mode", convenientPartial(AudioMode.fromStringFunction())).withDefault(AudioMode.UDP);
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
    private static final ParameterDescription<Integer, Integer> PPS = parameter("pps", integer()).withDefault(0);
    private static final ParameterDescription<Integer, Integer> MP4_RATE = parameter("mp4rate", integer()).withDefault(0);
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameter("slaveip", convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get());
    private static final ParameterDescription<Integer, Integer> OP_CHAN = parameter("opchan", integer()).withDefault(-1);
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameter("proxymode", convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT);
    private static final ParameterDescription<Integer, Integer> PROXY_PRI = parameter("proxypri", integer()).withDefault(1);
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameter("proxyretry", integer()).withDefault(0);
    public int getCam()
    {
        return parameterMap.get(CAM);
    }
    public int getFields()
    {
        return parameterMap.get(FIELDS);
    }
    public String getRes()
    {
        return parameterMap.get(RES);
    }
    public int getSeq()
    {
        return parameterMap.get(SEQ);
    }
    public int getDwell()
    {
        return parameterMap.get(DWELL);
    }
    public int getId()
    {
        return parameterMap.get(ID);
    }
    public int getDIndex()
    {
        return parameterMap.get(DINDEX);
    }
    public int getPresel()
    {
        return parameterMap.get(PRESEL);
    }
    public int getChannel()
    {
        return parameterMap.get(CHANNEL);
    }
    public int getRate()
    {
        return parameterMap.get(RATE);
    }
    public int getForcedQ()
    {
        return parameterMap.get(FORCED_Q);
    }
    public int getDuration()
    {
        return parameterMap.get(DURATION);
    }
    public int getNBuffers()
    {
        return parameterMap.get(N_BUFFERS);
    }
    public int getTelemQ()
    {
        return parameterMap.get(TELEM_Q);
    }
    public int getPktSize()
    {
        return parameterMap.get(PKT_SIZE);
    }
    public int getUdpPort()
    {
        return parameterMap.get(UDP_PORT);
    }
    public String getAudio()
    {
        return parameterMap.get(AUDIO);
    }
    public Format getFormat()
    {
        return parameterMap.get(FORMAT);
    }
    public AudioMode getAudioMode()
    {
        return parameterMap.get(AUDIO_MODE);
    }
    public TransmissionMode getTransmissionMode()
    {
        return parameterMap.get(TRANSMISSION_MODE);
    }
    public int getPPS()
    {
        return parameterMap.get(PPS);
    }
    public int getMp4Rate()
    {
        return parameterMap.get(MP4_RATE);
    }
    public IPAddress getSlaveIP()
    {
        return parameterMap.get(SLAVE_IP);
    }
    public int getOpChan()
    {
        return parameterMap.get(OP_CHAN);
    }
    public ProxyMode getProxyMode()
    {
        return parameterMap.get(PROXY_MODE);
    }
    public int getProxyPri()
    {
        return parameterMap.get(PROXY_PRI);
    }
    public int getProxyRetry()
    {
        return parameterMap.get(PROXY_RETRY);
    }
    public static final class Builder
    {
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );
        public Builder cam(int cam)
        {
            return set(CAM, cam);
        }
        public Builder fields(int fields)
        {
            return set(FIELDS, fields);
        }
        public Builder res(String res)
        {
            return set(RES, res);
        }
        public Builder seq(int seq)
        {
            return set(SEQ, seq);
        }
        public Builder dwell(int dwell)
        {
            return set(DWELL, dwell);
        }
        public Builder id(int id)
        {
            return set(ID, id);
        }
        public Builder dIndex(int dIndex)
        {
            return set(DINDEX, dIndex);
        }
        public Builder presel(int presel)
        {
            return set(PRESEL, presel);
        }
        public Builder channel(int channel)
        {
            return set(CHANNEL, channel);
        }
        public Builder rate(int rate)
        {
            return set(RATE, rate);
        }
        public Builder forcedQ(int forcedQ)
        {
            return set(FORCED_Q, forcedQ);
        }
        public Builder duration(int duration)
        {
            return set(DURATION, duration);
        }
        public Builder nBuffers(int nBuffers)
        {
            return set(N_BUFFERS, nBuffers);
        }
        public Builder telemQ(int telemQ)
        {
            return set(TELEM_Q, telemQ);
        }
        public Builder pktSize(int pktSize)
        {
            return set(PKT_SIZE, pktSize);
        }
        public Builder udpPort(int udpPort)
        {
            return set(UDP_PORT, udpPort);
        }
        public Builder audio(String audio)
        {
            return set(AUDIO, audio);
        }
        public Builder format(Format format)
        {
            return set(FORMAT, format);
        }
        public Builder audioMode(AudioMode audioMode)
        {
            return set(AUDIO_MODE, audioMode);
        }
        public Builder transmissionMode(TransmissionMode transmissionMode)
        {
            return set(TRANSMISSION_MODE, transmissionMode);
        }
        public Builder pps(int pps)
        {
            return set(PPS, pps);
        }
        public Builder mp4Rate(int mp4Rate)
        {
            return set(MP4_RATE, mp4Rate);
        }
        public Builder slaveIP(IPAddress slaveIP)
        {
            return set(SLAVE_IP, slaveIP);
        }
        public Builder opChan(int opChan)
        {
            return set(OP_CHAN, opChan);
        }
        public Builder proxyMode(ProxyMode proxyMode)
        {
            return set(PROXY_MODE, proxyMode);
        }
        public Builder proxyPri(int proxyPri)
        {
            return set(PROXY_PRI, proxyPri);
        }
        public Builder proxyRetry(int proxyRetry)
        {
            return set(PROXY_RETRY, proxyRetry);
        }
        private <T> Builder set(final ParameterDescription<T, ?> parameter, final T value)
        {
            if (parameterMap.isEmpty())
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException(message);
            }
            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }
        public DisplayPicCGI build()
        {
            try
            {
                return new DisplayPicCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption("This Builder has already been built once.");
            }
        }
    }
    static
    {
        params.add(CAM);
        params.add(FIELDS);
        params.add(RES);
        params.add(SEQ);
        params.add(DWELL);
        params.add(ID);
        params.add(DINDEX);
        params.add(PRESEL);
        params.add(CHANNEL);
        params.add(RATE);
        params.add(FORCED_Q);
        params.add(DURATION);
        params.add(N_BUFFERS);
        params.add(TELEM_Q);
        params.add(PKT_SIZE);
        params.add(UDP_PORT);
        params.add(AUDIO);
        params.add(FORMAT);
        params.add(AUDIO_MODE);
        params.add(TRANSMISSION_MODE);
        params.add(PPS);
        params.add(MP4_RATE);
        params.add(SLAVE_IP);
        params.add(OP_CHAN);
        params.add(PROXY_MODE);
        params.add(PROXY_PRI);
        params.add(PROXY_RETRY);
    }
    public static enum Format
    {
        JFIF, JPEG, MP4;
        public static Function<String, Option<Format>> fromStringFunction()
        {
            return new Function<String, Option<Format>>()
            {
                public Option<Format> apply(String s)
                {
                    for (final Format element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid Format element ");
                }
            }
            ;
        }
    }
    public static enum AudioMode
    {
        UDP, INLINE;
        public static Function<String, Option<AudioMode>> fromStringFunction()
        {
            return new Function<String, Option<AudioMode>>()
            {
                public Option<AudioMode> apply(String s)
                {
                    for (final AudioMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid AudioMode element ");
                }
            }
            ;
        }
    }
    public static enum TransmissionMode
    {
        MIME, BINARY, MINIMAL;
        public static Function<String, Option<TransmissionMode>> fromStringFunction()
        {
            return new Function<String, Option<TransmissionMode>>()
            {
                public Option<TransmissionMode> apply(String s)
                {
                    for (final TransmissionMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid TransmissionMode element ");
                }
            }
            ;
        }
    }
    public static enum ProxyMode
    {
        TRANSIENT, PERSISTENT;
        public static Function<String, Option<ProxyMode>> fromStringFunction()
        {
            return new Function<String, Option<ProxyMode>>()
            {
                public Option<ProxyMode> apply(String s)
                {
                    for (final ProxyMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid ProxyMode element ");
                }
            }
            ;
        }
    }
    @Override
    public String toString()
    {
        return "/display_pic.cgi?" + parameterMap.toURLParameters(params);
    }
    public static DisplayPicCGI fromString(String string)
    {
        CheckParameters.areNotNull( string );
        if (string.length() == 0)
        {
            throw new IllegalArgumentException("Cannot parse an empty String into a DisplayPicCGI.");
        }
        final Option<ParameterMap> map = ParameterMap.fromURL(string, params);
        if (map.isEmpty())
        {
            throw new IllegalArgumentException(map.reason());
        }
        return new DisplayPicCGI(map.get());
    }
}

