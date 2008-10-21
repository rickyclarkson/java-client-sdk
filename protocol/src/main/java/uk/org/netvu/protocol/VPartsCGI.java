package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A parameter list for a vparts.cgi query. Use {@link VPartsCGI.Builder} to
 * construct a VPartsCGI, or {@link VPartsCGI#fromURL(String)}. A vparts.cgi
 * query has three modes, 'read', 'protect' and 'reinitialise'. Some of the
 * parameters in this class are only applicable to certain of those. Those are
 * documented in each appropriate method in {@link VPartsCGI.Builder}.
 */
public final class VPartsCGI
{
    private static final ParameterDescription<Format, Format> FORMAT =
            ParameterDescription.parameterDisallowing( Format.HTML, ParameterDescription.parameterWithDefault(
                                                                                                              "format", Format.CSV, StringConversion.convenientPartial( Format.functionFromStringToFormat() ) ) );

    private static final ParameterDescription<Mode, Mode> MODE =
            ParameterDescription.parameterWithDefault( "mode", Mode.READ,
                    StringConversion.convenientPartial( Mode.functionFromStringToMode() ) );

    private static final ParameterDescription<Integer, Integer> TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "time", 0,
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> RANGE =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "range",
                    Integer.MAX_VALUE, StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> EXPIRY =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "expiry", 0,
                    StringConversion.integer() ) );

    private static final ParameterDescription<Boolean, Boolean> WATERMARK =
            ParameterDescription.parameterWithDefault( "watermark", false, StringConversion.bool() );

    private static final ParameterDescription<Integer, Integer> WMARKSTEP =
            ParameterDescription.parameterWithBounds( 1, 256, ParameterDescription.parameterWithDefault(
                    "wmarkstepParam", 1, StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> LIST_LENGTH =
            ParameterDescription.parameterWithDefault( "listlength", 100, StringConversion.integer() );

    private static final ParameterDescription<DirectoryPathFormat, DirectoryPathFormat> PATH_STYLE = pathStyle();

    private static final List<ParameterDescription<?, ?>> parameterDescriptions =
            new ArrayList<ParameterDescription<?, ?>>()
            {
                {
                    // this is an anonymous intialiser - it is creating a new
                    // ArrayList and adding values to it inline.
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
     * @throws NullPointerException
     *         if url is null.
     * @throws IllegalArgumentException
     *         if the URL cannot be parsed into a VPartsCGI.
     * @return a VPartsCGI containing the values from the URL.
     */
    public static VPartsCGI fromURL( final String url )
    {
        CheckParameters.areNotNull( url );

        final Option<ParameterMap> map = ParameterMap.fromURL( url, parameterDescriptions );

        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( url + " cannot be parsed into a VPartsCGI, because " + map.reason() );
        }

        return new VPartsCGI( map.get() );
    }

    private static ParameterDescription<DirectoryPathFormat, DirectoryPathFormat> pathStyle()
    {
        final Function<String, Option<DirectoryPathFormat>> stringToDirectoryPathFormat =
                DirectoryPathFormat.functionFromStringToDirectoryPathFormat();

        return ParameterDescription.parameterWithDefault( "pathstyle", DirectoryPathFormat.SHORT,
                StringConversion.convenientPartial( stringToDirectoryPathFormat ) );
    }

    private final ParameterMap builtMap;

    /**
     * Constructs a VPartsCGI, using the values from the specified ParameterMap.
     * 
     * @param builtMap
     *        the ParameterMap to get values from.
     * @throws NullPointerException
     *         if builtMap is null.
     */
    private VPartsCGI( final ParameterMap builtMap )
    {
        CheckParameters.areNotNull( builtMap );
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
    public int getListLength()
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

    /**
     * Gives /vparts.cgi? followed by the values stored in this VPartsCGI, as
     * URL parameters.
     */
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
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        /**
         * Constructs a Builder ready to take in the values needed for
         * constructing a VPartsCGI.
         */
        public Builder()
        {
        }

        /**
         * Constructs a VPartsCGI containing the stored parameters.
         * 
         * @return a VPartsCGI containing the stored parameters.
         */
        public VPartsCGI build()
        {
            try
            {
                return new VPartsCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already had build() called on it" );
            }
        }

        /**
         * Sets the expiry time for partitions, in Julianised GMT (only used in
         * protect mode). Must not be negative.
         * 
         * @param expiry
         *        the expiry time for partitions
         * @throws IllegalArgumentException
         *         if expiry is negative.
         * @return the Builder.
         */
        public Builder expiry( final int expiry )
        {
            return set( EXPIRY, expiry );
        }

        /**
         * Determines the output format (defaults to CSV in this API, which is
         * different to the servers' defaults). Note that Format.HTML is
         * disallowed, as per the Video Server Specification.
         * 
         * @param format
         *        the output format.
         * @throws NullPointerException
         *         if format is null.
         * @return the Builder.
         */
        public Builder format( final Format format )
        {
            return set( FORMAT, format );
        }

        /**
         * Determines the maximum number of elements in the list.
         * 
         * @param listLength
         *        the maximum number of elements in the list.
         * @return the Builder.
         */
        public Builder listLength( final int listLength )
        {
            return set( LIST_LENGTH, listLength );
        }

        /**
         * Sets the function of the cgi call.
         * 
         * @param mode
         *        the {@link Mode} to use.
         * @throws NullPointerException
         *         if mode is null.
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
         * @throws NullPointerException
         *         if style is null.
         * @return the Builder.
         */
        public Builder pathStyle( final DirectoryPathFormat style )
        {
            return set( PATH_STYLE, style );
        }

        /**
         * Time span to search in seconds.
         * 
         * @param range
         *        the time span to search in seconds.
         * @throws IllegalArgumentException
         *         if range is negative.
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
         * @throws IllegalArgumentException
         *         if time is negative.
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
         * Defines the step size to be used in watermark code generation, used
         * in conjunction with the watermark parameter.
         * 
         * @param step
         *        the step size to be used in watermark code generation.
         * @throws IllegalArgumentException
         *         if step is not between 1 and 256 inclusive.
         * @return the Builder.
         */
        public Builder watermarkStep( final int step )
        {
            return set( WMARKSTEP, step );
        }

        /**
         * Sets the value of a parameter to a given value, and returns the
         * Builder.
         * 
         * @param <T>
         *        the input type of the specified parameter.
         * @param parameter
         *        the parameter to set a value for.
         * @param value
         *        the value to give that parameter.
         * @return the Builder.
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }

            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }
    }

    /**
     * Specifies the format of directory paths returned by the server.
     */
    public enum DirectoryPathFormat
    {
        /**
         * The directory paths returned by the server are short.
         */
        SHORT,

        /**
         * The directory paths returned by the server are long.
         */
        LONG;

        /**
         * A Function that, given a String, will produce an Option containing
         * SHORT or LONG if the String matches it (ignoring case), and an empty
         * Option otherwise.
         * 
         * @return a Function that parses a String into a DirectoryPathFormat.
         */
        static Function<String, Option<DirectoryPathFormat>> functionFromStringToDirectoryPathFormat()
        {
            return new Function<String, Option<DirectoryPathFormat>>()
            {
                @Override
                public Option<DirectoryPathFormat> apply( final String t )
                {
                    try
                    {
                        return Option.getFullOption( DirectoryPathFormat.valueOf( t.toUpperCase( Locale.ENGLISH ) ) );
                    }
                    catch ( final IllegalArgumentException exception )
                    {
                        return Option.getEmptyOption( t + " is not a valid DirectoryPathFormat" );
                    }
                }
            };
        }

        /**
         * Gives a lowercase String representation of this DirectoryPathFormat.
         */
        @Override
        public String toString()
        {
            return super.toString().toLowerCase( Locale.ENGLISH );
        }
    }

    /**
     * Determines the function of the CGI call.
     */
    public enum Mode
    {
        /**
         * Supplies a javascript array or comma separated variable list of video
         * partition information.
         */
        READ,

        /**
         * Manual protect/unprotect of video partitions. The time/range/expiry
         * parameters are used to set/clear expiration times on all stored video
         * data in the specified time range. If the expiry time is specified as
         * 0 then any existing protection is removed
         */
        PROTECT,

        /**
         * Reset video partitions.
         */
        REINITIALISE;

        /**
         * Converts a String to a Mode if it matches a Mode's name
         * (case-insensitive), returning it in an Option if it does, and
         * returning an empty Option otherwise.
         * 
         * @return a Function that parses a String into a Mode.
         */
        static Function<String, Option<Mode>> functionFromStringToMode()
        {
            return new Function<String, Option<Mode>>()
            {
                @Override
                public Option<Mode> apply( final String s )
                {
                    try
                    {
                        return Option.getFullOption( Mode.valueOf( s.toUpperCase( Locale.ENGLISH ) ) );
                    }
                    catch ( final IllegalArgumentException exception )
                    {
                        return Option.getEmptyOption( s + " is not a valid Mode" );
                    }
                }
            };
        }

        /**
         * Gives a lowercase String representation of this Locale.
         */
        @Override
        public String toString()
        {
            return super.toString().toLowerCase( Locale.ENGLISH );
        }
    }
}
