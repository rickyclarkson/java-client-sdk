import Pretty._

object replaypiccgi { def main(args: Array[String]): Unit = {  
  val params = List(
   intParaMeta("CAMERA", """ParameterDescription.parameter( "cam", StringConversion.integer() )
               .withDefault( 1 ).withBounds( 1, 16, Num.integer )""", "getCamera", "camera"),
   intParaMeta("FIELDS", """ParameterDescription.parameter( "fields", StringConversion.integer() )
               .withDefault( 1 ).notNegative( Num.integer )""", "getFields", "fields"),
   ParaMeta("Integer", "CAMERA_SEQUENCE_MASK", lines("""ParameterDescription.parameter( "seq", StringConversion.hexInt() )
                                    .withDefault( 0 ).withBounds( 0, 0xF, Num.integer )"""), "int", "getCameraSequenceMask", "cameraSequenceMask"),
   intParaMeta("CONNECTION_ID", """ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 )""", "getConnectionID", "connectionID"),
   ParaMeta("Control", "CONTROL", lines("""ParameterDescription.parameter( "control",
                                        StringConversion.convenientPartial( Control.fromStringFunction() ) )
                                        .withDefault( Control.STOP )"""), "Control", "getControl", "control"),
   intParaMeta("GMT_TIME", """ParameterDescription.parameter( "time", StringConversion.convenientPartial( fromTimeFunction() ) )
               .withDefault( 0 )""", "getGMTTime", "gmtTime"),
   intParaMeta("LOCAL_TIME", """ParameterDescription.parameter( "local", StringConversion.convenientPartial( fromTimeFunction() ) )
               .withDefault( 0 )""", "getLocalTime", "localTime"),
   intParaMeta("MAXIMUM_TRANSMIT_RATE", """ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 )""", "getMaximumTransmitRate", "maximumTransmitRate"),
   ParaMeta("String", "TEXT", lines("""ParameterDescription.parameter( "text", StringConversion.string() ).withDefault( "" )"""), "String", "getText", "text"),
   intParaMeta("TIME_RANGE", """ParameterDescription.parameter( "timerange", StringConversion.integer() ).withDefault( 0 )""", "getTimeRange", "timeRange"),
   ParaMeta("OnOrOff", "AUDIO", lines("""ParameterDescription.parameter( "audio",
                                      StringConversion.convenientPartial( OnOrOff.fromStringFunction() ) )
                                      .withDefault( OnOrOff.OFF )"""), "OnOrOff", "isAudioOn", "audioOn"),
   intParaMeta("FAST_FORWARD_MULTIPLIER", """ParameterDescription.parameter( "ffmult", StringConversion.integer() )
               .withDefault( 0 ).withBounds( 0, 256, Num.integer )""", "getFastForwardMultiplier", "fastForwardMultiplier"),
   intParaMeta("DURATION", """ParameterDescription.parameter( "duration", StringConversion.integer() )
               .withDefault( 0 ).notNegative( Num.integer )""", "getDuration", "duration"),
   ParaMeta("String", "RESOLUTION", lines("""ParameterDescription.parameter( "res", StringConversion.string() )
                                   .withDefault( "med" ).allowedValues( "hi", "med", "lo" )"""), "String", "getResolution", "resolution"),
   intParaMeta("PACKET_SIZE", """ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0,
                              ParameterDescription.parameter( "pkt_size", StringConversion.integer() ).withDefault( 0 ))""", "getPacketSize", "packetSize"),
   intParaMeta("UDP_PORT", """ParameterDescription.parameter( "udp_port", StringConversion.integer() )
                           .withDefault( 0 ).withBounds( 0, 65535, Num.integer )""", "getUdpPort", "udpPort"),
   intParaMeta("REFRESH", """ParameterDescription.parameter( "refresh", StringConversion.integer() )
                             .withDefault( 0 )""", "getRefresh", "refresh"),
   ParaMeta("Format", "FORMAT", lines("""ParameterDescription.parameter( "format",
                                      StringConversion.convenientPartial( Format.fromStringFunction() ) )
                                      .withDefault( Format.JFIF )"""), "Format", "getFormat", "format"),  
   ParaMeta("TransmissionMode", "TRANSMISSION_MODE", append(prepend("""ParameterDescription.parameterWithDefault( "txmode", """, lambda("ParameterMap", "TransmissionMode", "map", "map.get( FORMAT ) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL")), ", StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) )"), "TransmissionMode", "getTransmissionMode", "transmissionMode"),
   ParaMeta("IPAddress", "SLAVE_IP", lines("""ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial( IPAddress.fromString ) )
                                           .withDefault( IPAddress.fromString( "0.0.0.0" ).get() )"""), "IPAddress", "getSlaveIP", "slaveIP"),
   intParaMeta("OUTPUT_CHANNEL", """ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 )""", "getOutputChannel", "outputChannel"),
   ParaMeta("ProxyMode", "PROXY_MODE", lines("""ParameterDescription.parameter( "proxymode",
                                                StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault( ProxyMode.TRANSIENT )"""), "ProxyMode", "getProxyMode", "proxyMode"),
   intParaMeta("PROXY_RETRIES", """ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 )""", "getProxyRetries", "proxyRetries")
  )

  val packageName = "uk.org.netvu.protocol" 
  val className = "ReplayPicCGI"
  val urlPart = "/replay_pic.cgi?"

 val extras =
  Enum("Format", "The possible formats that the video stream can be returned as.", IsStatic(true),
       List(NameAndDescription("JFIF", "Complete JFIF (JPEG) image data"), NameAndDescription("JPEG", "Truncated JPEG image data"), NameAndDescription("MP4", "MPEG-4 image data"))).toJava ++
  Enum("Control", "Various video playback modes.", IsStatic(true),
       List(NameAndDescription("PLAY", "Play video forwards at its original speed"), NameAndDescription("FFWD", "Play video forwards at a speed controlled by the fast-forward multiplier"),
            NameAndDescription("RWND", "Play video backwards"),
            NameAndDescription("STOP", "Stop playing video"))).toJava ++
  Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", IsStatic(true),
       List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava ++
  Enum("OnOrOff", "This is used in storing whether audio should be enabled.", IsStatic(true),
       List(NameAndDescription("ON", "Signifies that audio is enabled"), NameAndDescription("OFF", "Signifies that audio is disabled"))).toJava ++ blankLine ++
 blockComment(lines("A Function that parses a timestamp either in HH:mm:ss:dd:MM:yy format or as a Julian time.") ++ blankLine ++ returnDoc("a Function that parses a timestamp.")) ++
 lines("static Function<String, Option<Integer>> fromTimeFunction()") ++ brace(append(prepend("return ", function("String", "Option<Integer>", "s", lines(
    """java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yy");
    try""") ++ brace(lines("return Option.getFullOption((int)(format.parse(s).getTime()/1000));")) ++ lines(
     "catch (java.text.ParseException e)") ++ brace(lines(
      "try") ++ brace(lines(
       "return Option.getFullOption(Integer.parseInt(s));")) ++ lines(
      "catch (NumberFormatException e2)") ++ brace(lines(
       """return Option.getEmptyOption("Cannot parse "+s+" as a timestamp.");"""))))), ";")) ++ blankLine

 CodeGen.generate(packageName, lines(
  """A parameter list for a replay_pic.cgi query.
  Use {@link ReplayPicCGI.Builder} to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromString(String)}."""), className, params, urlPart, extras)
} }
