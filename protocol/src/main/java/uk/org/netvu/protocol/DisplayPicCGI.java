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
        /**
        * Sets the cam parameter in the builder.
        @param cam the value to store as the cam parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the cam parameter has already been set.
        @throws NullPointerException if cam is null.
        */
        public Builder cam(int cam)
        {
            CheckParameters.areNotNull(cam);
            return set(CAM, cam);
        }
        /**
        * Sets the fields parameter in the builder.
        @param fields the value to store as the fields parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the fields parameter has already been set.
        @throws NullPointerException if fields is null.
        */
        public Builder fields(int fields)
        {
            CheckParameters.areNotNull(fields);
            return set(FIELDS, fields);
        }
        /**
        * Sets the res parameter in the builder.
        @param res the value to store as the res parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the res parameter has already been set.
        @throws NullPointerException if res is null.
        */
        public Builder res(String res)
        {
            CheckParameters.areNotNull(res);
            return set(RES, res);
        }
        /**
        * Sets the seq parameter in the builder.
        @param seq the value to store as the seq parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the seq parameter has already been set.
        @throws NullPointerException if seq is null.
        */
        public Builder seq(int seq)
        {
            CheckParameters.areNotNull(seq);
            return set(SEQ, seq);
        }
        /**
        * Sets the dwell parameter in the builder.
        @param dwell the value to store as the dwell parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the dwell parameter has already been set.
        @throws NullPointerException if dwell is null.
        */
        public Builder dwell(int dwell)
        {
            CheckParameters.areNotNull(dwell);
            return set(DWELL, dwell);
        }
        /**
        * Sets the id parameter in the builder.
        @param id the value to store as the id parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the id parameter has already been set.
        @throws NullPointerException if id is null.
        */
        public Builder id(int id)
        {
            CheckParameters.areNotNull(id);
            return set(ID, id);
        }
        /**
        * Sets the dIndex parameter in the builder.
        @param dIndex the value to store as the dIndex parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the dIndex parameter has already been set.
        @throws NullPointerException if dIndex is null.
        */
        public Builder dIndex(int dIndex)
        {
            CheckParameters.areNotNull(dIndex);
            return set(DINDEX, dIndex);
        }
        /**
        * Sets the presel parameter in the builder.
        @param presel the value to store as the presel parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the presel parameter has already been set.
        @throws NullPointerException if presel is null.
        */
        public Builder presel(int presel)
        {
            CheckParameters.areNotNull(presel);
            return set(PRESEL, presel);
        }
        /**
        * Sets the channel parameter in the builder.
        @param channel the value to store as the channel parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the channel parameter has already been set.
        @throws NullPointerException if channel is null.
        */
        public Builder channel(int channel)
        {
            CheckParameters.areNotNull(channel);
            return set(CHANNEL, channel);
        }
        /**
        * Sets the rate parameter in the builder.
        @param rate the value to store as the rate parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the rate parameter has already been set.
        @throws NullPointerException if rate is null.
        */
        public Builder rate(int rate)
        {
            CheckParameters.areNotNull(rate);
            return set(RATE, rate);
        }
        /**
        * Sets the forcedQ parameter in the builder.
        @param forcedQ the value to store as the forcedQ parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the forcedQ parameter has already been set.
        @throws NullPointerException if forcedQ is null.
        */
        public Builder forcedQ(int forcedQ)
        {
            CheckParameters.areNotNull(forcedQ);
            return set(FORCED_Q, forcedQ);
        }
        /**
        * Sets the duration parameter in the builder.
        @param duration the value to store as the duration parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the duration parameter has already been set.
        @throws NullPointerException if duration is null.
        */
        public Builder duration(int duration)
        {
            CheckParameters.areNotNull(duration);
            return set(DURATION, duration);
        }
        /**
        * Sets the nBuffers parameter in the builder.
        @param nBuffers the value to store as the nBuffers parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the nBuffers parameter has already been set.
        @throws NullPointerException if nBuffers is null.
        */
        public Builder nBuffers(int nBuffers)
        {
            CheckParameters.areNotNull(nBuffers);
            return set(N_BUFFERS, nBuffers);
        }
        /**
        * Sets the telemQ parameter in the builder.
        @param telemQ the value to store as the telemQ parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the telemQ parameter has already been set.
        @throws NullPointerException if telemQ is null.
        */
        public Builder telemQ(int telemQ)
        {
            CheckParameters.areNotNull(telemQ);
            return set(TELEM_Q, telemQ);
        }
        /**
        * Sets the pktSize parameter in the builder.
        @param pktSize the value to store as the pktSize parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the pktSize parameter has already been set.
        @throws NullPointerException if pktSize is null.
        */
        public Builder pktSize(int pktSize)
        {
            CheckParameters.areNotNull(pktSize);
            return set(PKT_SIZE, pktSize);
        }
        /**
        * Sets the udpPort parameter in the builder.
        @param udpPort the value to store as the udpPort parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the udpPort parameter has already been set.
        @throws NullPointerException if udpPort is null.
        */
        public Builder udpPort(int udpPort)
        {
            CheckParameters.areNotNull(udpPort);
            return set(UDP_PORT, udpPort);
        }
        /**
        * Sets the audio parameter in the builder.
        @param audio the value to store as the audio parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the audio parameter has already been set.
        @throws NullPointerException if audio is null.
        */
        public Builder audio(String audio)
        {
            CheckParameters.areNotNull(audio);
            return set(AUDIO, audio);
        }
        /**
        * Sets the format parameter in the builder.
        @param format the value to store as the format parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the format parameter has already been set.
        @throws NullPointerException if format is null.
        */
        public Builder format(Format format)
        {
            CheckParameters.areNotNull(format);
            return set(FORMAT, format);
        }
        /**
        * Sets the audioMode parameter in the builder.
        @param audioMode the value to store as the audioMode parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the audioMode parameter has already been set.
        @throws NullPointerException if audioMode is null.
        */
        public Builder audioMode(AudioMode audioMode)
        {
            CheckParameters.areNotNull(audioMode);
            return set(AUDIO_MODE, audioMode);
        }
        /**
        * Sets the transmissionMode parameter in the builder.
        @param transmissionMode the value to store as the transmissionMode parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the transmissionMode parameter has already been set.
        @throws NullPointerException if transmissionMode is null.
        */
        public Builder transmissionMode(TransmissionMode transmissionMode)
        {
            CheckParameters.areNotNull(transmissionMode);
            return set(TRANSMISSION_MODE, transmissionMode);
        }
        /**
        * Sets the pps parameter in the builder.
        @param pps the value to store as the pps parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the pps parameter has already been set.
        @throws NullPointerException if pps is null.
        */
        public Builder pps(int pps)
        {
            CheckParameters.areNotNull(pps);
            return set(PPS, pps);
        }
        /**
        * Sets the mp4Rate parameter in the builder.
        @param mp4Rate the value to store as the mp4Rate parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the mp4Rate parameter has already been set.
        @throws NullPointerException if mp4Rate is null.
        */
        public Builder mp4Rate(int mp4Rate)
        {
            CheckParameters.areNotNull(mp4Rate);
            return set(MP4_RATE, mp4Rate);
        }
        /**
        * Sets the slaveIP parameter in the builder.
        @param slaveIP the value to store as the slaveIP parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the slaveIP parameter has already been set.
        @throws NullPointerException if slaveIP is null.
        */
        public Builder slaveIP(IPAddress slaveIP)
        {
            CheckParameters.areNotNull(slaveIP);
            return set(SLAVE_IP, slaveIP);
        }
        /**
        * Sets the opChan parameter in the builder.
        @param opChan the value to store as the opChan parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the opChan parameter has already been set.
        @throws NullPointerException if opChan is null.
        */
        public Builder opChan(int opChan)
        {
            CheckParameters.areNotNull(opChan);
            return set(OP_CHAN, opChan);
        }
        /**
        * Sets the proxyMode parameter in the builder.
        @param proxyMode the value to store as the proxyMode parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the proxyMode parameter has already been set.
        @throws NullPointerException if proxyMode is null.
        */
        public Builder proxyMode(ProxyMode proxyMode)
        {
            CheckParameters.areNotNull(proxyMode);
            return set(PROXY_MODE, proxyMode);
        }
        /**
        * Sets the proxyPri parameter in the builder.
        @param proxyPri the value to store as the proxyPri parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the proxyPri parameter has already been set.
        @throws NullPointerException if proxyPri is null.
        */
        public Builder proxyPri(int proxyPri)
        {
            CheckParameters.areNotNull(proxyPri);
            return set(PROXY_PRI, proxyPri);
        }
        /**
        * Sets the proxyRetry parameter in the builder.
        @param proxyRetry the value to store as the proxyRetry parameter.
        @return the Builder.
        @throws IllegalStateException if the value is invalid or the proxyRetry parameter has already been set.
        @throws NullPointerException if proxyRetry is null.
        */
        public Builder proxyRetry(int proxyRetry)
        {
            CheckParameters.areNotNull(proxyRetry);
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
            return
            new Function<String, Option<Format>>()
            {
                public Option<Format> apply(String s )
                {
                    for (final Format element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid Format element " );
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
            return
            new Function<String, Option<AudioMode>>()
            {
                public Option<AudioMode> apply(String s )
                {
                    for (final AudioMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid AudioMode element " );
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
            return
            new Function<String, Option<TransmissionMode>>()
            {
                public Option<TransmissionMode> apply(String s )
                {
                    for (final TransmissionMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid TransmissionMode element " );
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
            return
            new Function<String, Option<ProxyMode>>()
            {
                public Option<ProxyMode> apply(String s )
                {
                    for (final ProxyMode element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid ProxyMode element " );
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

