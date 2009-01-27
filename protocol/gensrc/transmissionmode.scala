import Pretty._

object transmissionmode { def main(args: Array[String]) =
 print(lines("package uk.org.netvu.protocol;") ++ blankLine ++
       Enum("TransmissionMode", "The possible stream headers that the video stream can be wrapped in.", Static(false),
            List(NameAndDescription("MIME", "Multipart MIME"), NameAndDescription("BINARY", "AD's 'binary' format"), NameAndDescription("MINIMAL", "AD's 'minimal' format"))).toJava)
}
