import Pretty._

object commonparameters {
 val commonParams = List(
  intParaMeta('CAMERA, """ParameterDescription.parameter( "cam", StringConversion.integer() )
              .withDefault( 1 ).positive( Num.integer )""", 'getCamera, 'camera),
  intParaMeta('FIELD_COUNT, """ParameterDescription.parameter( "fields", StringConversion.integer() )
              .withDefault( 1 ).positive( Num.integer )""", 'getFieldCount, 'fieldCount),
  ParaMeta('String, 'RESOLUTION, lines("""ParameterDescription.parameter( "res", StringConversion.string() )
                                         .withDefault( "med" ).allowedValues( "hi", "med", "lo" )"""), 'String, 'getResolution, 'resolution),
  ParaMeta('String, 'CAMERA_SEQUENCE_MASK, lines("""ParameterDescription.parameter( "seq", StringConversion.onlyHexDigits() ).withDefault( "0" )"""), 'String, 'getCameraSequenceMask, 'cameraSequenceMask),
  intParaMeta('CONNECTION_ID, """ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 )""", 'getConnectionID, 'connectionID),
  intParaMeta('MAXIMUM_TRANSMIT_RATE, """ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 )""", 'getMaximumTransmitRate, 'maximumTransmitRate),
  intParaMeta('DURATION, """ParameterDescription.parameter( "duration", StringConversion.integer() )
                          .withDefault( 0 ).notNegative( Num.integer )""", 'getDuration, 'duration),
  intParaMeta('PACKET_SIZE, """ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0,
                             ParameterDescription.parameter( "pkt_size", StringConversion.integer() ).withDefault( 0 ) )""", 'getPacketSize, 'packetSize),
  intParaMeta('UDP_PORT, """ParameterDescription.parameter( "udp_port", StringConversion.integer() ).withDefault( 0 )
                          .withBounds( 0, 65535, Num.integer )""", 'getUdpPort, 'udpPort),
  ParaMeta('VideoFormat, 'FORMAT, lines("""ParameterDescription.parameter( "format",
                                        StringConversion.convenientPartial( VideoFormat.fromStringFunction() ) )
                                        .withDefault( VideoFormat.JFIF )"""), 'VideoFormat, 'getFormat, 'format),
  intParaMeta('AUDIO_CHANNEL, """ParameterDescription.parameter( "audio", StringConversion.integer() )
                               .withDefault( 0 ).positive( Num.integer )""", 'getAudioChannel, 'audioChannel),
  ParaMeta('TransmissionMode, 'TRANSMISSION_MODE, append(prepend("""ParameterDescription.parameterWithDefault( "txmode", """, lambda("ParameterMap", "TransmissionMode", "map", "map.get( FORMAT ) == VideoFormat.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL")), ", StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) )"), 'TransmissionMode, 'getTransmissionMode, 'transmissionMode),
  ParaMeta('IPAddress, 'SLAVE_IP, lines("""ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial(
                                           IPAddress.fromString )).withDefault( IPAddress.fromString( "0.0.0.0" ).get() )"""), 'IPAddress, 'getSlaveIP, 'slaveIP),
  intParaMeta('OUTPUT_CHANNEL, """ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 )""", 'getOutputChannel, 'outputChannel),
  ParaMeta('ProxyMode, 'PROXY_MODE, lines("""ParameterDescription.parameter( "proxymode",
                                            StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault( ProxyMode.TRANSIENT )"""), 'ProxyMode, 'getProxyMode, 'proxyMode),
  intParaMeta('PROXY_RETRIES, """ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 )""", 'getProxyRetries, 'proxyRetries))

 def main(args: Array[String]) = {
  print(lines("package uk.org.netvu.protocol;") ++ blankLine ++
        clazz(lines("A utility class containing ParameterDescriptions that are common between multiple CGIs."),
              "",
              "CommonParameters",
              commonParams.flatMap(param => Field(lines("The specification of the " +++ param.name +++ " parameter."),
                                                  Package,
                                                  Static(true),
                                                  Final(true),
                                                  Symbol("ParameterDescription<" +++ param.storedType +++ ", " +++ param.storedType +++ ">"),
                                                  param.constName,
                                                  param.constructor).toJava)))
 }
}
