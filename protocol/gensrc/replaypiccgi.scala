import Pretty._

object replaypiccgi { def main(args: Array[String]): Unit = {  
  val params = List(
   ParaMeta('Control, 'CONTROL, lines("""ParameterDescription.parameter( "control",
                                        StringConversion.convenientPartial( Control.fromStringFunction() ) )
                                        .withDefault( Control.STOP )"""), 'Control, 'getControl, 'control),
   intParaMeta('GMT_TIME, """ParameterDescription.parameter( "time", StringConversion.convenientPartial( fromTimeFunction() ) )
               .withDefault( 0 )""", 'getGMTTime, 'gmtTime),
   intParaMeta('LOCAL_TIME, """ParameterDescription.parameter( "local", StringConversion.convenientPartial( fromTimeFunction() ) )
               .withDefault( 0 )""", 'getLocalTime, 'localTime),
   ParaMeta('String, 'TEXT, lines("""ParameterDescription.parameter( "text", StringConversion.string() ).withDefault( "" )"""), 'String, 'getText, 'text),
   intParaMeta('TIME_RANGE, """ParameterDescription.parameter( "timerange", StringConversion.integer() ).withDefault( 0 )""", 'getTimeRange, 'timeRange),
   intParaMeta('FAST_FORWARD_MULTIPLIER, """ParameterDescription.parameter( "ffmult", StringConversion.integer() )
               .withDefault( 0 ).withBounds( 0, 256, Num.integer )""", 'getFastForwardMultiplier, 'fastForwardMultiplier),
   intParaMeta('REFRESH, """ParameterDescription.parameter( "refresh", StringConversion.integer() )
                             .withDefault( 0 )""", 'getRefresh, 'refresh))

  val packageName = "uk.org.netvu.protocol" 
  val className = 'ReplayPicCGI
  val urlPart = "/replay_pic.cgi?"

 val extras =
  Enum("Control", "Various video playback modes.", Static(true),
       List(NameAndDescription("PLAY", "Play video forwards at its original speed"), NameAndDescription("FFWD", "Play video forwards at a speed controlled by the fast-forward multiplier"),
            NameAndDescription("RWND", "Play video backwards"),
            NameAndDescription("STOP", "Stop playing video"))).toJava ++
 blockComment(lines("A Function that parses a timestamp either in HH:mm:ss:dd:MM:yy format or as a Julian time.") ++ blankLine ++ returnDoc("a Function that parses a timestamp.")) ++
 lines("static Function<String, Option<Integer>> fromTimeFunction()") ++ brace(append(prepend("return ", function("String", "Option<Integer>", "s", lines(
    """java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yy");
    try""") ++ brace(lines("return Option.getFullOption((int)(format.parse(s).getTime()/1000));")) ++ lines(
     "catch (java.text.ParseException e)") ++ brace(lines(
      "try") ++ brace(lines(
       "return Option.getFullOption(Integer.parseInt(s));")) ++ lines(
      "catch (NumberFormatException e2)") ++ brace(lines(
       """return Option.getEmptyOption("Cannot parse " + s + " as a timestamp.");"""))))), ";")) ++ blankLine

 CodeGen.generate(packageName, lines(
  """A parameter list for a replay_pic.cgi query.
  Use {@link ReplayPicCGI.Builder} to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromString(String)}."""), className, params, urlPart, extras)
} }
