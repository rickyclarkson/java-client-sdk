package uk.org.netvu.protocol;

import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;

/**
 * The possible stream headers that the video stream can be wrapped in.
 */
public  enum TransmissionMode
{
    /**
     * Multipart MIME.
     */
    MIME,
    
    /**
     * AD's 'binary' format.
     */
    BINARY,
    
    /**
     * AD's 'minimal' format.
     */
    MINIMAL;
    
    /**
     * A Function that, given a String, will produce an Option containing
     * a member of TransmissionMode if the passed-in String matches it (ignoring case), and an empty
     * Option otherwise.
     * 
     * @return a Function that parses a String into a TransmissionMode
     */
    static Function<String, Option<TransmissionMode>> fromStringFunction()
    {
        return new Function<String, Option<TransmissionMode>>()
        {
            @Override
            public Option<TransmissionMode> apply(String s )
            {
                for ( final TransmissionMode element: values() )
                {
                    if ( element.toString().equalsIgnoreCase( s ) )
                    {
                        return Option.getFullOption( element );
                    }
                }
                return Option.getEmptyOption( s + " is not a valid TransmissionMode element " );
            }
        };
    }
    
}


