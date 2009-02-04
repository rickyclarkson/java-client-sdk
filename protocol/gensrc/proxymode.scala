import Pretty._

object proxymode { def main(args: Array[String]) = {
 print(lines("""package uk.org.netvu.protocol;

                import uk.org.netvu.util.Function;
                import uk.org.netvu.util.Option;""") ++ blankLine ++ 
       Enum("ProxyMode", """This controls whether or not a decoder that is connected to by
                            the server maintains connections to cameras set up by the CGI request.""", Static(false),
            List(NameAndDescription("TRANSIENT", "A decoder will clear connections to cameras made by the CGI request after the video stream has terminated"),
                 NameAndDescription("PERSISTENT", "A decoder will maintain connections to cameras made by the CGI request after the video stream has terminated"))).toJava)
} }
