package uk.org.netvu.protocol;

import java.util.List;
import java.util.ArrayList;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;   


/**
 * A parameter list for a replay_pic.cgi query.
 * Use {@link ReplayPicCGI.Builder} to construct a ReplayPicCGI, or {@link ReplayPicCGI#fromString(String)}.
 * See the <a href="http://adwiki.ad-group.adh/attachment/wiki/DevelopmentSite/AD/Client/Vserve%20Specification.pdf">Video
 * Server Specification</a> for more detail on the replay_pic.cgi query.
 */
public final class ReplayPicCGI extends AbstractPicCGI
{
    /**
     * Constructs a ReplayPicCGI, using the values from the specified ParameterMap.
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    ReplayPicCGI( final ParameterMap parameterMap )
    {
        super( parameterMap );
    }
    
    /**
     * All the parameter specifications, used in parsing URLs.
     */
    private static final List<ParameterDescription<?, ?>> params = 
            new ArrayList<ParameterDescription<?, ?>>();
    
    /**
     * The specification of the control parameter.
     */
    private static final ParameterDescription<Control, Control> CONTROL = 
            ParameterDescription.parameter( "control",
            StringConversion.convenientPartial( Control.fromStringFunction() ) )
            .withDefault( Control.STOP );
    
    /**
     * The specification of the gmtTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> GMT_TIME = 
            ParameterDescription.parameter( "time", StringConversion.convenientPartial( fromTimeFunction() ) )
            .withDefault( 0 );
    
    /**
     * The specification of the localTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> LOCAL_TIME = 
            ParameterDescription.parameter( "local", StringConversion.convenientPartial( fromTimeFunction() ) )
            .withDefault( 0 );
    
    /**
     * The specification of the text parameter.
     */
    private static final ParameterDescription<String, String> TEXT = 
            ParameterDescription.parameter( "text", StringConversion.string() ).withDefault( "" );
    
    /**
     * The specification of the timeRange parameter.
     */
    private static final ParameterDescription<Integer, Integer> TIME_RANGE = 
            ParameterDescription.parameter( "timerange", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the fastForwardMultiplier parameter.
     */
    private static final ParameterDescription<Integer, Integer> FAST_FORWARD_MULTIPLIER = 
            ParameterDescription.parameter( "ffmult", StringConversion.integer() )
            .withDefault( 0 ).withBounds( 0, 256, Num.integer );
    
    /**
     * The specification of the refresh parameter.
     */
    private static final ParameterDescription<Integer, Integer> REFRESH = 
            ParameterDescription.parameter( "refresh", StringConversion.integer() )
            .withDefault( 0 );
    
    /**
     * Gets the value of the control parameter.
     * 
     * @return the value of the control parameter.
     */
    public Control getControl()
    {
        return parameterMap.get( CONTROL );
    }
    
    /**
     * Gets the value of the gmtTime parameter.
     * 
     * @return the value of the gmtTime parameter.
     */
    public int getGMTTime()
    {
        return parameterMap.get( GMT_TIME );
    }
    
    /**
     * Gets the value of the localTime parameter.
     * 
     * @return the value of the localTime parameter.
     */
    public int getLocalTime()
    {
        return parameterMap.get( LOCAL_TIME );
    }
    
    /**
     * Gets the value of the text parameter.
     * 
     * @return the value of the text parameter.
     */
    public String getText()
    {
        return parameterMap.get( TEXT );
    }
    
    /**
     * Gets the value of the timeRange parameter.
     * 
     * @return the value of the timeRange parameter.
     */
    public int getTimeRange()
    {
        return parameterMap.get( TIME_RANGE );
    }
    
    /**
     * Gets the value of the fastForwardMultiplier parameter.
     * 
     * @return the value of the fastForwardMultiplier parameter.
     */
    public int getFastForwardMultiplier()
    {
        return parameterMap.get( FAST_FORWARD_MULTIPLIER );
    }
    
    /**
     * Gets the value of the refresh parameter.
     * 
     * @return the value of the refresh parameter.
     */
    public int getRefresh()
    {
        return parameterMap.get( REFRESH );
    }
    
    /**
     * A builder that takes in all the optional values for ReplayPicCGI and produces a ReplayPicCGI when build() is
     * called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can
     * only have build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its
     * values after calling build() will cause an IllegalStateException.
     */
    public static final class Builder extends AbstractPicCGI.AbstractBuilder<Builder>
    {
        /**
         * Sets the control parameter in the builder.
         * 
         * @param control
         *        the value to store as the control parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if control is null.
         */
        public Builder control( final Control control )
        {
            CheckParameters.areNotNull( control );
            return set( CONTROL, control );
        }
        
        /**
         * Sets the gmtTime parameter in the builder.
         * 
         * @param gmtTime
         *        the value to store as the gmtTime parameter.
         * @return the Builder
         */
        public Builder gmtTime( final int gmtTime )
        {
            return set( GMT_TIME, gmtTime );
        }
        
        /**
         * Sets the localTime parameter in the builder.
         * 
         * @param localTime
         *        the value to store as the localTime parameter.
         * @return the Builder
         */
        public Builder localTime( final int localTime )
        {
            return set( LOCAL_TIME, localTime );
        }
        
        /**
         * Sets the text parameter in the builder.
         * 
         * @param text
         *        the value to store as the text parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if text is null.
         */
        public Builder text( final String text )
        {
            CheckParameters.areNotNull( text );
            return set( TEXT, text );
        }
        
        /**
         * Sets the timeRange parameter in the builder.
         * 
         * @param timeRange
         *        the value to store as the timeRange parameter.
         * @return the Builder
         */
        public Builder timeRange( final int timeRange )
        {
            return set( TIME_RANGE, timeRange );
        }
        
        /**
         * Sets the fastForwardMultiplier parameter in the builder.
         * 
         * @param fastForwardMultiplier
         *        the value to store as the fastForwardMultiplier parameter.
         * @return the Builder
         */
        public Builder fastForwardMultiplier( final int fastForwardMultiplier )
        {
            return set( FAST_FORWARD_MULTIPLIER, fastForwardMultiplier );
        }
        
        /**
         * Sets the refresh parameter in the builder.
         * 
         * @param refresh
         *        the value to store as the refresh parameter.
         * @return the Builder
         */
        public Builder refresh( final int refresh )
        {
            return set( REFRESH, refresh );
        }
        
        /**
         * Constructs a ReplayPicCGI with the values from this Builder.
         * 
         * @return a ReplayPicCGI containing the values from this Builder
         * @throws IllegalStateException
         *         if the Builder has already been built.
         */
        public ReplayPicCGI build()
        {
            try
            {
                return new ReplayPicCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
            }
        }
        
        /**
         * Returns this Builder.
         * 
         * @return this Builder
         */
        @Override
        Builder self()
        {
            return this;
        }
        
    }
    static
    {
        params.add( CONTROL );
        params.add( GMT_TIME );
        params.add( LOCAL_TIME );
        params.add( TEXT );
        params.add( TIME_RANGE );
        params.add( FAST_FORWARD_MULTIPLIER );
        params.add( REFRESH );
        params.addAll( commonParams );
    }
    /**
     * Various video playback modes.
     */
    public static enum Control
    {
        /**
         * Play video forwards at its original speed.
         */
        PLAY,
        
        /**
         * Play video forwards at a speed controlled by the fast-forward multiplier.
         */
        FFWD,
        
        /**
         * Play video backwards.
         */
        RWND,
        
        /**
         * Stop playing video.
         */
        STOP;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of Control if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * 
         * @return a Function that parses a String into a Control
         */
        static Function<String, Option<Control>> fromStringFunction()
        {
            return new Function<String, Option<Control>>()
            {
                @Override
                public Option<Control> apply(String s )
                {
                    for ( final Control element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Control element " );
                }
            };
        }
        
    }
    
    /**
     * A Function that parses a timestamp either in HH:mm:ss:dd:MM:yy format or as a Julian time.
     * 
     * @return a Function that parses a timestamp.
     */
    static Function<String, Option<Integer>> fromTimeFunction()
    {
        return new Function<String, Option<Integer>>()
        {
            @Override
            public Option<Integer> apply(String s )
            {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yy");
                try
                {
                    return Option.getFullOption((int)(format.parse(s).getTime()/1000));
                }
                catch (java.text.ParseException e)
                {
                    try
                    {
                        return Option.getFullOption(Integer.parseInt(s));
                    }
                    catch (NumberFormatException e2)
                    {
                        return Option.getEmptyOption("Cannot parse " + s + " as a timestamp.");
                    }
                }
            }
        };
    }
    
    /**
     * Converts this ReplayPicCGI into a String containing a URL beginning with 
     * /replay_pic.cgi? and containing the supplied parameters.
     * @return a String containing a URL beginning with /replay_pic.cgi? and containing the supplied parameters
     */
    @Override
    public String toString()
    {
        return "/replay_pic.cgi?" + parameterMap.toURLParameters( params );
    }
    
    /**
     * Converts a String containing a URL describing a /replay_pic.cgi? request into a ReplayPicCGI.
     * 
     * @param string
     *        the String to parse.
     * @return A ReplayPicCGI describing the specified URL
     * @throws IllegalArgumentException
     *         if the String cannot be parsed into a ReplayPicCGI.
     * @throws NullPointerException
     *         if string is null.
     */
    public static ReplayPicCGI fromString( final String string )
    {
        CheckParameters.areNotNull( string );
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into a ReplayPicCGI." );
        }
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }
        return new ReplayPicCGI( map.get() );
    }
    
}

