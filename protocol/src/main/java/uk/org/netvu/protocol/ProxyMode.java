package uk.org.netvu.protocol;

/**
 * This controls whether or not a decoder that is connected to by the server maintains connections to cameras set up by the CGI request
 */
public  enum ProxyMode
{
    /**
     * A decoder will clear connections to cameras made by the CGI request after the video stream has terminated.
     */
    TRANSIENT,
    
    /**
     * A decoder will maintain connections to cameras made by the CGI request after the video stream has terminated.
     */
    PERSISTENT;
    
    /**
     * A Function that, given a String, will produce an Option containing
     * a member of ProxyMode if the passed-in String matches it (ignoring case), and an empty
     * Option otherwise.
     * @return a Function that parses a String into a ProxyMode
     */
    static Function<String, Option<ProxyMode>> fromStringFunction(  )
    {
        return
        new Function<String, Option<ProxyMode>>()
        {
            public Option<ProxyMode> apply(String s )
            {
                for ( final ProxyMode element: values() )
                {
                    if ( element.toString().equalsIgnoreCase( s ) )
                    {
                        return Option.getFullOption( element );
                    }
                }
                return Option.getEmptyOption( s + " is not a valid ProxyMode element " );
            }
        }
        ;
    }
    
}

