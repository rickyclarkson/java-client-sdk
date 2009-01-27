import Pretty._

object videoformat { def main(args: Array[String]) = {
 print(lines("package uk.org.netvu.protocol;") ++ blankLine ++
       Enum("VideoFormat", "The possible formats that the video stream can be returned as.", Static(false),
            List(NameAndDescription("JFIF", "Complete JFIF (JPEG) image data"), NameAndDescription("JPEG", "Truncated JPEG image data"), NameAndDescription("MP4", "MPEG-4 image data"))).toJava)
} }
