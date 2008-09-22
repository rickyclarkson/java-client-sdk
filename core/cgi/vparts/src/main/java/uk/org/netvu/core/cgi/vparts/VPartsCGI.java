package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Option.someRef;
import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.not;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;

/**
 * Builds and parses vparts.cgi requests.
 */
public final class VPartsCGI
{
    private static final Parameter<Format, Format> FORMAT = not(
            Format.HTML,
            param(
                    "format",
                    "Determines output format.  js is for JavaScript, csv for comma separated variables",
                    Format.CSV, Format.fromString ) );

    private static final Parameter<Mode, Mode> MODE = param( "mode",
            "Determines the function of the cgi call", Mode.READ,
            Mode.fromString );

    private static final Parameter<Integer, Integer> TIME = notNegative( param(
            "time", "Julianised GMT start time for database search", 0,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> RANGE = notNegative( param(
            "range", "Timespan to search in seconds.", Integer.MAX_VALUE,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> EXPIRY = param(
            "expiry",
            "Sets the expiry time for partitions, used in protect mode; Julianised GMT",
            0, Conversion.stringToInt );

    private static final Parameter<Boolean, Boolean> WATERMARK = param(
            "watermark",
            "Tells the CGI to generate watermark codes in read mode", false,
            someRef( Conversion.stringToBoolean ) );

    private static final Parameter<Integer, Integer> WMARKSTEP = bound( 1, 256,
            param( "wmarkstepParam",
                    "Defines the step size to be used in watermark.", 1,
                    Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> LIST_LENGTH = param(
            "listlength", "Maximum number of entries in the list", 100,
            Conversion.stringToInt );

    private static final Parameter<DirectoryPathFormat, DirectoryPathFormat> PATH_STYLE = param(
            "pathstyle", "Format of directory paths.",
            DirectoryPathFormat.SHORT, DirectoryPathFormat.fromString );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
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

    private final ParameterMap builtMap;

    private VPartsCGI( final ParameterMap builtMap )
    {
        this.builtMap = builtMap;
    }

    /**
     * A Builder for constructing VPartsCGIs.
     */
    public static final class Builder
    {
        private Option<ParameterMap> parameterMap = new Option.Some<ParameterMap>(
                new ParameterMap() );

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
                parameterMap = new Option.None<ParameterMap>();
            }
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
            parameterMap = parameterMap.map( ParameterMap.withRef( MODE, mode ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( TIME, time ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( RANGE, range ) );
            return this;
        }

        // TODO decide whether to restrict expiry to a VPartsCGI with protect
        // set.

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
            parameterMap = parameterMap.map( ParameterMap.withRef( EXPIRY,
                    expiry ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( WATERMARK,
                    watermark ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( WMARKSTEP,
                    step ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( FORMAT,
                    format ) );
            return this;
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
            parameterMap = parameterMap.map( ParameterMap.withRef( LIST_LENGTH,
                    listlength ) );
            return this;
        }

        /**
         * Determines the format of directory paths, short or long.
         * 
         * @param style
         *        the format of directory paths.
         * @return the Builder.
         */
        public Builder pathstyle( final DirectoryPathFormat style )
        {
            parameterMap = parameterMap.map( ParameterMap.withRef( PATH_STYLE,
                    style ) );
            return this;
        }
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
     * Julianised GMT time to start the search from.
     * 
     * @return the Julianised GMT time to start the search from.
     */
    public int getTime()
    {
        return builtMap.get( TIME );
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
     * The format of directory paths, short or long.
     * 
     * @return the format of directory paths, short or long.
     */
    public DirectoryPathFormat getPathstyle()
    {
        return builtMap.get( PATH_STYLE );
    }

    @Override
    public String toString()
    {
        return "/vparts.cgi?format=" + builtMap.get( FORMAT ) + "&"
                + builtMap.toURLParameters( Lists.remove( params, FORMAT ) );
    }

    /**
     * Parses a vparts.cgi request into a VPartsCGI.
     * 
     * @param url
     *        the vparts request to parse.
     * @return a VPartsCGI containing the values from the URL.
     */
    public static VPartsCGI fromString( final String url )
    {
        return new VPartsCGI( ParameterMap.fromURL( url, params ) );
    }
}
