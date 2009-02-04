package uk.org.netvu.protocol;

import java.util.List;
import java.util.ArrayList;
import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;   


/**
 * A parameter list for a display_pic.cgi query.
 * Use {@link DisplayPicCGI.Builder} to construct a DisplayPicCGI, or {@link DisplayPicCGI#fromString(String)}
 * See the <a href="http://adwiki.ad-group.adh/attachment/wiki/DevelopmentSite/AD/Client/Vserve%20Specification.pdf">Video
 * Server Specification</a> for more detail on the display_pic.cgi query.
 */
public final class DisplayPicCGI extends AbstractPicCGI
{
    /**
     * Constructs a DisplayPicCGI, using the values from the specified ParameterMap.
     * @param parameterMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if parameterMap is null.
     */
    DisplayPicCGI( final ParameterMap parameterMap )
    {
        super( parameterMap );
    }
    
    /**
     * All the parameter specifications, used in parsing URLs.
     */
    private static final List<ParameterDescription<?, ?>> params = 
            new ArrayList<ParameterDescription<?, ?>>();
    
    /**
     * The specification of the dwellTime parameter.
     */
    private static final ParameterDescription<Integer, Integer> DWELL_TIME = 
            ParameterDescription.parameter( "dwell", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the preselector parameter.
     */
    private static final ParameterDescription<Integer, Integer> PRESELECTOR = 
            ParameterDescription.parameter( "presel", StringConversion.integer() )
            .withDefault( 0 ).withBounds( 0, 3, Num.integer );
    
    /**
     * The specification of the channel parameter.
     */
    private static final ParameterDescription<Integer, Integer> CHANNEL = 
            ParameterDescription.parameter( "channel", StringConversion.integer() )
            .withDefault( -1 ).withBounds( -1, 1, Num.integer );
    
    /**
     * The specification of the quantisationFactor parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR = 
            ParameterDescription.parameter( "forcedq", StringConversion.integer() )
            .withDefault( 0 ).withBounds( 0, 255, Num.integer ).disallowing( 1 );
    
    /**
     * The specification of the quantisationFactorForTelemetryImages parameter.
     */
    private static final ParameterDescription<Integer, Integer> QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES = 
            ParameterDescription.parameter( "telemQ", StringConversion.integer() )
            .withDefault( -1 ).withBounds( -1, Integer.MAX_VALUE, Num.integer );
    
    /**
     * The specification of the audioMode parameter.
     */
    private static final ParameterDescription<AudioMode, AudioMode> AUDIO_MODE = 
            ParameterDescription.parameter( "audmode",
            StringConversion.convenientPartial( AudioMode.fromStringFunction() ) )
            .withDefault( AudioMode.UDP );
    
    /**
     * The specification of the picturesPerSecond parameter.
     */
    private static final ParameterDescription<Integer, Integer> PICTURES_PER_SECOND = 
            ParameterDescription.parameter( "pps", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the mp4Bitrate parameter.
     */
    private static final ParameterDescription<Integer, Integer> MP4_BITRATE = 
            ParameterDescription.parameter( "mp4rate", StringConversion.integer() ).withDefault( 0 );
    
    /**
     * The specification of the proxyPriority parameter.
     */
    private static final ParameterDescription<Integer, Integer> PROXY_PRIORITY = 
            ParameterDescription.parameter( "proxypri", StringConversion.integer() ).withDefault( 1 );
    
    /**
     * The specification of the bufferCount parameter.
     */
    private static final ParameterDescription<Integer, Integer> BUFFER_COUNT = 
            ParameterDescription.parameter( "nbuffers", StringConversion.integer() )
            .withDefault( 0 ).notNegative( Num.integer );
    
    /**
     * Gets the value of the dwellTime parameter.
     * 
     * @return the value of the dwellTime parameter.
     */
    public int getDwellTime()
    {
        return parameterMap.get( DWELL_TIME );
    }
    
    /**
     * Gets the value of the preselector parameter.
     * 
     * @return the value of the preselector parameter.
     */
    public int getPreselector()
    {
        return parameterMap.get( PRESELECTOR );
    }
    
    /**
     * Gets the value of the channel parameter.
     * 
     * @return the value of the channel parameter.
     */
    public int getChannel()
    {
        return parameterMap.get( CHANNEL );
    }
    
    /**
     * Gets the value of the quantisationFactor parameter.
     * 
     * @return the value of the quantisationFactor parameter.
     */
    public int getQuantisationFactor()
    {
        return parameterMap.get( QUANTISATION_FACTOR );
    }
    
    /**
     * Gets the value of the quantisationFactorForTelemetryImages parameter.
     * 
     * @return the value of the quantisationFactorForTelemetryImages parameter.
     */
    public int getQuantisationFactorForTelemetryImages()
    {
        return parameterMap.get( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES );
    }
    
    /**
     * Gets the value of the audioMode parameter.
     * 
     * @return the value of the audioMode parameter.
     */
    public AudioMode getAudioMode()
    {
        return parameterMap.get( AUDIO_MODE );
    }
    
    /**
     * Gets the value of the picturesPerSecond parameter.
     * 
     * @return the value of the picturesPerSecond parameter.
     */
    public int getPicturesPerSecond()
    {
        return parameterMap.get( PICTURES_PER_SECOND );
    }
    
    /**
     * Gets the value of the mp4Bitrate parameter.
     * 
     * @return the value of the mp4Bitrate parameter.
     */
    public int getMp4Bitrate()
    {
        return parameterMap.get( MP4_BITRATE );
    }
    
    /**
     * Gets the value of the proxyPriority parameter.
     * 
     * @return the value of the proxyPriority parameter.
     */
    public int getProxyPriority()
    {
        return parameterMap.get( PROXY_PRIORITY );
    }
    
    /**
     * Gets the value of the bufferCount parameter.
     * 
     * @return the value of the bufferCount parameter.
     */
    public int getBufferCount()
    {
        return parameterMap.get( BUFFER_COUNT );
    }
    
    /**
     * A builder that takes in all the optional values for DisplayPicCGI and produces a DisplayPicCGI when build() is
     * called.  Each parameter must be supplied no more than once.  A Builder can only be built once; that is, it can
     * only have build() called on it once.  Calling it a second time will cause an IllegalStateException.  Setting its
     * values after calling build() will cause an IllegalStateException.
     */
    public static final class Builder extends AbstractPicCGI.AbstractBuilder<Builder>
    {
        /**
         * Sets the dwellTime parameter in the builder.
         * 
         * @param dwellTime
         *        the value to store as the dwellTime parameter.
         * @return the Builder
         */
        public Builder dwellTime( final int dwellTime )
        {
            return set( DWELL_TIME, dwellTime );
        }
        
        /**
         * Sets the preselector parameter in the builder.
         * 
         * @param preselector
         *        the value to store as the preselector parameter.
         * @return the Builder
         */
        public Builder preselector( final int preselector )
        {
            return set( PRESELECTOR, preselector );
        }
        
        /**
         * Sets the channel parameter in the builder.
         * 
         * @param channel
         *        the value to store as the channel parameter.
         * @return the Builder
         */
        public Builder channel( final int channel )
        {
            return set( CHANNEL, channel );
        }
        
        /**
         * Sets the quantisationFactor parameter in the builder.
         * 
         * @param quantisationFactor
         *        the value to store as the quantisationFactor parameter.
         * @return the Builder
         */
        public Builder quantisationFactor( final int quantisationFactor )
        {
            return set( QUANTISATION_FACTOR, quantisationFactor );
        }
        
        /**
         * Sets the quantisationFactorForTelemetryImages parameter in the builder.
         * 
         * @param quantisationFactorForTelemetryImages
         *        the value to store as the quantisationFactorForTelemetryImages parameter.
         * @return the Builder
         */
        public Builder quantisationFactorForTelemetryImages( final int quantisationFactorForTelemetryImages )
        {
            return set( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES, quantisationFactorForTelemetryImages );
        }
        
        /**
         * Sets the audioMode parameter in the builder.
         * 
         * @param audioMode
         *        the value to store as the audioMode parameter.
         * @return the Builder
         * @throws NullPointerException
         *         if audioMode is null.
         */
        public Builder audioMode( final AudioMode audioMode )
        {
            CheckParameters.areNotNull( audioMode );
            return set( AUDIO_MODE, audioMode );
        }
        
        /**
         * Sets the picturesPerSecond parameter in the builder.
         * 
         * @param picturesPerSecond
         *        the value to store as the picturesPerSecond parameter.
         * @return the Builder
         */
        public Builder picturesPerSecond( final int picturesPerSecond )
        {
            return set( PICTURES_PER_SECOND, picturesPerSecond );
        }
        
        /**
         * Sets the mp4Bitrate parameter in the builder.
         * 
         * @param mp4Bitrate
         *        the value to store as the mp4Bitrate parameter.
         * @return the Builder
         */
        public Builder mp4Bitrate( final int mp4Bitrate )
        {
            return set( MP4_BITRATE, mp4Bitrate );
        }
        
        /**
         * Sets the proxyPriority parameter in the builder.
         * 
         * @param proxyPriority
         *        the value to store as the proxyPriority parameter.
         * @return the Builder
         */
        public Builder proxyPriority( final int proxyPriority )
        {
            return set( PROXY_PRIORITY, proxyPriority );
        }
        
        /**
         * Sets the bufferCount parameter in the builder.
         * 
         * @param bufferCount
         *        the value to store as the bufferCount parameter.
         * @return the Builder
         */
        public Builder bufferCount( final int bufferCount )
        {
            return set( BUFFER_COUNT, bufferCount );
        }
        
        /**
         * Constructs a DisplayPicCGI with the values from this Builder.
         * 
         * @return a DisplayPicCGI containing the values from this Builder
         * @throws IllegalStateException
         *         if the Builder has already been built.
         */
        public DisplayPicCGI build()
        {
            try
            {
                return new DisplayPicCGI( parameterMap.get() );
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
        Builder self()
        {
            return this;
        }
        
    }
    static
    {
        params.add( DWELL_TIME );
        params.add( PRESELECTOR );
        params.add( CHANNEL );
        params.add( QUANTISATION_FACTOR );
        params.add( QUANTISATION_FACTOR_FOR_TELEMETRY_IMAGES );
        params.add( AUDIO_MODE );
        params.add( PICTURES_PER_SECOND );
        params.add( MP4_BITRATE );
        params.add( PROXY_PRIORITY );
        params.add( BUFFER_COUNT );
        params.addAll( commonParams );
    }
    /**
     * The possible mechanisms for returning audio data.
     */
    public static enum AudioMode
    {
        /**
         * Out of band UDP data.
         */
        UDP,
        
        /**
         * In-band data interleaved with images.
         */
        INLINE;
        
        /**
         * A Function that, given a String, will produce an Option containing
         * a member of AudioMode if the passed-in String matches it (ignoring case), and an empty
         * Option otherwise.
         * 
         * @return a Function that parses a String into a AudioMode
         */
        static Function<String, Option<AudioMode>> fromStringFunction()
        {
            return new Function<String, Option<AudioMode>>()
            {
                @Override
                public Option<AudioMode> apply(String s )
                {
                    for ( final AudioMode element: values() )
                    {
                        if ( element.toString().equalsIgnoreCase( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid AudioMode element " );
                }
            };
        }
        
    }
    
    /**
     * Converts this DisplayPicCGI into a String containing a URL beginning with 
     * /display_pic.cgi? and containing the supplied parameters.
     * @return a String containing a URL beginning with /display_pic.cgi? and containing the supplied parameters
     */
    @Override
    public String toString()
    {
        return "/display_pic.cgi?" + parameterMap.toURLParameters( params );
    }
    
    /**
     * Converts a String containing a URL describing a /display_pic.cgi? request into a DisplayPicCGI.
     * 
     * @param string
     *        the String to parse.
     * @return A DisplayPicCGI describing the specified URL
     * @throws IllegalArgumentException
     *         if the String cannot be parsed into a DisplayPicCGI.
     * @throws NullPointerException
     *         if string is null.
     */
    public static DisplayPicCGI fromString( final String string )
    {
        CheckParameters.areNotNull( string );
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into a DisplayPicCGI." );
        }
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }
        return new DisplayPicCGI( map.get() );
    }
    
}

