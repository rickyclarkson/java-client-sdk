import Pretty._

object abstractpiccgi {
 val commonParams: List[ParaMeta] = List(
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
  ParaMeta('String, 'SLAVE_IP, lines("""ParameterDescription.parameter( "slaveip", StringConversion.string() )
                                        .withDefault( "0.0.0.0" )"""),
           'String, 'getSlaveIP, 'slaveIP),
  intParaMeta('OUTPUT_CHANNEL, """ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 )""", 'getOutputChannel, 'outputChannel),
  ParaMeta('ProxyMode, 'PROXY_MODE, lines("""ParameterDescription.parameter( "proxymode",
                                            StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault( ProxyMode.TRANSIENT )"""), 'ProxyMode, 'getProxyMode, 'proxyMode),
  intParaMeta('PROXY_RETRIES, """ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 )""", 'getProxyRetries, 'proxyRetries))

 def main(args: Array[String]) = {
  print(lines("""package uk.org.netvu.protocol;
                 
                 import java.util.ArrayList;
                 import java.util.List;
                 import uk.org.netvu.util.CheckParameters;""") ++ blankLine ++
        clazz(lines("A common supertype of DisplayPicCGI and ReplayPicCGI containing common code."),
              "",
              "AbstractPicCGI",              
              blankFinal(lines("The ParameterMap to get values from."), "ParameterMap", "parameterMap") ++
              blockComment(lines("Constructs an AbstractPicCGI with the specified ParameterMap.") ++
                           paramDoc(Parameter('ParameterMap, 'parameterMap, "The ParameterMap to store parameter values in."))
                          ) ++
              lines("AbstractPicCGI( ParameterMap parameterMap )") ++ brace(lines("this.parameterMap = parameterMap;")) ++
              commonParams.flatMap(param => Field(lines("The specification of the " +++ param.name +++ " parameter."),
                                                  Package,
                                                  Static(true),
                                                  Final(true),
                                                  Symbol("ParameterDescription<" +++ param.storedType +++ ", " +++ param.storedType +++ ">"),
                                                  param.constName,
                                                  param.constructor).toJava) ++
              packagePrivateStatic(lines("All the common parameter specifications, used in parsing URLs."),
                                   Symbol("List<ParameterDescription<?, ?>>"),
                                   'commonParams, lines("new ArrayList<ParameterDescription<?, ?>>()")) ++
              lines("static") ++ brace(commonParams map (param => "commonParams.add( " +++ param.constName +++ " );")) ++
              commonParams.flatMap(_.getter) ++
              clazz(lines("A common supertype of DisplayPicCGI.Builder and ReplayPicCGI.Builder containing common code.") ++
                    paramDoc(Parameter('fake, Symbol("<Builder>"), "The type of the subclass of AbstractBuilder")),
                    "static abstract",
                    "AbstractBuilder<Builder extends AbstractBuilder<Builder>>",
                    blockComment(lines("Gives this instance as a Builder, instead of an AbstractBuilder&lt;Builder&gt;.") ++ blankLine ++
                                 returnDoc("this instance as a Builder")) ++
                    lines("abstract Builder self();") ++
                    packagePrivateField(lines("""The values supplied for each parameter so far.
                                       When this is an empty Option, the Builder is in an invalid state, the reason for
                                        which is stored in the Option."""),
                                 "Option<ParameterMap>", "parameterMap", "Option.getFullOption( new ParameterMap() )") ++
                    commonParams.flatMap(_.setter) ++
                    Method(Package, Static(false), TypeParameters(List('T -> "the input type of the specified parameter")), Returns('Builder -> "the Builder"), 'set,
                           List(Parameter(Symbol("ParameterDescription<T, ?>"), 'parameter, "the parameter to set a value for"), Parameter('T, 'value, "the value to give that parameter")),
                           lines("Sets the value of a parameter to a given value, and returns the Builder."), List(NameAndDescription("IllegalStateException", "if the Builder has already been built once")),
                           lines("if ( parameterMap.isEmpty() )") ++ brace(
                            lines("""final String message = "The Builder has already been built (build() has been called on it).";
                                  throw new IllegalStateException( message );""")) ++
                           lines("""parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
                                 return self();""")).toJava)))
 }
}
