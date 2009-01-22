import Pretty._

object replaypiccgi { def main(args: Array[String]): Unit = {  
  val params = List(
   intParaMeta("CAM", """ParameterDescription.parameter("cam", StringConversion.integer()).withDefault(1).withBounds(1, 16, Num.integer)""", "getCam", "cam"),
   intParaMeta("FIELDS", """ParameterDescription.parameter("fields", StringConversion.integer()).withDefault(1).notNegative(Num.integer)""", "getFields", "fields"),
   ParaMeta("Integer", "SEQ", """ParameterDescription.parameter("seq", StringConversion.hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer)""", "int", "getSeq", "seq"),
   intParaMeta("ID", """ParameterDescription.parameter("id", StringConversion.integer()).withDefault(0)""", "getId", "id"),
   ParaMeta("Control", "CONTROL", """ParameterDescription.parameter("control", StringConversion.convenientPartial(Control.fromStringFunction())).withDefault(Control.STOP)""", "Control", "getControl", "control"),
   intParaMeta("TIME", """ParameterDescription.parameter("time", StringConversion.convenientPartial(fromTimeFunction())).withDefault(0)""", "getTime", "time"),
   intParaMeta("LOCAL", """ParameterDescription.parameter("local", StringConversion.convenientPartial(fromTimeFunction())).withDefault(0)""", "getLocal", "local"),
   intParaMeta("RATE", """ParameterDescription.parameter("rate", StringConversion.integer()).withDefault(0)""", "getRate", "rate"),
   ParaMeta("String", "TEXT", """ParameterDescription.parameter("text", StringConversion.string()).withDefault("")""", "String", "getText", "text"),
   intParaMeta("TIME_RANGE", """ParameterDescription.parameter("timerange", StringConversion.integer()).withDefault(0)""", "getTimeRange", "timeRange"),
   ParaMeta("OnOrOff", "AUDIO", """ParameterDescription.parameter("audio", StringConversion.convenientPartial(OnOrOff.fromStringFunction())).withDefault(OnOrOff.OFF)""", "OnOrOff", "isAudioOn", "audioOn"),
   intParaMeta("FAST_FORWARD_MULTIPLIER", """ParameterDescription.parameter("ffmult", StringConversion.integer()).withDefault(0).withBounds(0, 256, Num.integer)""", "getFastForwardMultiplier", "fastForwardMultiplier"),
   intParaMeta("DURATION", """ParameterDescription.parameter("duration", StringConversion.integer()).withDefault(0).notNegative(Num.integer)""", "getDuration", "duration"),
   ParaMeta("String", "RES", """ParameterDescription.parameter("res", StringConversion.string()).withDefault("med").allowedValues("hi", "med", "lo")""", "String", "getRes", "res"),
   intParaMeta("PKT_SIZE", """ParameterDescription.parameterWithBoundsAndAnException(100, 1500, 0, ParameterDescription.parameter("pkt_size", StringConversion.integer()).withDefault(0))""", "getPktSize", "pktSize"),
   intParaMeta("UDP_PORT", """ParameterDescription.parameter("udp_port", StringConversion.integer()).withDefault(0).withBounds(0, 65535, Num.integer)""", "getUdpPort", "udpPort"),
   intParaMeta("REFRESH", """ParameterDescription.parameter("refresh", StringConversion.integer()).withDefault(0)""", "getRefresh", "refresh"),
   ParaMeta("Format", "FORMAT", """ParameterDescription.parameter("format", StringConversion.convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF)""", "Format", "getFormat", "format"),  
   ParaMeta("TransmissionMode", "TRANSMISSION_MODE", """ParameterDescription.parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, StringConversion.convenientPartial(TransmissionMode.fromStringFunction()))""", "TransmissionMode", "getTransmissionMode", "transmissionMode"),
   ParaMeta("IPAddress", "SLAVE_IP", """ParameterDescription.parameter("slaveip", StringConversion.convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get())""", "IPAddress", "getSlaveIP", "slaveIP"),
   intParaMeta("OP_CHAN", """ParameterDescription.parameter("opchan", StringConversion.integer()).withDefault(-1)""", "getOpChan", "opChan"),
   ParaMeta("ProxyMode", "PROXY_MODE", """ParameterDescription.parameter("proxymode", StringConversion.convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT)""", "ProxyMode", "getProxyMode", "proxyMode"),
   intParaMeta("PROXY_RETRY", """ParameterDescription.parameter("proxyretry", StringConversion.integer()).withDefault(0)""", "getProxyRetry", "proxyRetry")
  )

  val packageName = "uk.org.netvu.protocol" 
  val className = "ReplayPicCGI"
  val urlPart = "/replay_pic.cgi?"

 val extras =
  Enum("Format", "The possible formats that the video stream can be returned as.", IsStatic(true),
       List(NameAndDescription("JFIF", "Complete JFIF (JPEG) image data"), NameAndDescription("JPEG", "Truncated JPEG image data"), NameAndDescription("MP4", "MPEG-4 image data"))).toJava ++
  Enum("Control", "Various video playback modes", IsStatic(true),
       List(NameAndDescription("PLAY", "Play video forwards at its original speed"), NameAndDescription("FFWD", "Play video forwards at a speed controlled by the fast-forward multiplier"),
            NameAndDescription("RWND", "Play video backwards"),
            NameAndDescription("STOP", "Stop playing video"))).toJava ++
  Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", IsStatic(true),
       List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava ++
  Enum("OnOrOff", "This is used in storing whether audio should be enabled", IsStatic(true),
       List(NameAndDescription("ON", "Signifies that audio is enabled"), NameAndDescription("OFF", "Signifies that audio is disabled"))).toJava ++
 lines("public static Function<String, Option<Integer>> fromTimeFunction()") ++ brace(lines(
   "return") ++ function("String", "Option<Integer>", "s", lines(
    """java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yy");
    try""") ++ brace(lines("return Option.getFullOption((int)(format.parse(s).getTime()/1000));")) ++ lines(
     "catch (java.text.ParseException e)") ++ brace(lines(
      "try") ++ brace(lines(
       "return Option.getFullOption(Integer.parseInt(s));")) ++ lines(
      "catch (NumberFormatException e2)") ++ brace(lines(
       """return Option.getEmptyOption("Cannot parse "+s+" as a timestamp.");""")))) ++ lines(";"))

 CodeGen.generate(packageName, lines(
  """A parameter list for a replay_pic.cgi query.
  Use {@link ReplayPicCGI.Builder} to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromURL(String)}."""), className, params, urlPart, extras)
} }
