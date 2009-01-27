object Pretty {
 trait Show[T] { def show(toShow: T): String }
 def show[T](toShow: T)(implicit show: Show[T]) = show.show(toShow)
 implicit val symbolShow = new Show[Symbol] { def show(toShow: Symbol) = toShow.toString.substring(1) }
 case class WithShow[T](t: T)(implicit showInstance: Show[T]) { def show = showInstance.show(t) }
 implicit def withShow[T](t: T)(implicit show: Show[T]) = WithShow(t)

 case class StringWithBetterConcat(s: String) { def +++(t: String) = s + t
                                                def +++[T](t: T)(implicit show: Show[T]) = s + t.show }
 implicit def stringWithBetterConcat(s: String) = StringWithBetterConcat(s)

 val tab = 4
 val max = 120

 def lines(s: String): Iterable[String] = s.split("\n").map(_ replaceAll ("^ *", ""))
 def brace(lines: Iterable[String]): Iterable[String] = List("{") ++ lines.map(" " * tab +++ _) ++ List("}")
 def wrapped(lines: Iterable[String]) = lines.map(" " * (tab * 2) +++ _)
 def blockComment(lines: Iterable[String]): Iterable[String] = List("/**") ++ lines.map(" * " +++ _) ++ List(" */")
 def paramDoc(parameter: Parameter) = List(
  "@param " +++ parameter.name,
  "       " +++ parameter.description +++ "."
 )

 def throwsDoc(exceptionType: String, reason: String) = List(
  "@throws " +++ exceptionType,
  "        " +++ reason +++ "."
 )

 def throwsNpeDoc(params: List[Parameter]) = {
  val nonPrimitives = params.filter(!_.isPrimitive)
  nonPrimitives match { case Nil => Nil
                        case _ :: _ => throwsDoc("NullPointerException", "if " +++ (nonPrimitives match { case x :: Nil => x.name.show +++ " is "
                                                                                                      case x :: y :: Nil => x.name.show +++ " or " +++ y.name +++ " are "
                                                                                                      case _ => "any of " +++ nonPrimitives.map(_.name.show).mkString(", ") +++ " are " }) +++ "null") }
 }

 def returnDoc(description: String) = lines("@return " +++ description)
 def blankLine = List("")
 def blankFinal(description: Iterable[String], `type`: String, name: String) = blockComment(description) ++ lines("private final " +++ `type` +++ " " +++ name +++ ";") ++ blankLine
 def privateStatic(description: Iterable[String], `type`: Symbol, name: Symbol, value: Iterable[String]) =
  blockComment(description) ++ lines("private static final " +++ `type` +++ " " +++ name +++ " = ") ++ append(wrapped(value), ";") ++ blankLine

 def privateField(description: Iterable[String], `type`: String, name: String, value: String) = blockComment(description) ++ lines("private " +++ `type` +++ " " +++ name +++ " = " +++ value +++ ";") ++ blankLine

 def checkParametersLine(parameters: List[Parameter]) = { val nonPrimitives = parameters.filter(!_.isPrimitive)
                                                          nonPrimitives match {
                                                           case Nil => Nil
                                                           case x :: y => List("CheckParameters.areNotNull( " +++ nonPrimitives.map(_.name.show).reduceLeft(_ +++ ", " +++ _) +++ " );")
                                                          }
                                                        }

 def packagePrivateConstructor(description: Iterable[String], className: Symbol, parameters: List[Parameter], body: Iterable[String]): Iterable[String] = {
  val nonPrimitives = parameters.filter(!_.isPrimitive)
  
  blockComment(description ++ parameters.flatMap(paramDoc) ++ throwsNpeDoc(parameters)) ++
  lines(className.show +++ "( " +++ reduceLeftOr(parameters.map(_.typeThenName), "")(_ +++ ", " +++ _) +++ " )") ++ brace(checkParametersLine(parameters) ++ body) ++ blankLine
 }

 def nonPrimitives(parameters: List[Parameter]) = parameters.filter(!_.isPrimitive)

 def staticPackagePrivateMethod(description: Iterable[String], returnDocPart: String, returnType: Symbol, name: Symbol, parameters: List[Parameter], body: Iterable[String]) =
  Method(Package, Static(true), TypeParameters(Nil), Returns(returnType -> returnDocPart), name, parameters, description, Nil, body).toJava

 def publicMethod(description: Iterable[String], returnDocPart: String, returnType: Symbol, name: Symbol, parameters: List[Parameter], body: Iterable[String]) =
  Method(Public, Static(false), TypeParameters(Nil), Returns(returnType -> returnDocPart), name, parameters, description, Nil, body).toJava

 def clazz(description: Iterable[String], modifiers: String, name: String, body: Iterable[String]) = blockComment(description) ++ lines(modifiers +++ " class " +++ name) ++ brace(body)
 private def reduceLeftOr[T](list: List[T], or: T)(f: (T, T) => T): T = list match { case Nil => or
                                                                                     case x :: y => list.reduceLeft(f) }
 def print(lines: Iterable[String]) = {
  if (lines.filter(_.length > 120).toList.size != 0)
   System.err.println(lines.filter(_.length > 120).mkString("\n"))
  println(lines.foldLeft(new StringBuilder)(_ append _ append '\n'))
 }

 def Try(body: Iterable[String]) = lines("try") ++ brace(body)
 def Finally(body: Iterable[String]) = lines("finally") ++ brace(body)

 def append(lines: Iterable[String], s: String): Iterable[String] = lines.toList match { case list => list.dropRight(1) ++ List(list.last +++ s) }
 def append(one: Iterable[String], two: Iterable[String]): Iterable[String] = { val a = one.toList
                                                                                val b = two.toList
                                                                                a.dropRight(1) ++ List(a.last +++ b.head) ++ b.drop(1) }
 def prepend(s: String, lines: Iterable[String]) = lines.toList match { case list => (s +++ list.head) :: list.drop(1) }
}

import Pretty._

case class Parameter(`type`: Symbol, name: Symbol, description: String) { def typeThenName = "final " +++ `type` +++ " " +++ name
                                                                          def isPrimitive = `type`.show charAt 0 isLowerCase }

sealed abstract class Visibility(override val toString: String)
case object Public extends Visibility("public")
case object Package extends Visibility("")
case object Private extends Visibility("private")

case class Static(is: Boolean) { val isnt = !is }
object Static { implicit val showStatic = new Show[Static] { def show(static: Static) = if (static.is) "static" else "" } }
import Static._

case class Final(is: Boolean) { val isnt = !is }
case class TypeParameters(value: List[(Symbol, String)]) { override def toString = value.map({ case (name, description) => name.show }).mkString(", ") }
case class Returns(value: (Symbol, String)) { val `type` = value._1
                                              val description = value._2 }


case class Method(visibility: Visibility, isStatic: Static, typeParameters: TypeParameters, returns: Returns, name: Symbol, parameters: List[Parameter], description: Iterable[String], throwsDocs: List[NameAndDescription], body: Iterable[String]) {
 def toJava = blockComment(description ++ blankLine ++ (typeParameters.value.map(tp => Parameter('fake, Symbol("<" +++ tp._1 +++ ">"), tp._2)) ++ parameters).flatMap(paramDoc) ++ returnDoc(returns.description) ++ throwsDocs.flatMap(t => throwsDoc(t.name, t.description)) ++ throwsNpeDoc(parameters)) ++
  lines(visibility.toString +++ (if (isStatic.is) " static " else " ") +++ (typeParameters.toString match {
   case "" => ""
   case s => "<" +++ s +++ ">"
  }) +++ returns.`type`.show +++ " " +++ name.show +++ (parameters.map(_.typeThenName).mkString(", ") match { case "" => "()"
                                                                                           case s => "( " +++ s +++ " )" })) ++
  brace(checkParametersLine(parameters) ++ body) ++ blankLine
}

case class Field(description: Iterable[String], visibility: Visibility, isStatic: Static, isFinal: Final, `type`: Symbol, name: Symbol, value: Iterable[String]) {
 def toJava =
  blockComment(description) ++
  append(lines(visibility.toString +++ (if (isStatic.is) " static " else " ") +++ (if (isFinal.is) " final " else " ") +++ `type` +++ " " +++ name +++ " = ") ++ wrapped(value), ";") ++
  blankLine
}

case class ParaMeta(storedType: Symbol, constName: Symbol, constructor: Iterable[String], publicType: Symbol, getterName: Symbol, name: Symbol) {
 def withConstName(newName: Symbol) = ParaMeta(storedType, newName, constructor, publicType, getterName, name)
}

object intParaMeta {
 def apply(constName: Symbol, constructor: String, getterName: Symbol, name: Symbol) = ParaMeta('Integer, constName, lines(constructor), 'int, getterName, name)
}

object lambda {
 def apply(in: String, out: String, name: String, body: String) =
  lines("new Function<" +++ in +++ ", " +++ out +++ ">()") ++
  brace(lines("""@Override
              public """ +++ out +++ " apply( " +++ in +++ " " +++ name +++ " )") ++
        brace(lines("return " +++ body +++ ";")))
}

object function {
 def apply(in: String, out: String, name: String, body: Iterable[String]) =
  lines("new Function<" +++ in +++ ", " +++ out +++ ">()") ++
  brace(lines("""@Override
              public """ +++ out +++ " apply(" +++ in +++ " " +++ name +++ " )") ++
        brace(body))
}

case class NameAndDescription(name: String, description: String) { def toJava = blockComment(lines(description +++ ".")) ++ lines(name) }

case class Enum(name: String, description: String, static: Static, members: List[NameAndDescription]) {
 def toJava = {
  val fromStringToEnum = staticPackagePrivateMethod(lines(
   """A Function that, given a String, will produce an Option containing
      a member of """ +++ name +++ """ if the passed-in String matches it (ignoring case), and an empty
      Option otherwise."""), "a Function that parses a String into a " +++ name, Symbol("Function<String, Option<" +++ name +++ ">>"), 'fromStringFunction, Nil, 
     append(prepend("return ", function("String", "Option<" +++ name +++ ">", "s", lines(
      "for ( final " +++ name +++ " element: values() )") ++ brace(lines(
       "if ( element.toString().equalsIgnoreCase( s ) )") ++ brace(lines(
        "return Option.getFullOption( element );"))) ++ lines("return Option.getEmptyOption( s + \" is not a valid " +++ name +++ " element \" );"))), ";"))
  blockComment(lines(description)) ++
  lines("public " +++ static +++ " enum " +++ name) ++
  brace(append(members.map(_.toJava).reduceLeft((acc, m) => append(acc, ",") ++ blankLine ++ m), ";") ++ blankLine ++ fromStringToEnum) ++
  blankLine
 }
}

object displaypiccgi { def main(args: Array[String]): Unit = {  
 val fields = List(
  intParaMeta('DWELL_TIME, """ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 )""", 'getDwellTime, 'dwellTime),
   intParaMeta('PRESELECTOR, """ParameterDescription.parameter( "presel", StringConversion.integer() )
                         .withDefault( 0 ).withBounds( 0, 3, Num.integer )""", 'getPreselector, 'preselector),
   intParaMeta('CHANNEL, """ParameterDescription.parameter( "channel", StringConversion.integer() )
                          .withDefault( -1 ).withBounds( -1, 1, Num.integer )""", 'getChannel, 'channel),

   intParaMeta('QUANTISATION_FACTOR, """ParameterDescription.parameter( "forcedq", StringConversion.integer() )
                           .withDefault( 0 ).withBounds( 0, 255, Num.integer ).disallowing( 1 )""", 'getQuantisationFactor, 'quantisationFactor),
   intParaMeta('QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES, """ParameterDescription.parameter( "telemQ", StringConversion.integer() )
                          .withDefault( -1 ).withBounds( -1, Integer.MAX_VALUE, Num.integer )""", 'getQuantisationFactorForTelemetryImages, 'quantisationFactorForTelemetryImages),
   ParaMeta('AudioMode, 'AUDIO_MODE, lines("""ParameterDescription.parameter( "audmode",
                                             StringConversion.convenientPartial( AudioMode.fromStringFunction() ) )
                                             .withDefault( AudioMode.UDP )"""), 'AudioMode, 'getAudioMode, 'audioMode),
   intParaMeta('PICTURES_PER_SECOND, """ParameterDescription.parameter( "pps", StringConversion.integer() ).withDefault( 0 )""", 'getPicturesPerSecond, 'picturesPerSecond),
   intParaMeta('MP4_BITRATE, """ParameterDescription.parameter( "mp4rate", StringConversion.integer() ).withDefault( 0 )""", 'getMp4Bitrate, 'mp4Bitrate),
   intParaMeta('PROXY_PRIORITY, """ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 )""", 'getProxyPriority, 'proxyPriority),
   intParaMeta('BUFFER_COUNT, """ParameterDescription.parameter( "nbuffers", StringConversion.integer() )
               .withDefault( 0 ).notNegative( Num.integer )""", 'getBufferCount, 'bufferCount)
  )

 val packageName = "uk.org.netvu.protocol" 
 val className = 'DisplayPicCGI
 val urlPart = "/display_pic.cgi?"

 val extras =
  Enum("AudioMode", "The possible mechanisms for returning audio data.", Static(true),
       List(NameAndDescription("UDP", "Out of band UDP data"), NameAndDescription("INLINE", "In-band data interleaved with images"))).toJava

 CodeGen.generate(packageName, lines(
  """A parameter list for a display_pic.cgi query.
  Use {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or {@link DisplayPicCGI#fromString(String)}."""), className, fields, urlPart, extras)
} }

object CodeGen {
 def generate(packageName: String, classComment: Iterable[String], className: Symbol, params: List[ParaMeta], urlPart: String, extras: Iterable[String]) {
  print(lines(
   """package """ +++ packageName +++ """;

   import java.util.List;
   import java.util.ArrayList;
   import uk.org.netvu.util.CheckParameters;   
   """) ++ blockComment(classComment) ++ lines(
   """public final class """ +++ className) ++ brace(
    blankFinal(lines("The ParameterMap to get values from."), "ParameterMap", "parameterMap") ++
    packagePrivateConstructor(lines("Constructs a " +++ className +++ ", using the values from the specified ParameterMap."), className, List(Parameter('ParameterMap, 'parameterMap, "the ParameterMap to get values from")), lines("this.parameterMap = parameterMap;")) ++
    privateStatic(lines("All the parameter specifications, used in parsing URLs."), Symbol("List<ParameterDescription<?, ?>>"), 'params, lines("new ArrayList<ParameterDescription<?, ?>>()")) ++
    params.flatMap(param => privateStatic(lines("The specification of the " +++ param.name +++ " parameter."), Symbol("ParameterDescription<" +++ param.storedType +++ ", " +++ param.storedType +++ ">"), param.constName, param.constructor)) ++
    (params ++ commonparameters.commonParams.map(p => p.withConstName(Symbol("CommonParameters." +++ p.constName)))).flatMap(param => publicMethod(lines("Gets the value of the " +++ param.name +++ " parameter."), "the value of the " +++ param.name +++ " parameter.", param.publicType, param.getterName, Nil, lines("return parameterMap.get( " +++ param.constName +++ " );"))) ++
    clazz(lines(
     """A builder that takes in all the optional values for """ +++ className +++ " and produces a " +++ className +++ """ when build() is
        called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can
        only have build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its
        values after calling build() will cause an IllegalStateException."""), "public static final", "Builder",
     privateField(lines(
      """The values supplied for each parameter so far.
         When this is an empty Option, the Builder is in an invalid state, the reason for
         which is stored in the Option."""),
      "Option<ParameterMap>", "parameterMap", "Option.getFullOption( new ParameterMap() )") ++
     
    (params ++ commonparameters.commonParams.map(p => p.withConstName(Symbol("CommonParameters." +++ p.constName)))).flatMap(param => publicMethod(
     lines("Sets the " +++ param.name +++ " parameter in the builder."), "the Builder", 'Builder, param.name, List(Parameter(param.publicType, param.name, "the value to store as the " +++ param.name +++ " parameter")), 
     lines("return set( " +++ param.constName +++ ", " +++ param.name +++ " );"))) ++ 
     
     Method(Private, Static(false), TypeParameters(List('T -> "the input type of the specified parameter")), Returns('Builder -> "the Builder"), 'set,
            List(Parameter(Symbol("ParameterDescription<T, ?>"), 'parameter, "the parameter to set a value for"), Parameter('T, 'value, "the value to give that parameter")),
            lines("Sets the value of a parameter to a given value, and returns the Builder."), List(NameAndDescription("IllegalStateException", "if the Builder has already been built once")),
            lines("if ( parameterMap.isEmpty() )") ++ brace(
             lines("""final String message = "The Builder has already been built (build() has been called on it).";
                   throw new IllegalStateException( message );""")) ++
            lines("""parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
                  return this;""")).toJava ++

     Method(Public, Static(false), TypeParameters(Nil), Returns(className -> ("a " +++ className.show +++ " containing the values from this Builder")), 'build, Nil,
            lines("Constructs a " +++ className +++ " with the values from this Builder."), List(NameAndDescription("IllegalStateException", "if the Builder has already been built")),
            Try(lines("return new " +++ className +++ "( parameterMap.get() );")) ++ Finally(lines("""parameterMap = Option.getEmptyOption( "This Builder has already been built once." );"""))).toJava) ++

     lines("static") ++ brace((params ++ commonparameters.commonParams.map(p => p.withConstName(Symbol("CommonParameters." +++ p.constName)))) map (param => "params.add( " +++ param.constName +++" );")) ++ extras ++

     blockComment(lines("Converts this " +++ className +++""" into a String containing a URL beginning with 
                        """ +++ urlPart +" and containing the supplied parameters.") ++ returnDoc("a String containing a URL beginning with " +++ urlPart +++ " and containing the supplied parameters")) ++
     lines("""@Override
           public String toString()""") ++ brace(lines("return \"" +++ urlPart +++ "\" + parameterMap.toURLParameters( params );")) ++ blankLine ++

     Method(Public, Static(true), TypeParameters(Nil), Returns(className, "A " +++ className +++ " describing the specified URL"), 'fromString,
            List(Parameter('String, 'string, "the String to parse")), lines("Converts a String containing a URL describing a " +++ urlPart +++ " request into a " +++ className +++ "."),
            List(NameAndDescription("IllegalArgumentException", "if the String cannot be parsed into a " +++ className)),
            lines("if ( string.length() == 0 )") ++ brace(lines("""throw new IllegalArgumentException( "Cannot parse an empty String into a """ +++ className +++ """." );""")) ++
            lines("""final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
                  if ( map.isEmpty() )""") ++ brace(lines("throw new IllegalArgumentException( map.reason() );")) ++ lines("return new " +++ className +++ "( map.get() );")).toJava))
 }
}
