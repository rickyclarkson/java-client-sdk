object Pretty {
 val tab = 4
 def lines(s: String): Iterable[String] = s.split("\n").map(_ replaceAll ("^ *", ""))
 def brace(lines: Iterable[String]): Iterable[String] = List("{") ++ lines.map(" " * tab + _) ++ List("}")
 def blockComment(lines: Iterable[String]): Iterable[String] = List("/**") ++ lines.map(" * " + _) ++ List(" */")
 def paramDoc(parameter: Parameter) = List(
  "@param "+parameter.name,
  "       "+parameter.description+"."
 )

 def throwsDoc(exceptionType: String, reason: String) = List(
  "@throws "+exceptionType,
  "        "+reason+"."
 )

 def throwsNpeDoc(params: List[Parameter]) = {
  val nonPrimitives = params.filter(!_.isPrimitive)
  nonPrimitives match { case Nil => Nil
                        case _ :: _ => throwsDoc("NullPointerException", "if "+(nonPrimitives match { case x :: Nil => x.name + " is "
                                                                                                      case x :: y :: Nil => x.name + " or " + y.name + " are "
                                                                                                      case _ => "any of " + nonPrimitives.map(_.name).mkString(", ") + " are " }) + "null") }
 }

 def throwsNpeDoc(name: String) = throwsDoc("NullPointerException", "if "+name+" is null")
 def throwsNpeDoc(one: String, two: String) = throwsDoc("NullPointerException", "if "+one+" or "+two+" are null")
 def returnDoc(description: String) = lines("@return "+description)
 def blankLine = List("")
 def blankFinal(description: Iterable[String], `type`: String, name: String) = blockComment(description) ++ lines("private final "+`type`+" "+name+";") ++ blankLine
 def privateStatic(description: Iterable[String], `type`: String, name: String, value: String) = blockComment(description) ++ lines("private static final "+`type` + " " + name + " = " + value + ";") ++ blankLine
 def privateField(description: Iterable[String], `type`: String, name: String, value: String) = blockComment(description) ++ lines("private "+`type`+" "+name+" = "+value+";") ++ blankLine

 private def checkParametersLine(parameters: List[Parameter]) = { val nonPrimitives = parameters.filter(!_.isPrimitive)
                                                                  nonPrimitives match {
                                                                   case Nil => Nil
                                                                   case x :: y => List("CheckParameters.areNotNull( " + nonPrimitives.map(_.name).reduceLeft(_ + ", " + _) + " );")
                                                                  }
                                                                }

 def packagePrivateConstructor(description: Iterable[String], className: String, parameters: List[Parameter], body: Iterable[String]): Iterable[String] = {
  val nonPrimitives = parameters.filter(!_.isPrimitive)
  
  blockComment(description ++ parameters.flatMap(paramDoc) ++ throwsNpeDoc(parameters)) ++
  lines(className+"( "+reduceLeftOr(parameters.map(_.typeThenName), "")(_ + ", " + _) + " )") ++ brace(checkParametersLine(parameters) ++ body) ++ blankLine
 }

 private def nonPrimitives(parameters: List[Parameter]) = parameters.filter(!_.isPrimitive)

 def staticPackagePrivateMethod(description: Iterable[String], returnDocPart: String, returnType: String, name: String, parameters: List[Parameter], body: Iterable[String]) =
  blockComment(description ++ parameters.flatMap(paramDoc) ++ nonPrimitives(parameters).flatMap(p => throwsNpeDoc(p.name)) ++ returnDoc(returnDocPart)) ++
  lines("static "+returnType+" "+name+"( "+parameters.map(_.typeThenName).mkString(", ")+" )") ++
  brace(checkParametersLine(parameters) ++ body) ++ blankLine

 def publicMethod(description: Iterable[String], returnDocPart: String, returnType: String, name: String, parameters: List[Parameter], body: Iterable[String]) =
  blockComment(description ++ parameters.flatMap(paramDoc) ++ nonPrimitives(parameters).flatMap(p => throwsNpeDoc(p.name)) ++ returnDoc(returnDocPart)) ++
  lines("public "+returnType+" "+name+"( "+parameters.map(_.typeThenName).mkString(", ") + " )") ++
  brace(checkParametersLine(parameters) ++ body) ++ blankLine

 def clazz(description: Iterable[String], modifiers: String, name: String, body: Iterable[String]) = blockComment(description) ++ lines(modifiers + " class " + name) ++ brace(body)
 private def reduceLeftOr[T](list: List[T], or: T)(f: (T, T) => T): T = list match { case Nil => or
                                                                                     case x :: y => list.reduceLeft(f) }
 def print(lines: Iterable[String]) = println(lines.foldLeft(new StringBuilder)(_ append _ append '\n'))
}

case class Parameter(`type`: String, name: String, description: String) { def typeThenName = `type` + " " + name
                                                                          def isPrimitive = `type` charAt 0 isLowerCase }

import Pretty._

case class ParaMeta(storedType: String, constName: String, constructor: String, publicType: String, getterName: String, name: String)

object intParaMeta {
 def apply(constName: String, constructor: String, getterName: String, name: String) = ParaMeta("Integer", constName, constructor, "int", getterName, name)
}

object lambda {
 def apply(in: String, out: String, name: String, body: String) =
  lines("new Function<"+in+", "+out+">()") ++
  brace(lines("public "+out+" apply("+in+" "+name+")") ++
        brace(lines("return "+body+";")))
}

object function {
 def apply(in: String, out: String, name: String, body: Iterable[String]) =
  lines("new Function<" + in +", " + out + ">()") ++
  brace(lines("public " + out + " apply(" + in + " " + name + " )") ++
        brace(body))
}

case class NameAndDescription(name: String, description: String) { def toJava = blockComment(lines(description)) ++ lines(name) }

case class Enum(name: String, description: String, members: List[NameAndDescription]) {
 def toJava = {
  val fromStringToEnum = staticPackagePrivateMethod(lines(
   """A Function that, given a String, will produce an Option containing
      a member of """+name+""" if the passed-in String matches it (ignoring case), and an empty
      Option otherwise."""), "a Function that parses a String into a "+name, "Function<String, Option<"+name+">>", "fromStringFunction", Nil, 
     lines("return") ++ function("String", "Option<"+name+">", "s", lines(
      "for ( final "+name+" element: values() )") ++ brace(lines(
       "if ( element.toString().equalsIgnoreCase( s ) )") ++ brace(lines(
        "return Option.getFullOption( element );"))) ++ lines("return Option.getEmptyOption( s + \" is not a valid " + name + " element \" );")) ++ lines(";"))
  blockComment(lines(description)) ++ lines("public static enum "+name) ++ brace(members.map(_.toJava).reduceLeft(_ ++ List(",") ++ blankLine ++ _) ++ List(";") ++ blankLine ++ fromStringToEnum)
 }
}

object displaypiccgi { def main(args: Array[String]): Unit = {  
  val fields = List(
   intParaMeta("CAM", """parameter("cam", integer()).withDefault(1).withBounds(1, 16, Num.integer)""", "getCam", "cam"),
   intParaMeta("FIELDS", """parameter("fields", integer()).withDefault(1).positive(Num.integer)""", "getFields", "fields"),
   ParaMeta("String", "RES", """parameter("res", string()).withDefault("med").allowedValues("hi", "med", "lo")""", "String", "getRes", "res"),
   ParaMeta("Integer", "SEQ", """parameter("seq", hexInt()).withDefault(0).withBounds(0, 0xF, Num.integer)""", "int", "getSeq", "seq"),
   intParaMeta("DWELL", """parameter("dwell", integer()).withDefault(0)""", "getDwell", "dwell"),
   intParaMeta("ID", """parameter("id", integer()).withDefault(0)""", "getId", "id"),
   intParaMeta("DINDEX", """parameter("dindex", integer()).withDefault(0)""", "getDIndex", "dIndex"),
   intParaMeta("PRESEL", """parameter("presel", integer()).withDefault(0).withBounds(0, 3, Num.integer)""", "getPresel", "presel"),
   intParaMeta("CHANNEL", """parameter("channel", integer()).withDefault(-1).withBounds(-1, 1, Num.integer)""", "getChannel", "channel"),
   intParaMeta("RATE", """parameter("rate", integer()).withDefault(0)""", "getRate", "rate"),
   intParaMeta("FORCED_Q", """parameter("forcedq", integer()).withDefault(0).withBounds(0, 255, Num.integer).disallowing(1)""", "getForcedQ", "forcedQ"),
   intParaMeta("DURATION", """parameter("duration", integer()).withDefault(0).notNegative(Num.integer)""", "getDuration", "duration"),
   intParaMeta("N_BUFFERS", """parameter("nbuffers", integer()).withDefault(0).notNegative(Num.integer)""", "getNBuffers", "nBuffers"),
   intParaMeta("TELEM_Q", """parameter("telemQ", integer()).withDefault(-1).withBounds(-1, Integer.MAX_VALUE, Num.integer)""", "getTelemQ", "telemQ"),
   intParaMeta("PKT_SIZE", """parameterWithBoundsAndAnException(100, 1500, 0, parameter("pkt_size", integer()).withDefault(0))""", "getPktSize", "pktSize"),
   intParaMeta("UDP_PORT", """parameter("udp_port", integer()).withDefault(0).withBounds(0, 65535, Num.integer)""", "getUdpPort", "udpPort"),
   ParaMeta("String", "AUDIO", """parameter("audio", string()).withDefault("0").allowedValues("on", "off", "0", "1", "2")""", "String", "getAudio", "audio"),
   ParaMeta("Format", "FORMAT", """parameter("format", convenientPartial(Format.fromStringFunction())).withDefault(Format.JFIF)""", "Format", "getFormat", "format"),
   ParaMeta("AudioMode", "AUDIO_MODE", """parameter("audmode", convenientPartial(AudioMode.fromStringFunction())).withDefault(AudioMode.UDP)""", "AudioMode", "getAudioMode", "audioMode"),
   ParaMeta("TransmissionMode", "TRANSMISSION_MODE", """parameterWithDefault("txmode", new Function<ParameterMap, TransmissionMode>() { public TransmissionMode apply(ParameterMap map) { return map.get(FORMAT) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL; } }, convenientPartial(TransmissionMode.fromStringFunction()))""", "TransmissionMode", "getTransmissionMode", "transmissionMode"),
   intParaMeta("PPS", """parameter("pps", integer()).withDefault(0)""", "getPPS", "pps"),
   intParaMeta("MP4_RATE", """parameter("mp4rate", integer()).withDefault(0)""", "getMp4Rate", "mp4Rate"),
   ParaMeta("IPAddress", "SLAVE_IP", """parameter("slaveip", convenientPartial(IPAddress.fromString)).withDefault(IPAddress.fromString("0.0.0.0").get())""", "IPAddress", "getSlaveIP", "slaveIP"),
   intParaMeta("OP_CHAN", """parameter("opchan", integer()).withDefault(-1)""", "getOpChan", "opChan"),
   ParaMeta("ProxyMode", "PROXY_MODE", """parameter("proxymode", convenientPartial(ProxyMode.fromStringFunction())).withDefault(ProxyMode.TRANSIENT)""", "ProxyMode", "getProxyMode", "proxyMode"),
   intParaMeta("PROXY_PRI", """parameter("proxypri", integer()).withDefault(1)""", "getProxyPri", "proxyPri"),
   intParaMeta("PROXY_RETRY", """parameter("proxyretry", integer()).withDefault(0)""", "getProxyRetry", "proxyRetry")
  )

 val packageName = "uk.org.netvu.protocol" 
 val className = "DisplayPicCGI"
 val urlPart = "/display_pic.cgi?"

 val extras = Enum("Format", "The possible formats that the video stream can be returned as.", List(NameAndDescription("JFIF", "Complete JFIF (JPEG) image data"), NameAndDescription("JPEG", "Truncated JPEG image data"), NameAndDescription("MP4", "MPEG-4 image data"))).toJava ++
 Enum("AudioMode", "The possible mechanisms for returning audio data", List(NameAndDescription("UDP", "Out of band UDP data"), NameAndDescription("INLINE", "In-band data interleaved with images"))).toJava ++
 Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava ++
 Enum("ProxyMode", "This controls whether or not a decoder that is connected to by the server maintains connections to cameras set up by the CGI request", List(NameAndDescription("TRANSIENT", "A decoder will clear connections to cameras made by the CGI request after the video stream has terminated"), NameAndDescription("PERSISTENT", "A decoder will maintain connections to cameras made by the CGI request after the video stream has terminated"))).toJava

 CodeGen.generate(packageName, lines(
  """A parameter list for a display_pic.cgi query.
  Use {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or {@link DisplayPicCGI#fromURL(String)}."""), className, fields, urlPart, extras)
} }

object CodeGen {
 def generate(packageName: String, classComment: Iterable[String], className: String, params: List[ParaMeta], urlPart: String, extras: Iterable[String]) {
  print(lines(
   """package """+packageName+""";
   import java.util.*;
   import static uk.org.netvu.protocol.ParameterDescription.*;
   import static uk.org.netvu.protocol.StringConversion.*;
   import uk.org.netvu.util.CheckParameters;   
   """) ++ blockComment(classComment) ++ lines(
   """public final class """+className) ++ brace(
    blankFinal(lines("The ParameterMap to get values from."), "ParameterMap", "parameterMap") ++
    packagePrivateConstructor(lines("Constructs a "+className+", using the values from the specified ParameterMap."), className, List(Parameter("ParameterMap", "parameterMap", "the ParameterMap to get values from")), lines("this.parameterMap = parameterMap;")) ++
    privateStatic(lines("All the parameter specifications, used in parsing URLs."), "List<ParameterDescription<?, ?>>", "params", "new ArrayList<ParameterDescription<?, ?>>();") ++
    params.flatMap(param => privateStatic(lines("The specification of the "+param.name+" parameter."), "ParameterDescription<"+param.storedType+", "+param.storedType+">", param.constName, param.constructor)) ++
    params.flatMap(param => publicMethod(lines("Gets the value of the "+param.name+" parameter."), "the value of the "+param.name+" parameter.", param.publicType, param.getterName, Nil, lines("return parameterMap.get( "+param.constName+" );"))) ++
    clazz(lines(
     """A builder that takes in all the optional values for """+className+" and produces a "+className+""" when build() is
        called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can only have
        build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its values after
        calling build() will cause an IllegalStateException."""), "public static final", "Builder",
     privateField(lines(
      """The values supplied for each parameter so far.
         When this is an empty Option, the Builder is in an invalid state, the reason for which is stored in the Option."""),
      "Option<ParameterMap>", "parameterMap", "Option.getFullOption( new ParameterMap() );") ++
     
    params.flatMap(param => publicMethod(
     lines("Sets the "+param.name+" parameter in the builder."), "the Builder", "Builder", param.name, List(Parameter(param.storedType, param.name, "the value to store as the "+param.name+" parameter")), 
     lines("return set( "+param.constName+", "+param.name+" );"))) ++ 
     
    blockComment(
     lines("Sets the value of a parameter to a given value, and returns the Builder.") ++
     paramDoc(Parameter("fake", "<T>", "the input type of the specified parameter")) ++
     paramDoc(Parameter("fake", "parameter", "the parameter to set a value for")) ++
     paramDoc(Parameter("fake", "value", "the value to give that parameter")) ++
     returnDoc("the Builder") ++
     throwsDoc("IllegalStateException", "if the Builder has already been built once") ++
     throwsNpeDoc("parameter", "value")) ++ lines(
      """private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )""") ++ brace(lines(
     "if ( parameterMap.isEmpty() )") ++ brace(lines(
      """final String message = "The Builder has already been built (build() has been called on it).";
      throw new IllegalStateException( message );""")) ++ lines(
       """parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
       return this;""")) ++ blankLine ++

     (lines(
      """/**
          * Constructs a """+className+""" with the values from this Builder.
          *
          * @throws IllegalStateException
          *         if this Builder has already been built.
          * @return a """+className+""" containing the values from this Builder.
          */
         public """+className+" build()") ++ brace(lines(
      "try") ++ brace(lines("return new "+className+"( parameterMap.get() );")) ++
      lines("finally") ++ brace(lines("""parameterMap = Option.getEmptyOption( "This Builder has already been built once." );"""))))) ++

     lines("static") ++ brace(params map (param => "params.add( "+param.constName+" );")) ++ extras ++ lines(
      """@Override
      public String toString()""") ++ brace(lines("return \"" + urlPart + "\" + parameterMap.toURLParameters( params );")) ++

     lines("""public static """+className+""" fromString( String string )""") ++ brace(lines(
      """CheckParameters.areNotNull( string );
      if ( string.length() == 0 )""") ++ brace(lines("""throw new IllegalArgumentException( "Cannot parse an empty String into a """ + className + """." );""")) ++ lines(
       """final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
           if ( map.isEmpty() )""") ++ brace(lines("throw new IllegalArgumentException( map.reason() );")) ++ lines("return new "+className+"( map.get() );"))))
 }
}
