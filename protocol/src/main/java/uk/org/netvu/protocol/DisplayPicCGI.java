package uk.org.netvu.protocol;

import static uk.org.netvu.protocol.ParameterDescription.*;
import static uk.org.netvu.protocol.StringConversion.*;


public final class DisplayPicCGI
{
  private static final ParameterDescription<Integer, Integer> CAM =
    ParameterDescription.parameterWithBounds( 1, 16, ParameterDescription.parameterWithDefault( "cam", 1, StringConversion.integer()));

  private static final ParameterDescription<Integer, Integer> FIELDS = 
    ParameterDescription.positiveInteger( parameterWithDefault( "fields", 1, integer() ) );

  private static final ParameterDescription<String, String> RES = ParameterDescription.oneOfWithDefault("res", "med", StringConversion.string(), "hi", "med", "lo");

  private static final ParameterDescription<Integer, Integer> SEQ = parameterWithBounds(0, 0xF, parameterWithDefault("seq", 0, hexInt() ) );

  private static final ParameterDescription<Integer, Integer> DWELL = parameterWithDefault("dwell", 0, integer());

  private static final ParameterDescription<Integer, Integer> ID = parameterWithDefault("id", 0, integer());
  private static final ParameterDescription<Integer, Integer> DINDEX = parameterWithDefault("dindex", 0, integer());
  private static final ParameterDescription<Integer, Integer> PRESEL = parameterWithBounds(0, 3, parameterWithDefault("presel", 0, integer()));
  private static final ParameterDescription<Integer, Integer> CHANNEL = parameterWithBounds(-1, 1, parameterWithDefault("channel", -1, integer()));
  private static final ParameterDescription<Integer, Integer> RATE = parameterWithDefault("rate", 0, integer());
  private static final ParameterDescription<Integer, Integer> FORCED_Q = parameterDisallowing( 1, parameterWithBounds(0, 255, parameterWithDefault("forcedq", 0, integer())));
  private static final ParameterDescription<Integer, Integer> DURATION = nonNegativeParameter( parameterWithDefault("duration", 0, integer() ));
  private static final ParameterDescription<Integer, Integer> N_BUFFERS = nonNegativeParameter( parameterWithDefault("nbuffers", 0, integer() ));
  private static final ParameterDescription<Integer, Integer> TELEM_Q = nonNegativeParameter( parameterWithDefault("telemq", -1, integer()));
  private static final ParameterDescription<Integer, Integer> PKT_SIZE = parameterWithBoundsAndAnException(100, 1500, 0, parameterWithDefault("pkt_size", 0, integer()));
  private static final ParameterDescription<Integer, Integer> UDP_PORT = parameterWithBounds(0, 65535, parameterWithDefault("udp_port", 0, integer() ) );
  private static final ParameterDescription<String, String> AUDIO = oneOfWithDefault("audio", "0", string(), "on", "off", "0", "1", "2");
  private static final ParameterDescription<Format, Format> FORMAT = parameterWithDefault("format", Format.JFIF, convenientPartial(Format.fromStringFunction()));
  private static final ParameterDescription<AudioMode, AudioMode> AUD_MODE = parameterWithDefault("aud_mode", AudioMode.UDP, convenientPartial(AudioMode.fromStringFunction()));
  private static final ParameterDescription<TransmissionMode, TransmissionMode> TX_MODE = parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()));
  private static final ParameterDescription<Integer, Integer> PPS = parameterWithDefault("pps", 0, integer());
  private static final ParameterDescription<Integer, Integer> MP4_RATE = parameterWithDefault("mp4rate", 0, integer());
  private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP = parameterWithDefault("slaveip", IPAddress.fromString("0.0.0.0").get(), convenientPartial(IPAddress.fromString));
  private static final ParameterDescription<Integer, Integer> OP_CHAN = parameterWithDefault("opchan", -1, integer());
  private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE = parameterWithDefault("proxymode", ProxyMode.TRANSIENT, convenientPartial(ProxyMode.fromStringFunction()));
  private static final ParameterDescription<Integer, Integer> PROXY_PRI = parameterWithDefault("proxypri", 1, integer());
  private static final ParameterDescription<Integer, Integer> PROXY_RETRY = parameterWithDefault("proxyretry", 0, integer());


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
