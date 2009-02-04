import Pretty._

object transmissionmode { def main(args: Array[String]) =
 print(lines("""package uk.org.netvu.protocol;
             
                import uk.org.netvu.util.Function;
                import uk.org.netvu.util.Option;""") ++ blankLine ++
       Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", Static(false),
            List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava)
}
