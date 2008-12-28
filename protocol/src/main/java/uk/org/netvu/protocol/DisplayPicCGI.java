package uk.org.netvu.protocol;

import static uk.org.netvu.protocol.ParameterDescription.*;
import static uk.org.netvu.protocol.StringConversion.*;


public final class DisplayPicCGI
{
        private final ParameterMap parameterMap;

        DisplayPicCGI(ParameterMap parameterMap)
        {
                this.parameterMap = parameterMap;
        }

  private static final ParameterDescription<Integer, Integer> CAM = parameterWithBounds( 1, 16, parameterWithDefault( "cam", 1, integer()));
public int getCam()
{
        return parameterMap.get(CAM);
}
  private static final ParameterDescription<Integer, Integer> FIELDS = positiveInteger(parameterWithDefault("fields", 1, integer()));
public int getFields()
{
        return parameterMap.get(FIELDS);
}
  private static final ParameterDescription<String, String> RES = oneOfWithDefault("res", "med", StringConversion.string(), "hi", "med", "lo");
public String getRes()
{
        return parameterMap.get(RES);
}
  private static final ParameterDescription<Integer, Integer> SEQ = parameterWithBounds(0, 0xF, parameterWithDefault("seq", 0, hexInt()));
public int getSeq()
{
        return parameterMap.get(SEQ);
}
  private static final ParameterDescription<Integer, Integer> DWELL = parameterWithDefault("dwell", 0, integer());
public int getDwell()
{
        return parameterMap.get(DWELL);
}
  private static final ParameterDescription<Integer, Integer> ID = parameterWithDefault("id", 0, integer());
public int getId()
{
        return parameterMap.get(ID);
}
  private static final ParameterDescription<Integer, Integer> DINDEX = parameterWithDefault("dindex", 0, integer());
public int getDIndex()
{
        return parameterMap.get(DINDEX);
}
  private static final ParameterDescription<Integer, Integer> PRESEL = parameterWithBounds(0, 3, parameterWithDefault("presel", 0, integer()));
public int getPresel()
{
        return parameterMap.get(PRESEL);
}
  private static final ParameterDescription<Integer, Integer> CHANNEL = parameterWithBounds(-1, 1, parameterWithDefault("channel", -1, integer()));
public int getChannel()
{
        return parameterMap.get(CHANNEL);
}
  private static final ParameterDescription<Integer, Integer> RATE = parameterWithDefault("rate", 0, integer());
public int getRate()
{
        return parameterMap.get(RATE);
}
  private static final ParameterDescription<Integer, Integer> FORCED_Q = parameterDisallowing( 1, parameterWithBounds(0, 255, parameterWithDefault("forcedq", 0, integer())));
public int getForcedQ()
{
        return parameterMap.get(FORCED_Q);
}
  private static final ParameterDescription<Integer, Integer> DURATION = nonNegativeParameter( parameterWithDefault("duration", 0, integer() ));
public int getDuration()
{
        return parameterMap.get(DURATION);
}
  private static final ParameterDescription<Integer, Integer> N_BUFFERS = nonNegativeParameter( parameterWithDefault("nbuffers", 0, integer() ));
public int getNBuffers()
{
        return parameterMap.get(N_BUFFERS);
}
  private static final ParameterDescription<Integer, Integer> TELEM_Q = nonNegativeParameter( parameterWithDefault("telemq", -1, integer()));
public int getTelemQ()
{
        return parameterMap.get(TELEM_Q);
}
  private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameterWithDefault("pkt_size", 0, integer()));
public int getPktSize()
{
        return parameterMap.get(PKT_SIZE);
}
  private static final ParameterDescription<Integer, Integer> UDP_PORT = parameterWithBounds(0, 65535, parameterWithDefault("udp_port", 0, integer() ) );
public int getUdpPort()
{
        return parameterMap.get(UDP_PORT);
}
  private static final ParameterDescription<String, String> AUDIO = oneOfWithDefault("audio", "0", string(), "on", "off", "0", "1", "2");
public String getAudio()
{
        return parameterMap.get(AUDIO);
}
  private static final ParameterDescription<Format, Format> FORMAT = parameterWithDefault("format", Format.JFIF, convenientPartial(Format.fromStringFunction()));
public Format getFormat()
{
        return parameterMap.get(FORMAT);
}
  private static final ParameterDescription<AudioMode, AudioMode> AUD_MODE = parameterWithDefault("aud_mode", AudioMode.UDP, convenientPartial(AudioMode.fromStringFunction()));
public AudioMode getAudMode()
{
        return parameterMap.get(AUD_MODE);
}
  private static final ParameterDescription<TransmissionMode, TransmissionMode> TX_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
public TransmissionMode getTxMode()
{
        return parameterMap.get(TX_MODE);
}
  private static final ParameterDescription<Integer, Integer> PPS = parameterWithDefault("pps", 0, integer());
public int getPPS()
{
        return parameterMap.get(PPS);
}
  private static final ParameterDescription<Integer, Integer> MP4_RATE = parameterWithDefault("mp4rate", 0, integer());
public int getMp4Rate()
{
        return parameterMap.get(MP4_RATE);
}
  private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameterWithDefault("slaveip", IPAddress.fromString("0.0.0.0").get(), convenientPartial(IPAddress.fromString));
public IPAddress getSlaveIP()
{
        return parameterMap.get(SLAVE_IP);
}
  private static final ParameterDescription<Integer, Integer> OP_CHAN = parameterWithDefault("opchan", -1, integer());
public int getOpChan()
{
        return parameterMap.get(OP_CHAN);
}
  private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameterWithDefault("proxymode", ProxyMode.TRANSIENT, convenientPartial(ProxyMode.fromStringFunction()));
public ProxyMode getProxyMode()
{
        return parameterMap.get(PROXY_MODE);
}
  private static final ParameterDescription<Integer, Integer> PROXY_PRI = parameterWithDefault("proxypri", 1, integer());
public int getProxyPri()
{
        return parameterMap.get(PROXY_PRI);
}
  private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameterWithDefault("proxyretry", 0, integer());
public int getProxyRetry()
{
        return parameterMap.get(PROXY_RETRY);
}


  private static enum Format
  {
    JFIF;

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

  private static enum AudioMode
  {
    UDP;

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

  private static enum TransmissionMode
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

  private static enum ProxyMode
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
}
