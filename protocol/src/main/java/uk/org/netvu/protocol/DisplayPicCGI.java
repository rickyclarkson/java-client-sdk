package uk.org.netvu.protocol;

import java.util.*;
import static uk.org.netvu.protocol.ParameterDescription.*;
import static uk.org.netvu.protocol.StringConversion.*;



public final class DisplayPicCGI
{
        private final ParameterMap parameterMap;

        DisplayPicCGI(ParameterMap parameterMap)
        {
                this.parameterMap = parameterMap;
        }

  private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();

  private static final ParameterDescription<Integer, Integer> CAM = parameter( "cam", integer()).withDefault(1).withBounds(1, 16, Num.integer);
public int getCam()
{
        return parameterMap.get(CAM);
}
static
{
    params.add(CAM);
}
  private static final ParameterDescription<Integer, Integer> FIELDS = parameter("fields", integer()).withDefault(1).positive(Num.integer);
public int getFields()
{
        return parameterMap.get(FIELDS);
}
static
{
    params.add(FIELDS);
}
  private static final ParameterDescription<String, String> RES = parameter("res", string()).withDefault("med").allowedValues("hi", "med", "lo");
public String getRes()
{
        return parameterMap.get(RES);
}
static
{
    params.add(RES);
}
  private static final ParameterDescription<Integer, Integer> SEQ = parameter("seq", hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer);
public int getSeq()
{
        return parameterMap.get(SEQ);
}
static
{
    params.add(SEQ);
}
  private static final ParameterDescription<Integer, Integer> DWELL = parameter("dwell", integer()).withDefault(0);
public int getDwell()
{
        return parameterMap.get(DWELL);
}
static
{
    params.add(DWELL);
}
  private static final ParameterDescription<Integer, Integer> ID = parameter("id", integer()).withDefault(0);
public int getId()
{
        return parameterMap.get(ID);
}
static
{
    params.add(ID);
}
  private static final ParameterDescription<Integer, Integer> DINDEX = parameter("dindex", integer()).withDefault(0);
public int getDIndex()
{
        return parameterMap.get(DINDEX);
}
static
{
    params.add(DINDEX);
}
  private static final ParameterDescription<Integer, Integer> PRESEL = parameter("presel", integer()).withDefault(0).withBounds(0, 3, Num.integer);
public int getPresel()
{
        return parameterMap.get(PRESEL);
}
static
{
    params.add(PRESEL);
}
  private static final ParameterDescription<Integer, Integer> CHANNEL = parameter("channel", integer()).withDefault(-1).withBounds(-1, 1, Num.integer);
public int getChannel()
{
        return parameterMap.get(CHANNEL);
}
static
{
    params.add(CHANNEL);
}
  private static final ParameterDescription<Integer, Integer> RATE = parameter("rate", integer()).withDefault(0);
public int getRate()
{
        return parameterMap.get(RATE);
}
static
{
    params.add(RATE);
}
  private static final ParameterDescription<Integer, Integer> FORCED_Q = parameter("forcedq", integer()).withDefault(0).withBounds(0, 255, Num.integer).disallowing(1);
public int getForcedQ()
{
        return parameterMap.get(FORCED_Q);
}
static
{
    params.add(FORCED_Q);
}
  private static final ParameterDescription<Integer, Integer> DURATION = nonNegativeParameter( parameter("duration", integer()).withDefault(0 ));
public int getDuration()
{
        return parameterMap.get(DURATION);
}
static
{
    params.add(DURATION);
}
  private static final ParameterDescription<Integer, Integer> N_BUFFERS = nonNegativeParameter( parameter("nbuffers", integer()).withDefault( 0));
public int getNBuffers()
{
        return parameterMap.get(N_BUFFERS);
}
static
{
    params.add(N_BUFFERS);
}
  private static final ParameterDescription<Integer, Integer> TELEM_Q = nonNegativeParameter( parameter("telemq", integer()).withDefault(-1));
public int getTelemQ()
{
        return parameterMap.get(TELEM_Q);
}
static
{
    params.add(TELEM_Q);
}
  private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameterWithDefault("pkt_size", 0, integer()));
public int getPktSize()
{
        return parameterMap.get(PKT_SIZE);
}
static
{
    params.add(PKT_SIZE);
}
  private static final ParameterDescription<Integer, Integer> UDP_PORT = parameterWithBounds(0, 65535, parameterWithDefault("udp_port", 0, integer() ) );
public int getUdpPort()
{
        return parameterMap.get(UDP_PORT);
}
static
{
    params.add(UDP_PORT);
}
  private static final ParameterDescription<String, String> AUDIO = parameter("audio", string()).withDefault("0").allowedValues("on", "off", "0", "1", "2");
public String getAudio()
{
        return parameterMap.get(AUDIO);
}
static
{
    params.add(AUDIO);
}
  private static final ParameterDescription<Format, Format> FORMAT = parameterWithDefault("format", Format.JFIF, convenientPartial(Format.fromStringFunction()));
public Format getFormat()
{
        return parameterMap.get(FORMAT);
}
static
{
    params.add(FORMAT);
}
  private static final ParameterDescription<AudioMode, AudioMode> AUD_MODE = parameterWithDefault("aud_mode", AudioMode.UDP, convenientPartial(AudioMode.fromStringFunction()));
public AudioMode getAudMode()
{
        return parameterMap.get(AUD_MODE);
}
static
{
    params.add(AUD_MODE);
}
  private static final ParameterDescription<TransmissionMode, TransmissionMode> TX_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
public TransmissionMode getTxMode()
{
        return parameterMap.get(TX_MODE);
}
static
{
    params.add(TX_MODE);
}
  private static final ParameterDescription<Integer, Integer> PPS = parameterWithDefault("pps", 0, integer());
public int getPPS()
{
        return parameterMap.get(PPS);
}
static
{
    params.add(PPS);
}
  private static final ParameterDescription<Integer, Integer> MP4_RATE = parameterWithDefault("mp4rate", 0, integer());
public int getMp4Rate()
{
        return parameterMap.get(MP4_RATE);
}
static
{
    params.add(MP4_RATE);
}
  private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameterWithDefault("slaveip", IPAddress.fromString("0.0.0.0").get(), convenientPartial(IPAddress.fromString));
public IPAddress getSlaveIP()
{
        return parameterMap.get(SLAVE_IP);
}
static
{
    params.add(SLAVE_IP);
}
  private static final ParameterDescription<Integer, Integer> OP_CHAN = parameterWithDefault("opchan", -1, integer());
public int getOpChan()
{
        return parameterMap.get(OP_CHAN);
}
static
{
    params.add(OP_CHAN);
}
  private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameterWithDefault("proxymode", ProxyMode.TRANSIENT, convenientPartial(ProxyMode.fromStringFunction()));
public ProxyMode getProxyMode()
{
        return parameterMap.get(PROXY_MODE);
}
static
{
    params.add(PROXY_MODE);
}
  private static final ParameterDescription<Integer, Integer> PROXY_PRI = parameterWithDefault("proxypri", 1, integer());
public int getProxyPri()
{
        return parameterMap.get(PROXY_PRI);
}
static
{
    params.add(PROXY_PRI);
}
  private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameterWithDefault("proxyretry", 0, integer());
public int getProxyRetry()
{
        return parameterMap.get(PROXY_RETRY);
}
static
{
    params.add(PROXY_RETRY);
}

  public static enum Format
  {
    JFIF, JPEG, MP4;

    public static Function<String, Option<Format>> fromStringFunction()
{
  return new Function<String, Option<Format>>() { public Option<Format> apply(String s) { for (final Format element: values())
{
  if (element.toString().equals(s))
    return Option.getFullOption(element);
}
return Option.getEmptyOption(s + " is not a valid Format element"); } };
}
  }

  public static enum AudioMode
  {
    UDP, INLINE;

    public static Function<String, Option<AudioMode>> fromStringFunction()
{
  return new Function<String, Option<AudioMode>>() { public Option<AudioMode> apply(String s) { for (final AudioMode element: values())
{
  if (element.toString().equals(s))
    return Option.getFullOption(element);
}
return Option.getEmptyOption(s + " is not a valid AudioMode element"); } };
}
  }

  public static enum TransmissionMode
  {
    MIME,
    BINARY,
    MINIMAL;

    public static Function<String, Option<TransmissionMode>> fromStringFunction()
{
  return new Function<String, Option<TransmissionMode>>() { public Option<TransmissionMode> apply(String s) { for (final TransmissionMode element: values())
{
  if (element.toString().equals(s))
    return Option.getFullOption(element);
}
return Option.getEmptyOption(s + " is not a valid TransmissionMode element"); } };
}
  }

  public static enum ProxyMode
  {
    TRANSIENT,
    PERSISTENT;

    public static Function<String, Option<ProxyMode>> fromStringFunction()
{
  return new Function<String, Option<ProxyMode>>() { public Option<ProxyMode> apply(String s) { for (final ProxyMode element: values())
{
  if (element.toString().equals(s))
    return Option.getFullOption(element);
}
return Option.getEmptyOption(s + " is not a valid ProxyMode element"); } };
}
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
  return set(AUD_MODE, audioMode);
}
    public Builder txMode(TransmissionMode txMode)
{
  return set(TX_MODE, txMode);
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

    parameterMap = Option.getFullOption( parameterMap.get().set(parameter, value));
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

  @Override
  public String toString()
  {
    return "/display_pic.cgi?" + parameterMap.toURLParameters(params);
  }
}
