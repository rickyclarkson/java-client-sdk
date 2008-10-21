package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds and parses vparts.cgi requests.
 */
public final class VPartsCGI
{
    private static final ParameterDescription<Format, Format> FORMAT =
            ParameterDescription.parameterDisallowing( Format.HTML, ParameterDescription.parameterWithDefault(
                    "format", Format.CSV, StringConversion.convenientPartial( Format.fromString ) ) );

    private static final ParameterDescription<Mode, Mode> MODE =
            ParameterDescription.parameterWithDefault( "mode", Mode.READ,
                    StringConversion.convenientPartial( Mode.fromString ) );

    private static final ParameterDescription<Integer, Integer> TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "time", 0,
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> RANGE =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "range",
                    Integer.MAX_VALUE, StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> EXPIRY =
            ParameterDescription.parameterWithDefault( "expiry", 0, StringConversion.integer() );

    private static final ParameterDescription<Boolean, Boolean> WATERMARK =
            ParameterDescription.parameterWithDefault( "watermark", false, StringConversion.bool() );

    private static final ParameterDescription<Integer, Integer> WMARKSTEP =
            ParameterDescription.parameterWithBounds( 1, 256, ParameterDescription.parameterWithDefault(
                    "wmarkstepParam", 1, StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> LIST_LENGTH =
            ParameterDescription.parameterWithDefault( "listlength", 100, StringConversion.integer() );

    private static final ParameterDescription<DirectoryPathFormat, DirectoryPathFormat> PATH_STYLE =
            ParameterDescription.parameterWithDefault( "pathstyle", DirectoryPathFormat.SHORT,
                    StringConversion.convenientPartial( DirectoryPathFormat.fromString ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<ParameterDescription<?, ?>> parameterDescriptions =
            new ArrayList<ParameterDescription<?, ?>>()
            {
                {
                    add( FORMAT );
                    add( MODE );
                    add( TIME );
                    add( RANGE );
                    add( EXPIRY );
                    add( WATERMARK );
                    add( WMARKSTEP );
                    add( LIST_LENGTH );
                    add( PATH_STYLE );
                }
            };

    /**
     * Parses a vparts.cgi request into a VPartsCGI.
     * 
     * @param url
     *        the vparts request to parse.
     * @return a VPartsCGI containing the values from the URL.
     */
    public static VPartsCGI fromString( final String url )
    {
        final Option<ParameterMap> map = ParameterMap.fromURL( url, parameterDescriptions );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( url + " cannot be parsed into a VPartsCGI, because " + map.reason() );
        }

        return new VPartsCGI( map.get() );
    }

    private final ParameterMap builtMap;

    private VPartsCGI( final ParameterMap builtMap )
    {
        this.builtMap = builtMap;
    }

    /**
     * The expiry time for partitions, in Julianised GMT (only used in protect
     * mode).
     * 
     * @return the expiry time for partitions.
     */
    public int getExpiry()
    {
        return builtMap.get( EXPIRY );
    }

    /**
     * The output format.
     * 
     * @return the output format.
     */
    public Format getFormat()
    {
        return builtMap.get( FORMAT );
    }

    /**
     * The maximum number of elements in the list.
     * 
     * @return the maximum number of elements in the list.
     */
    public int getListlength()
    {
        return builtMap.get( LIST_LENGTH );
    }

    /**
     * The function of the cgi call.
     * 
     * @return the {@link Mode} that the CGI call is in.
     */
    public Mode getMode()
    {
        return builtMap.get( MODE );
    }

    /**
     * The format of directory paths, short or long.
     * 
     * @return the format of directory paths, short or long.
     */
    public DirectoryPathFormat getPathstyle()
    {
        return builtMap.get( PATH_STYLE );
    }

    /**
     * The timespan to search in seconds.
     * 
     * @return the timespan to search in seconds.
     */
    public int getRange()
    {
        return builtMap.get( RANGE );
    }

    /**
     * Julianised GMT time to start the search from.
     * 
     * @return the Julianised GMT time to start the search from.
     */
    public int getTime()
    {
        return builtMap.get( TIME );
    }

    /**
     * Whether or not to generate watermark codes (read mode only).
     * 
     * @return true if the server should generate watermark codes (read mode
     *         only), false otherwise.
     */
    public boolean getWatermark()
    {
        return builtMap.get( WATERMARK );
    }

    /**
     * The step size to be used in watermark code generation.
     * 
     * @return the step size to be used in watermark code generation.
     */
    public int getWatermarkStep()
    {
        return builtMap.get( WMARKSTEP );
    }

    @Override
    public String toString()
    {
        return "/vparts.cgi?format=" + builtMap.get( FORMAT ) + "&"
                + builtMap.toURLParameters( Lists.remove( parameterDescriptions, FORMAT ) );
    }

    /**
     * A Builder for constructing VPartsCGIs.
     */
    public static final class Builder
    {
        private Option<ParameterMap> real = Option.getFullOption( new ParameterMap() );

        /**
         * Constructs a VPartsCGI containing the stored parameters.
         * 
         * @return a VPartsCGI containing the stored parameters.
         */
        public VPartsCGI build()
        {
            try
            {
                return new VPartsCGI( real.get() );
            }
            finally
            {
                real = Option.getEmptyOption( "This Builder has already had build() called on it" );
            }
        }

        /**
         * Sets the expiry time for partitions, in Julianised GMT (only used in
         * protect mode).
         * 
         * @param expiry
         *        the expiry time for partitions
         * @return the Builder.
         */
        public Builder expiry( final int expiry )
        {
            return set( EXPIRY, expiry );
        }

        /**
         * Determines the output format (defaults to CSV in this API, which is
         * different to the servers' defaults).
         * 
         * @param format
         *        the output format.
         * @return the Builder.
         */
        public Builder format( final Format format )
        {
            return set( FORMAT, format );
        }

        /**
         * Determines the maximum number of elements in the list.
         * 
         * @param listlength
         *        the maximum number of elements in the list.
         * @return the Builder.
         */
        public Builder listlength( final int listlength )
        {
            return set( LIST_LENGTH, listlength );
        }

        /**
         * Sets the function of the cgi call.
         * 
         * @param mode
         *        the {@link Mode} to use.
         * @return the Builder.
         */
        public Builder mode( final Mode mode )
        {
            return set( MODE, mode );
        }

        // TODO decide whether to restrict expiry to a VPartsCGI with protect
        // set.

        /**
         * Determines the format of directory paths, short or long.
         * 
         * @param style
         *        the format of directory paths.
         * @return the Builder.
         */
        public Builder pathstyle( final DirectoryPathFormat style )
        {
            return set( PATH_STYLE, style );
        }

        /**
         * Time span to search in seconds.
         * 
         * @param range
         *        the time span to search in seconds.
         * @return the Builder.
         */
        public Builder range( final int range )
        {
            return set( RANGE, range );
        }

        /**
         * Sets the Julianised GMT start time for database search.
         * 
         * @param time
         *        the start time for database search.
         * @return the Builder.
         */
        public Builder time( final int time )
        {
            return set( TIME, time );
        }

        // TODO decide whether to restrict watermark to read mode.
        /**
         * Sets whether to generate watermark codes (read mode only).
         * 
         * @param watermark
         *        true if the server should generate watermark codes, false
         *        otherwise.
         * @return the Builder.
         */
        public Builder watermark( final boolean watermark )
        {
            return set( WATERMARK, watermark );
        }

        // TODO make this and the watermark parameter be mutually 'inclusive'.
        /**
         * defines the step size to be used in watermark code generation, used
         * in conjunction with the watermark parameter.
         * 
         * @param step
         *        the step size to be used in watermark code generation.
         * @return the Builder.
         */
        public Builder watermarkStep( final int step )
        {
            return set( WMARKSTEP, step );
        }

        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( real.isEmpty() )
            {
                throw new IllegalStateException( "The Builder has already been built (build() has been called on it)." );
            }

            real = real.map( new Function<ParameterMap, ParameterMap>()
            {
                @Override
                public ParameterMap apply( final ParameterMap map )
                {
                    return map.set( parameter, value );
                }
            } );
            return this;
        }
    }
}
