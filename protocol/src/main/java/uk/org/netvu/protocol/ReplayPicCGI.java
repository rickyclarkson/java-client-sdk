package uk.org.netvu.protocol;
   import java.util.*;
   import static uk.org.netvu.protocol.ParameterDescription.*;
   import static uk.org.netvu.protocol.StringConversion.*;
   import uk.org.netvu.util.CheckParameters;
   
   public final class ReplayPicCGI
{
    private final ParameterMap parameterMap;
                      ReplayPicCGI(ParameterMap parameterMap)
    {
        this.parameterMap = parameterMap;
    }
    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();
    private static final ParameterDescription<Integer, Integer> CAM = parameter("cam", integer()).withDefault(1).withBounds(1, 16, Num.integer);
    private static final ParameterDescription<Integer, Integer> FIELDS = parameter("fields", integer()).withDefault(1).notNegative(Num.integer);
    private static final ParameterDescription<Integer, Integer> SEQ = parameter("seq", hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer);
    private static final ParameterDescription<Integer, Integer> ID = parameter("id", integer()).withDefault(0);
    private static final ParameterDescription<Control, Control> CONTROL = parameter("control", convenientPartial(Control.fromStringFunction())).withDefault(Control.STOP);
    private static final ParameterDescription<Integer, Integer> TIME = parameter("time", convenientPartial(fromTimeFunction())).withDefault(0);
    private static final ParameterDescription<Integer, Integer> LOCAL = parameter("local", convenientPartial(fromTimeFunction())).withDefault(0);
    private static final ParameterDescription<Integer, Integer> RATE = parameter("rate", integer()).withDefault(0);
    private static final ParameterDescription<String, String> TEXT = parameter("text", string()).withDefault("");
    private static final ParameterDescription<Integer, Integer> TIME_RANGE = parameter("timerange", integer()).withDefault(0);
    private static final ParameterDescription<OnOrOff, OnOrOff> AUDIO = parameter("audio", convenientPartial(OnOrOff.fromStringFunction())).withDefault(OnOrOff.OFF);
    private static final ParameterDescription<Integer, Integer> FAST_FORWARD_MULTIPLIER = parameter("ffmult", integer()).withDefault(0).withBounds(0, 256, Num.integer);
    private static final ParameterDescription<Integer, Integer> DURATION = parameter("duration", integer()).withDefault(0).notNegative(Num.integer);
    private static final ParameterDescription<String, String> RES = parameter("res", string()).withDefault("med").allowedValues("hi", "med", "lo");
    private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameter("pkt_size", integer()).withDefault(0));
    private static final ParameterDescription<Integer, Integer> UDP_PORT = parameter("udp_port", integer()).withDefault(0).withBounds(0, 65535, Num.integer);
    private static final ParameterDescription<Integer, Integer> REFRESH = parameter("refresh", integer()).withDefault(0);
    private static final ParameterDescription<Format, Format> FORMAT = parameter("format", convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF);
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameter("slaveip", convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get());
    private static final ParameterDescription<Integer, Integer> OP_CHAN = parameter("opchan", integer()).withDefault(-1);
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameter("proxymode", convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT);
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameter("proxyretry", integer()).withDefault(0);
    public int getCam()
    {
        return parameterMap.get(CAM);
    }
    public int getFields()
    {
        return parameterMap.get(FIELDS);
    }
    public int getSeq()
    {
        return parameterMap.get(SEQ);
    }
    public int getId()
    {
        return parameterMap.get(ID);
    }
    public Control getControl()
    {
        return parameterMap.get(CONTROL);
    }
    public int getTime()
    {
        return parameterMap.get(TIME);
    }
    public int getLocal()
    {
        return parameterMap.get(LOCAL);
    }
    public int getRate()
    {
        return parameterMap.get(RATE);
    }
    public String getText()
    {
        return parameterMap.get(TEXT);
    }
    public int getTimeRange()
    {
        return parameterMap.get(TIME_RANGE);
    }
    public OnOrOff isAudioOn()
    {
        return parameterMap.get(AUDIO);
    }
    public int getFastForwardMultiplier()
    {
        return parameterMap.get(FAST_FORWARD_MULTIPLIER);
    }
    public int getDuration()
    {
        return parameterMap.get(DURATION);
    }
    public String getRes()
    {
        return parameterMap.get(RES);
    }
    public int getPktSize()
    {
        return parameterMap.get(PKT_SIZE);
    }
    public int getUdpPort()
    {
        return parameterMap.get(UDP_PORT);
    }
    public int getRefresh()
    {
        return parameterMap.get(REFRESH);
    }
    public Format getFormat()
    {
        return parameterMap.get(FORMAT);
    }
    public TransmissionMode getTransmissionMode()
    {
        return parameterMap.get(TRANSMISSION_MODE);
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
        public Builder seq(int seq)
        {
            return set(SEQ, seq);
        }
        public Builder id(int id)
        {
            return set(ID, id);
        }
        public Builder control(Control control)
        {
            return set(CONTROL, control);
        }
        public Builder time(int time)
        {
            return set(TIME, time);
        }
        public Builder local(int local)
        {
            return set(LOCAL, local);
        }
        public Builder rate(int rate)
        {
            return set(RATE, rate);
        }
        public Builder text(String text)
        {
            return set(TEXT, text);
        }
        public Builder timeRange(int timeRange)
        {
            return set(TIME_RANGE, timeRange);
        }
        public Builder audioOn(OnOrOff audioOn)
        {
            return set(AUDIO, audioOn);
        }
        public Builder fastForwardMultiplier(int fastForwardMultiplier)
        {
            return set(FAST_FORWARD_MULTIPLIER, fastForwardMultiplier);
        }
        public Builder duration(int duration)
        {
            return set(DURATION, duration);
        }
        public Builder res(String res)
        {
            return set(RES, res);
        }
        public Builder pktSize(int pktSize)
        {
            return set(PKT_SIZE, pktSize);
        }
        public Builder udpPort(int udpPort)
        {
            return set(UDP_PORT, udpPort);
        }
        public Builder refresh(int refresh)
        {
            return set(REFRESH, refresh);
        }
        public Builder format(Format format)
        {
            return set(FORMAT, format);
        }
        public Builder transmissionMode(TransmissionMode transmissionMode)
        {
            return set(TRANSMISSION_MODE, transmissionMode);
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
        public ReplayPicCGI build()
        {
            try
            {
                return new ReplayPicCGI( parameterMap.get() );
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
        params.add(SEQ);
        params.add(ID);
        params.add(CONTROL);
        params.add(TIME);
        params.add(LOCAL);
        params.add(RATE);
        params.add(TEXT);
        params.add(TIME_RANGE);
        params.add(AUDIO);
        params.add(FAST_FORWARD_MULTIPLIER);
        params.add(DURATION);
        params.add(RES);
        params.add(PKT_SIZE);
        params.add(UDP_PORT);
        params.add(REFRESH);
        params.add(FORMAT);
        params.add(TRANSMISSION_MODE);
        params.add(SLAVE_IP);
        params.add(OP_CHAN);
        params.add(PROXY_MODE);
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
    public static enum Control
    {
        PLAY, FFWD, RWND, STOP;
        public static Function<String, Option<Control>> fromStringFunction()
        {
            return new Function<String, Option<Control>>()
            {
                public Option<Control> apply(String s)
                {
                    for (final Control element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid Control element ");
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
    public static enum OnOrOff
    {
        ON, OFF;
        public static Function<String, Option<OnOrOff>> fromStringFunction()
        {
            return new Function<String, Option<OnOrOff>>()
            {
                public Option<OnOrOff> apply(String s)
                {
                    for (final OnOrOff element: values())
                    {
                        if (element.toString().equals(s))
                        {
                            return Option.getFullOption(element);
                        }
                    }
                    return Option.getEmptyOption(s + " is not a valid OnOrOff element ");
                }
            }
            ;
        }
    }
    public static Function<String, Option<Integer>> fromTimeFunction()
    {
        return new Function<String, Option<Integer>>()
        {
            public Option<Integer> apply(String s)
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
        return "/replay_pic.cgi?" + parameterMap.toURLParameters(params);
    }
    public static ReplayPicCGI fromString(String string)
    {
        CheckParameters.areNotNull( string );
        if (string.length() == 0)
        {
            throw new IllegalArgumentException("Cannot parse an empty String into a ReplayPicCGI.");
        }
        final Option<ParameterMap> map = ParameterMap.fromURL(string, params);
        if (map.isEmpty())
        {
            throw new IllegalArgumentException(map.reason());
        }
        return new ReplayPicCGI(map.get());
    }
}

