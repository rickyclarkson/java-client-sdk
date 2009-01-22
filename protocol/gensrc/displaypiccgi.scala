object Pretty {
 val tab = 4
 val max = 120

 def lines(s: String): Iterable[String] = s.split("\n").map(_ replaceAll ("^ *", ""))
 def brace(lines: Iterable[String]): Iterable[String] = List("{") ++ lines.map(" " * tab + _) ++ List("}")
 def wrapped(lines: Iterable[String]) = lines.map(" " * (tab * 2) + _)
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

 def returnDoc(description: String) = lines("@return "+description)
 def blankLine = List("")
 def blankFinal(description: Iterable[String], `type`: String, name: String) = blockComment(description) ++ lines("private final "+`type`+" "+name+";") ++ blankLine
 def privateStatic(description: Iterable[String], `type`: String, name: String, value: Iterable[String]) =
  blockComment(description) ++ lines("private static final "+`type` + " " + name + " = ") ++ append(wrapped(value), ";") ++ blankLine

 def privateField(description: Iterable[String], `type`: String, name: String, value: String) = blockComment(description) ++ lines("private "+`type`+" "+name+" = "+value+";") ++ blankLine

 def checkParametersLine(parameters: List[Parameter]) = { val nonPrimitives = parameters.filter(!_.isPrimitive)
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

 def nonPrimitives(parameters: List[Parameter]) = parameters.filter(!_.isPrimitive)

 def staticPackagePrivateMethod(description: Iterable[String], returnDocPart: String, returnType: String, name: String, parameters: List[Parameter], body: Iterable[String]) =
  Method(Package, IsStatic(true), Nil, NameAndDescription(returnType, returnDocPart), name, parameters, description, Nil, body).toJava

 def publicMethod(description: Iterable[String], returnDocPart: String, returnType: String, name: String, parameters: List[Parameter], body: Iterable[String]) =
  Method(Public, IsStatic(false), Nil, NameAndDescription(returnType, returnDocPart), name, parameters, description, Nil, body).toJava

 def clazz(description: Iterable[String], modifiers: String, name: String, body: Iterable[String]) = blockComment(description) ++ lines(modifiers + " class " + name) ++ brace(body)
 private def reduceLeftOr[T](list: List[T], or: T)(f: (T, T) => T): T = list match { case Nil => or
                                                                                     case x :: y => list.reduceLeft(f) }
 def print(lines: Iterable[String]) = {
  if (lines.filter(_.length > 120).toList.size != 0)
   System.err.println(lines.filter(_.length > 120).mkString("\n"))
  println(lines.foldLeft(new StringBuilder)(_ append _ append '\n'))
 }

 def Try(body: Iterable[String]) = lines("try") ++ brace(body)
 def Finally(body: Iterable[String]) = lines("finally") ++ brace(body)

 def append(lines: Iterable[String], s: String): Iterable[String] = lines.toList match { case list => list.dropRight(1) ++ List(list.last + s) }
 def prepend(s: String, lines: Iterable[String]) = lines.toList match { case list => (s + list.head) :: list.drop(1) }
}

case class Parameter(`type`: String, name: String, description: String) { def typeThenName = `type` + " " + name
                                                                          def isPrimitive = `type` charAt 0 isLowerCase }

sealed abstract class Visibility(override val toString: String)
case object Public extends Visibility("public")
case object Package extends Visibility("")
case object Private extends Visibility("private")

case class IsStatic(is: Boolean) { val isnt = !is
                                   def toJava = if (is) "static" else ""
                                   override def toString = toJava }

case class TypeParameter(name: String, description: String)

import Pretty._

case class Method(visibility: Visibility, isStatic: IsStatic, typeParameters: List[TypeParameter], returnType: NameAndDescription, name: String, parameters: List[Parameter], description: Iterable[String], throwsDocs: List[NameAndDescription], body: Iterable[String]) {
 def toJava = blockComment(description ++ blankLine ++ (typeParameters.map(tp => Parameter("fake", "<"+tp.name+">", tp.description)) ++ parameters).flatMap(paramDoc) ++ returnDoc(returnType.description) ++ throwsDocs.flatMap(t => throwsDoc(t.name, t.description)) ++ throwsNpeDoc(parameters)) ++
  lines(visibility.toString+(if (isStatic.is) " static " else " ")+(typeParameters.map(_.name).mkString(", ") match {
   case "" => ""
   case s => "<" + s + ">"
  }) + returnType.name + " " + name + (parameters.map(_.typeThenName).mkString(", ") match { case "" => "()"
                                                                                            case s => "( " + s + " )" })) ++
  brace(checkParametersLine(parameters) ++ body) ++ blankLine
}

case class ParaMeta(storedType: String, constName: String, constructor: Iterable[String], publicType: String, getterName: String, name: String)

object intParaMeta {
 def apply(constName: String, constructor: String, getterName: String, name: String) = ParaMeta("Integer", constName, lines(constructor), "int", getterName, name)
}

object lambda {
 def apply(in: String, out: String, name: String, body: String) =
  lines("new Function<"+in+", "+out+">()") ++
  brace(lines("""@Override
              public """+out+" apply( "+in+" "+name+" )") ++
        brace(lines("return "+body+";")))
}

object function {
 def apply(in: String, out: String, name: String, body: Iterable[String]) =
  lines("new Function<" + in +", " + out + ">()") ++
  brace(lines("""@Override
              public """ + out + " apply(" + in + " " + name + " )") ++
        brace(body))
}

case class NameAndDescription(name: String, description: String) { def toJava = blockComment(lines(description + ".")) ++ lines(name) }

case class Enum(name: String, description: String, static: IsStatic, members: List[NameAndDescription]) {
 def toJava = {
  val fromStringToEnum = staticPackagePrivateMethod(lines(
   """A Function that, given a String, will produce an Option containing
      a member of """+name+""" if the passed-in String matches it (ignoring case), and an empty
      Option otherwise."""), "a Function that parses a String into a "+name, "Function<String, Option<"+name+">>", "fromStringFunction", Nil, 
     append(prepend("return ", function("String", "Option<"+name+">", "s", lines(
      "for ( final "+name+" element: values() )") ++ brace(lines(
       "if ( element.toString().equalsIgnoreCase( s ) )") ++ brace(lines(
        "return Option.getFullOption( element );"))) ++ lines("return Option.getEmptyOption( s + \" is not a valid " + name + " element \" );"))), ";"))
  blockComment(lines(description)) ++
  lines("public "+static+" enum "+name) ++
  brace(append(members.map(_.toJava).reduceLeft((acc, m) => append(acc, ",") ++ blankLine ++ m), ";") ++ blankLine ++ fromStringToEnum) ++
  blankLine
 }
}

object displaypiccgi { def main(args: Array[String]): Unit = {  
  val fields = List(
   intParaMeta("CAM", """ParameterDescription.parameter( "cam", StringConversion.integer() )
                      .withDefault( 1 ).withBounds( 1, 16, Num.integer )""", "getCam", "cam"),
   intParaMeta("FIELDS", """ParameterDescription.parameter( "fields", StringConversion.integer() )
                      .withDefault( 1 ).positive( Num.integer )""", "getFields", "fields"),
   ParaMeta("String", "RES", lines("""ParameterDescription.parameter( "res", StringConversion.string() )
                                   .withDefault( "med" ).allowedValues( "hi", "med", "lo" )"""), "String", "getRes", "res"),
   ParaMeta("Integer", "SEQ", lines("""ParameterDescription.parameter( "seq", StringConversion.hexInt() ).withDefault( 0 )
                                    .withBounds( 0, 0xF, Num.integer )"""), "int", "getSeq", "seq"),
   intParaMeta("DWELL", """ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 )""", "getDwell", "dwell"),
   intParaMeta("ID", """ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 )""", "getId", "id"),
   intParaMeta("DINDEX", """ParameterDescription.parameter( "dindex", StringConversion.integer() ).withDefault( 0 )""", "getDIndex", "dIndex"),
   intParaMeta("PRESEL", """ParameterDescription.parameter( "presel", StringConversion.integer() )
                         .withDefault( 0 ).withBounds( 0, 3, Num.integer )""", "getPresel", "presel"),
   intParaMeta("CHANNEL", """ParameterDescription.parameter( "channel", StringConversion.integer() )
                          .withDefault( -1 ).withBounds( -1, 1, Num.integer )""", "getChannel", "channel"),
   intParaMeta("RATE", """ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 )""", "getRate", "rate"),
   intParaMeta("FORCED_Q", """ParameterDescription.parameter( "forcedq", StringConversion.integer() )
                           .withDefault( 0 ).withBounds( 0, 255, Num.integer ).disallowing( 1 )""", "getForcedQ", "forcedQ"),
   intParaMeta("DURATION", """ParameterDescription.parameter( "duration", StringConversion.integer() )
                              .withDefault( 0 ).notNegative( Num.integer )""", "getDuration", "duration"),
   intParaMeta("N_BUFFERS", """ParameterDescription.parameter( "nbuffers", StringConversion.integer() )
                            .withDefault( 0 ).notNegative( Num.integer )""", "getNBuffers", "nBuffers"),
   intParaMeta("TELEM_Q", """ParameterDescription.parameter( "telemQ", StringConversion.integer() )
                          .withDefault( -1 ).withBounds( -1, Integer.MAX_VALUE, Num.integer )""", "getTelemQ", "telemQ"),
   intParaMeta("PKT_SIZE", """ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0,
                           ParameterDescription.parameter( "pkt_size", StringConversion.integer() ).withDefault( 0 ) )""", "getPktSize", "pktSize"),
   intParaMeta("UDP_PORT", """ParameterDescription.parameter( "udp_port", StringConversion.integer() ).withDefault( 0 )
                           .withBounds( 0, 65535, Num.integer )""", "getUdpPort", "udpPort"),
   ParaMeta("String", "AUDIO", lines("""ParameterDescription.parameter( "audio", StringConversion.string() )
                                     .withDefault( "0" ).allowedValues( "on", "off", "0", "1", "2" )"""), "String", "getAudio", "audio"),
   ParaMeta("Format", "FORMAT", lines("""ParameterDescription.parameter( "format",
                                      StringConversion.convenientPartial( Format.fromStringFunction() ) )
                                      .withDefault( Format.JFIF )"""), "Format", "getFormat", "format"),
   ParaMeta("AudioMode", "AUDIO_MODE", lines("""ParameterDescription.parameter( "audmode",
                                             StringConversion.convenientPartial( AudioMode.fromStringFunction() ) )
                                             .withDefault( AudioMode.UDP )"""), "AudioMode", "getAudioMode", "audioMode"),
   ParaMeta("TransmissionMode", "TRANSMISSION_MODE", append(prepend("""ParameterDescription.parameterWithDefault( "txmode", """, lambda("ParameterMap", "TransmissionMode", "map", "map.get( FORMAT ) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL")), ", StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) )"), "TransmissionMode", "getTransmissionMode", "transmissionMode"),
   intParaMeta("PPS", """ParameterDescription.parameter( "pps", StringConversion.integer() ).withDefault( 0 )""", "getPPS", "pps"),
   intParaMeta("MP4_RATE", """ParameterDescription.parameter( "mp4rate", StringConversion.integer() ).withDefault( 0 )""", "getMp4Rate", "mp4Rate"),
   ParaMeta("IPAddress", "SLAVE_IP", lines("""ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial(
                                           IPAddress.fromString )).withDefault( IPAddress.fromString( "0.0.0.0" ).get() )"""), "IPAddress", "getSlaveIP", "slaveIP"),
   intParaMeta("OP_CHAN", """ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 )""", "getOpChan", "opChan"),
   ParaMeta("ProxyMode", "PROXY_MODE", lines("""ParameterDescription.parameter( "proxymode",
                                             StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault( ProxyMode.TRANSIENT )"""), "ProxyMode", "getProxyMode", "proxyMode"),
   intParaMeta("PROXY_PRI", """ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 )""", "getProxyPri", "proxyPri"),
   intParaMeta("PROXY_RETRY", """ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 )""", "getProxyRetry", "proxyRetry")
  )

 val packageName = "uk.org.netvu.protocol" 
 val className = "DisplayPicCGI"
 val urlPart = "/display_pic.cgi?"

 val extras =
  Enum("Format", "The possible formats that the video stream can be returned as.", IsStatic(true),
       List(NameAndDescription("JFIF", "Complete JFIF (JPEG) image data"), NameAndDescription("JPEG", "Truncated JPEG image data"), NameAndDescription("MP4", "MPEG-4 image data"))).toJava ++
  Enum("AudioMode", "The possible mechanisms for returning audio data.", IsStatic(true),
       List(NameAndDescription("UDP", "Out of band UDP data"), NameAndDescription("INLINE", "In-band data interleaved with images"))).toJava ++
  Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", IsStatic(true),
       List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava

 CodeGen.generate(packageName, lines(
  """A parameter list for a display_pic.cgi query.
  Use {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or {@link DisplayPicCGI#fromString(String)}."""), className, fields, urlPart, extras)
} }

object CodeGen {
 def generate(packageName: String, classComment: Iterable[String], className: String, params: List[ParaMeta], urlPart: String, extras: Iterable[String]) {
  print(lines(
   """package """+packageName+""";

   import java.util.List;
   import java.util.ArrayList;
   import uk.org.netvu.util.CheckParameters;   
   """) ++ blockComment(classComment) ++ lines(
   """public final class """+className) ++ brace(
    blankFinal(lines("The ParameterMap to get values from."), "ParameterMap", "parameterMap") ++
    packagePrivateConstructor(lines("Constructs a "+className+", using the values from the specified ParameterMap."), className, List(Parameter("ParameterMap", "parameterMap", "the ParameterMap to get values from")), lines("this.parameterMap = parameterMap;")) ++
    privateStatic(lines("All the parameter specifications, used in parsing URLs."), "List<ParameterDescription<?, ?>>", "params", lines("new ArrayList<ParameterDescription<?, ?>>()")) ++
    params.flatMap(param => privateStatic(lines("The specification of the "+param.name+" parameter."), "ParameterDescription<"+param.storedType+", "+param.storedType+">", param.constName, param.constructor)) ++
    params.flatMap(param => publicMethod(lines("Gets the value of the "+param.name+" parameter."), "the value of the "+param.name+" parameter.", param.publicType, param.getterName, Nil, lines("return parameterMap.get( "+param.constName+" );"))) ++
    clazz(lines(
     """A builder that takes in all the optional values for """+className+" and produces a "+className+""" when build() is
        called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can
        only have build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its
        values after calling build() will cause an IllegalStateException."""), "public static final", "Builder",
     privateField(lines(
      """The values supplied for each parameter so far.
         When this is an empty Option, the Builder is in an invalid state, the reason for
         which is stored in the Option."""),
      "Option<ParameterMap>", "parameterMap", "Option.getFullOption( new ParameterMap() )") ++
     
    params.flatMap(param => publicMethod(
     lines("Sets the "+param.name+" parameter in the builder."), "the Builder", "Builder", param.name, List(Parameter(param.publicType, param.name, "the value to store as the "+param.name+" parameter")), 
     lines("return set( "+param.constName+", "+param.name+" );"))) ++ 
     
     Method(Private, IsStatic(false), List(TypeParameter("T", "the input type of the specified parameter")), NameAndDescription("Builder", "the Builder"), "set",
            List(Parameter("ParameterDescription<T, ?>", "parameter", "the parameter to set a value for"), Parameter("T", "value", "the value to give that parameter")),
            lines("Sets the value of a parameter to a given value, and returns the Builder."), List(NameAndDescription("IllegalStateException", "if the Builder has already been built once")),
            lines("if ( parameterMap.isEmpty() )") ++ brace(
             lines("""final String message = "The Builder has already been built (build() has been called on it).";
                   throw new IllegalStateException( message );""")) ++
            lines("""parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
                  return this;""")).toJava ++

     Method(Public, IsStatic(false), Nil, NameAndDescription(className, "a "+className+" containing the values from this Builder"), "build", Nil,
            lines("Constructs a "+className+" with the values from this Builder."), List(NameAndDescription("IllegalStateException", "if the Builder has already been built")),
            Try(lines("return new "+className+"( parameterMap.get() );")) ++ Finally(lines("""parameterMap = Option.getEmptyOption( "This Builder has already been built once." );"""))).toJava) ++

     lines("static") ++ brace(params map (param => "params.add( "+param.constName+" );")) ++ extras ++

     blockComment(lines("Converts this "+className+""" into a String containing a URL beginning with 
                        """ + urlPart +" and containing the supplied parameters.") ++ returnDoc("a String containing a URL beginning with " + urlPart + " and containing the supplied parameters")) ++
     lines("""@Override
           public String toString()""") ++ brace(lines("return \"" + urlPart + "\" + parameterMap.toURLParameters( params );")) ++ blankLine ++

     Method(Public, IsStatic(true), Nil, NameAndDescription(className, "an Option containing the parsed "+className+" if the parse succeeded; the Option is empty otherwise"), "fromString",
            List(Parameter("String", "string", "the String to parse")), lines("Converts a String to a "+className+" if it matches one of "+className+"""'s
                                                                              members case-insensitively, returning it in an Option if it does, and
                                                                              returning an empty Option otherwise."""), Nil,
            lines("if ( string.length() == 0 )") ++ brace(lines("""throw new IllegalArgumentException( "Cannot parse an empty String into a """ + className + """." );""")) ++
            lines("""final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
                  if ( map.isEmpty() )""") ++ brace(lines("throw new IllegalArgumentException( map.reason() );")) ++ lines("return new "+className+"( map.get() );")).toJava))
 }
}
